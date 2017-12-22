package com.itlibrium.cooldomain.domain;

@FunctionalInterface
public interface PricePolicy {
    Money apply(ServiceAction serviceAction);
}
