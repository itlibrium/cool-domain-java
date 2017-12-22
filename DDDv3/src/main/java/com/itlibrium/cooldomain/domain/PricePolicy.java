package com.itlibrium.cooldomain.domain;

@FunctionalInterface
public interface PricePolicy {
    Pricing apply(PricingContext context);
}
