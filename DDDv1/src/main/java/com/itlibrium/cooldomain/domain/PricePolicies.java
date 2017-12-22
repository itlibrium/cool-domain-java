package com.itlibrium.cooldomain.domain;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class PricePolicies {
    public static PricePolicy labour(Money pricePerHour, Money minPrice) {
        return serviceAction ->
            Money.max(
                Money.multiply(pricePerHour, serviceAction.getDuration().getHours()),
                minPrice);
    }

    public static PricePolicy sparePartsCost(Map<Integer, Money> sparePartPrices) {
        return serviceAction ->
            serviceAction.getSparePartIds().stream()
                .map(sparePartPrices::get)
                .reduce(Money.ZERO, Money::sum);
    }

    public static PricePolicy sum(PricePolicy... policies) {
        return aggregate(Arrays.asList(policies), Money::sum);
    }

    public static PricePolicy aggregate(Collection<PricePolicy> policies, BiFunction<Money, Money, Money> valueAggregator) {
        return serviceAction ->
            policies.stream()
                .map(policy -> policy.apply(serviceAction))
                .reduce(Money.ZERO, valueAggregator::apply);
    }
}
