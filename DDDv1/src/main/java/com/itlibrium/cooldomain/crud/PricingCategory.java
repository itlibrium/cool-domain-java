package com.itlibrium.cooldomain.crud;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PricingCategory
{
    private final int id;
    private final String name;
    private final BigDecimal minPrice;
    private final BigDecimal pricePerHour;
}
