package com.csu.mall.util;

import com.csu.mall.common.CONSTANT;
import com.csu.mall.pojo.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 过滤器，重置redis中session有效期
 */
@Configuration
@WebFilter(urlPatterns = "/*", filterName = "CORSFilter")
public class CORSFilter implements Filter {
    /**
     这里有一个坑，这里我们使用了RedisUtil大家发现这里并没有用@Autowird注解注入
     是因为注入不进来，和spring的启动顺序有关，我们需要在init方法中引入，如果没有引入就是空指针异常
     */
    private RedisUtil redisUtil;
    /**
     在这里获取ApplicationContext对象，通过name或者type获取redisUtil赋值给redis变量否则就是空指针
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
        redisUtil = (RedisUtil)context.getBean("redisUtil");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        //读取loginToken
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isNotBlank(loginToken)) {
            //从redis中获取
            User user = (User) redisUtil.get(loginToken);

            if (user != null) {
                //重置时间
                redisUtil.expire(loginToken, CONSTANT.REDIS_SESSION_EXPIRE);
            }
        }

        HttpServletResponse response = (HttpServletResponse) res;
        String origin = request.getHeader("origin");// 获取源站
        response.setHeader("Access-Control-Allow-Origin", origin); //设置cookie跨域
        //response.setHeader("Access-Control-Allow-Origin","*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PATCH, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        chain.doFilter(req, res);
    }
}

