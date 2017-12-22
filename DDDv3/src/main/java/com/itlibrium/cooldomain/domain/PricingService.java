package com.itlibrium.cooldomain.domain;

import java.util.Collection;

public interface PricingService {
    InterventionPricing GetPricingFor(Intervention intervention, Collection<ServiceAction> serviceActions, ContractLimits contractLimits);
}
