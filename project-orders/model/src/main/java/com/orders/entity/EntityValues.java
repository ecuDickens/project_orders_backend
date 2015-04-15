package com.orders.entity;

public class EntityValues {

    public static class Order {
        private Order() {}

        public static class Status {
            // The order has been created but not billed.
            public static String NEW = "NEW";

            // The order has been billed and an invoice created.
            public static String BILLED = "BILLED";
        }
    }
}
