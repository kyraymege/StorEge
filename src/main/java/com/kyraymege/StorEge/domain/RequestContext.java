package com.kyraymege.StorEge.domain;

public class RequestContext {
    private static final ThreadLocal<Long> context = new ThreadLocal<>();

    private RequestContext() {

    }

    public static void start() {
        context.remove();
    }

    public static void setUserId(Long userId) {
        context.set(userId);
    }

    public static Long getUserId() {
        return context.get();
    }
}
