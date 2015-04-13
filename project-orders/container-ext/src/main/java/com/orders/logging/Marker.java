package com.orders.logging;

public enum Marker {
    /* Keywords for log records reporting monitored issues.               */
    ORDERS_ERROR_INTERNAL,
    ORDERS_ERROR_SERVICE_AUTH,
    ORDERS_ERROR_SERVICE_SOFTSWITCH,
    ORDERS_ERROR_SERVICE_JPA,
    ORDERS_ERROR_SERVICE_JERSEY;

    public static String insert(Marker marker, String record) {
        return marker + ": " + record;
    }
}
