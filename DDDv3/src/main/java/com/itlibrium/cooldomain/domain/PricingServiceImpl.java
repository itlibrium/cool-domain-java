package com.itlibrium.cooldomain.domain;

import java.util.Collection;

public class PricingServiceImpl implements PricingService {
    private PricePolicyFactory pricePolicyFactory;

    public PricingServiceImpl(PricePolicyFactory pricePolicyFactory) {
        this.pricePolicyFactory = pricePolicyFactory;
    }

    public InterventionPricing GetPricingFor(Intervention intervention,
                                             Collection<ServiceAction> serviceActions,
                                             ContractLimits contractLimits) {

        PricePolicy pricePolicy = pricePolicyFactory.createFor(intervention);
        Money totalPrice = Money.ZERO;
        for (ServiceAction serviceAction : serviceActions) {
            PricingContext context = new PricingContext(serviceAction, contractLimits);
            Pricing pricing = pricePolicy.apply(context);
            contractLimits = pricing.getContractLimits();
            totalPrice = Money.sum(totalPrice, pricing.getValue());
        }
        return new InterventionPricing(totalPrice, contractLimits);
    }
}
