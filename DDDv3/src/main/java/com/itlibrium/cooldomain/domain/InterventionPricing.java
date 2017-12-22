package com.itlibrium.cooldomain.domain;

import lombok.Getter;

public class InterventionPricing {
    @Getter
    private final  Money totalPrice;
    @Getter
    private final ContractLimits contractLimits;

    public InterventionPricing(Money totalPrice, ContractLimits contractLimits) {
        this.totalPrice = totalPrice;
        this.contractLimits = contractLimits;
    }
}
