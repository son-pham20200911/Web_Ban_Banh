package com.example.web_ban_banh.Utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class AuthUtils {
    // Lấy email từ Authentication
    public static String getEmail(Authentication auth) {
        if (auth == null) return null;
        return auth.getName(); // thông thường là email
    }

    // ✅ Lấy username từ Authentication
    public static String getUsername(Authentication auth) {
        if (auth == null) return null;
        return auth.getName(); // auth.getName() thường trả username
    }

    // Check quyền (ADMIN, PARTNER, ...)
    public static boolean hasRole(Authentication auth, String role) {
        if (auth == null) return false;
        for (GrantedAuthority authority : auth.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_" + role)) return true;
        }
        return false;
    }

    public static boolean isAdmin(Authentication auth) {
        return hasRole(auth, "ADMIN");
    }

    public static boolean isPartner(Authentication auth) {
        return hasRole(auth, "PARTNER");
    }
}
