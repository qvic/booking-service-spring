package com.salon.booking.utility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

public final class RequestUtility {

    private RequestUtility() {

    }

    public static String getFullUrl(String url, HttpServletRequest request) {
        return request.getContextPath() + url;
    }

    public static int getRequiredIntParameter(String name, HttpServletRequest request) {
        return ParseUtility.parseIntOrThrow(request.getParameter(name),
                () -> new IllegalArgumentException(name + " is a required parameter"));
    }

    public static String getRequiredStringParameter(String name, HttpServletRequest request) {
        String parameter = request.getParameter(name);
        if (parameter == null) {
            throw new IllegalArgumentException(name + " is a required parameter");
        }

        return parameter;
    }

    public static Optional<Integer> getIntSessionAttribute(String name, HttpSession session) {
        return getSessionAttribute(name, session, Integer.class);
    }

    private static <T> Optional<T> getSessionAttribute(String name, HttpSession session, Class<T> tClass) {
        if (session == null) {
            return Optional.empty();
        }

        T attribute = tClass.cast(session.getAttribute(name));

        return Optional.ofNullable(attribute);
    }

    public static void removeSessionAttribute(String name, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(name);
        }
    }
}
