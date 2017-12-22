package com.itlibrium.cooldomain.domain;

public interface PricePolicyFactory {
    PricePolicy createFor(Intervention intervention);
}
