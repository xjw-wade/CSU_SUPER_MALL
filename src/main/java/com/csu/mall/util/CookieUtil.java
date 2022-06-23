package com.csu.mall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CookieUtil {
    //www.csustore.com
    //.csustore.com
    //user.csustore.com
    //static.csustore.com
    //www.csustore.com/product/list

    private final static String COOKIE_DOMAIN = "jtang.cloud";
    private final static String COOKIE_NAME = "csumall_token";

    public static void writeLoginToken(HttpServletResponse response, String token){
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30);

        cookie.setHttpOnly(true);

        log.info("Write CookieName:{} , CookieValue:{}", COOKIE_NAME,token);

        response.addCookie(cookie);
    }

    public static String readLoginToken(HttpServletRequest request){
        Cookie [] cookies = request.getCookies();
        if (cookies != null){
            for(Cookie cookie : cookies){
                log.info("read cookieName:{}, cookieValue:{}" , cookie.getName(),cookie.getValue());
                if(StringUtils.equals(cookie.getName(), COOKIE_NAME)){
                    log.info("get cookieName:{}, cookieValue:{}" , cookie.getName(),cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void deleteLoginToken(HttpServletRequest request, HttpServletResponse response){
        Cookie [] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(StringUtils.equals(cookie.getName(), COOKIE_NAME)){
                    cookie.setDomain(COOKIE_DOMAIN);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    log.info("delete cookieName:{}, cookieValue:{}" , cookie.getName(),cookie.getValue());
                    response.addCookie(cookie);
                    return;
                }
            }
        }
    }

}
