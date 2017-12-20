package com.itlibrium.cooldomain.domain;

public interface IPricePolicyFactory {
    PricePolicy createFor(Intervention intervention);
}
