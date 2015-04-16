package com.orders.entity;

import com.google.common.collect.ImmutableSet;

public class EntityValues {

    public static class Order {
        private Order() {}

        public static class Status {
            private Status() {}

            // The order has been created but not billed.
            public static String NEW = "NEW";

            // The order has been billed and an invoice created.
            public static String BILLED = "BILLED";
        }
    }

    public static class Product {
        private Product() {}

        public static class Type {
            private Type() {}

            // The product is a one time charge.
            public static String ONE_TIME = "ONE TIME";

            // The product is a credit.
            public static String CREDIT = "CREDIT";

            public static ImmutableSet<String> VALID_TYPES = ImmutableSet.of(ONE_TIME, CREDIT);
        }
    }
}
