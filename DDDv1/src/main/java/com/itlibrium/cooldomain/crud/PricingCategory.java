package com.itlibrium.cooldomain.crud;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PricingCategory
{
    private int Id;
    private String Name;
    private BigDecimal MinPrice;
    private BigDecimal PricePerHour;
}
