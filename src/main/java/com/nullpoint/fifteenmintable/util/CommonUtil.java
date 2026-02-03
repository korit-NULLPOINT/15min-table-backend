package com.nullpoint.fifteenmintable.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class CommonUtil {
    // IP 가져오기 로직 - 재사용 필요시 util 폴더에 등록
    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        // ,(Comma) 분리 로직
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 여러 개일 경우 첫 번째 IP가 실제 클라이언트 IP입니다.
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            }
            return ip;
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}
