package com.csu.mall.util;

import com.csu.mall.common.Result;
import com.csu.mall.common.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.List;

//设置一个异常处理器，主要是在处理删除父类信息的时候，因为外键约束的存在，而导致违反约束。
@ControllerAdvice
@RestController
public class GloabalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public String defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        e.printStackTrace();
        Class constraintViolationException = Class.forName("org.hibernate.exception.ConstraintViolationException");
        if(null!=e.getCause()  && constraintViolationException==e.getCause().getClass()) {
            return "违反了约束，多半是外键约束";
        }
        return e.getMessage();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleValidatedException(ConstraintViolationException exception){
        return Result.createForError(ResultCode.PARAM_ERROR.getCode(),exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public Result<String> handleValidException(MethodArgumentNotValidException exception){
        return Result.createForError(
                ResultCode.PARAM_ERROR.getCode(),formatValidErrorsMessage(exception.getAllErrors()));
    }

    private String formatValidErrorsMessage(List<ObjectError> errors){
        StringBuffer errorMessage = new StringBuffer();
        errors.forEach(error -> errorMessage.append(error.getDefaultMessage()).append(","));
        errorMessage.deleteCharAt(errorMessage.length()-1);
        return errorMessage.toString();
    }


}

