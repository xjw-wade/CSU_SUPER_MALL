package com.csu.mall.controller;

import com.csu.mall.common.CONSTANT;
import com.csu.mall.common.Result;
import com.csu.mall.dto.UpdateUserDTO;
import com.csu.mall.pojo.User;
import com.csu.mall.service.UserService;
import com.csu.mall.util.CookieUtil;
import com.csu.mall.util.Page4Navigator;
import com.csu.mall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    RedisUtil redisUtil;

    @PostMapping("login")
    public Result<User> login(@RequestParam @Validated @NotBlank(message = "用户名不能为空") String username,
                              @RequestParam @Validated @NotBlank(message = "密码不能为空") String password,
                              HttpSession session, HttpServletResponse httpServletResponse) {
        Result<User> response = userService.login(username, password);
        if (response.isSuccess()) {
            System.out.println("Session ID :" + session.getId());
            redisUtil.set(session.getId(), response.getData(), 3600);
            CookieUtil.writeLoginToken(httpServletResponse,session.getId());
        }
        return response;
    }

    @PostMapping("check_field")
    public Result<String> checkField(
            @RequestParam @Validated @NotBlank(message = "字段名不能为空") String fieldName,
            @RequestParam @Validated @NotBlank(message = "字段值不能为空") String fieldValue) {
        return userService.checkField(fieldName, fieldValue);
    }

    @PostMapping("register")
    public Result<String> register(@RequestBody @Valid User user){
        return userService.register(user);
    }

    @PostMapping("get_forget_question")
    public Result<String> getForgetQuestion(
            @RequestParam @Validated @NotBlank(message = "用户名不能为空") String username){
        return userService.getForgetQuestion(username);
    }

    @PostMapping("check_forget_answer")
    public Result<String> checkForgetAnswer(
            @RequestParam @Validated @NotBlank(message = "用户名不能为空") String username,
            @RequestParam @Validated @NotBlank(message = "忘记密码问题不能为空") String question,
            @RequestParam @Validated @NotBlank(message = "忘记密码问题答案不能为空") String answer){
        return userService.checkForgetAnswer(username,question,answer);
    }

    @PostMapping("reset_forget_password")
    public Result<String> resetForgetPassword(
            @RequestParam @Validated @NotBlank(message = "用户名不能为空") String username,
            @RequestParam @Validated @NotBlank(message = "新密码不能为空") String newPassword,
            @RequestParam @Validated @NotBlank(message = "重置密码token不能为空") String forgetToken){
        return userService.resetForgetPassword(username,newPassword,forgetToken);
    }

    @PostMapping("reset_password")
    public Result<String> resetPassword(
            @RequestParam @Validated @NotBlank(message = "旧密码不能为空") String oldPassword,
            @RequestParam @Validated @NotBlank(message = "新密码不能为空") String newPassword,
            HttpSession session){
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return Result.createForError("用户未登录");
        }
        return userService.resetPassword(oldPassword, newPassword,loginUser);
    }

    @PostMapping("get_user_detail")
    public Result<User> getUserDetail(HttpSession session){
        String sessionId = session.getId();
        User loginUser = (User)redisUtil.get(sessionId);
        if(loginUser == null){
            return Result.createForError("用户未登录");
        }
        return userService.getUserDetail(loginUser.getId());
    }

    @PostMapping("update_user_info")
    public Result<User> updateUserInfo(@RequestBody @Valid UpdateUserDTO updateUser,
                                               HttpSession session){
        String sessionId = session.getId();
        User loginUser = (User)redisUtil.get(sessionId);
        if(loginUser == null){
            return Result.createForError("用户未登录");
        }
        loginUser.setEmail(updateUser.getEmail());
        loginUser.setPhone(updateUser.getPhone());
        loginUser.setQuestion(updateUser.getQuestion());
        loginUser.setAnswer(updateUser.getAnswer());

        Result<String> result = userService.updateUserInfo(loginUser);
        if(result.isSuccess()){
            loginUser = userService.getUserDetail(loginUser.getId()).getData();
            session.setAttribute(CONSTANT.LOGIN_USER, loginUser);
            return Result.createForSuccess(loginUser);
        }
        return Result.createForError(result.getMessage());
    }

    @GetMapping("logout")
    public Result<String> logout(HttpSession session, HttpServletRequest request, HttpServletResponse response){
        String sessionId = session.getId();
        redisUtil.del(sessionId);
        CookieUtil.deleteLoginToken(request, response);
        return Result.createForSuccessMessage("退出登录成功");
    }


    //返回分页对象及其数据
    @GetMapping("/user_list")
    public Result<Page4Navigator<User>> pageBreak(
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "size", defaultValue = "5") int size){
        start = start<0?0:start;
        Page4Navigator<User>  page = userService.pageBreak(start,size,5);
        return Result.createForSuccess(page);
    }

    //通过姓名查询用户信息
    @GetMapping("/getByName")
    public Result<User> getByName(@RequestParam @Validated @NotBlank(message = "用户名不能为空") String username) {
        return Result.createForSuccess(userService.getByName(username));
    }
}
