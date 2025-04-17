package com.pumpkin.tool.entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ControllerUtils {
    /**
     * @param request 前端发送的请求
     * @param response 后台发送的响应
     * @throws ServletException
     * @throws IOException
     */
    public static void setCorHeader(HttpServletRequest request, HttpServletResponse response){
        String origin = request.getHeader("Origin");
        response.addHeader("Access-Control-Allow-Origin", origin);
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.addHeader("Access-Control-Max-Age", "3600");
        response.addHeader("Access-Control-Allow-Headers", "content-type");
        response.addHeader("Access-Control-Allow-Credentials", "true");
    }
}
