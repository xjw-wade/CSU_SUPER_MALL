package com.csu.mall.common;

public class CONSTANT {
    public static final String LOGIN_USER = "loginUser";
    /**
     redis中session的key
     */
    public static final long REDIS_SESSION_EXPIRE = 60 * 60;


    public interface USER_FIELDS{
        String USERNAME = "username";
        String EMAIL = "email";
        String PHONE = "phone";
    }

    public interface ROLE{
        int CUSTOMER = 0; //前台普通用户
        int ADMIN = 1; //后台管理员
    }
}
