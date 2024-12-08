package com.khemiri.InternManager.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookiesManager {
    public static void setCookie(HttpServletResponse response, String cookieName, String dataToInject,boolean httpOnly) {
        // Create an HTTP-only cookie and set the information
        Cookie cookie = new Cookie(cookieName, dataToInject);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(false);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static String getCookie(String cookieValue) {
        return cookieValue;
    }
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response,String cookieName,boolean httpOnly){
        Cookie deleteCookie = new Cookie(cookieName, null);
        deleteCookie.setMaxAge(0);
        deleteCookie.setHttpOnly(httpOnly);
        deleteCookie.setPath("/");
        response.addCookie(deleteCookie);
    }
}
