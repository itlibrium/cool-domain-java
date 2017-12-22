package com.itlibrium.cooldomain.crud;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class EquipmentModel
{
    private final  int id;
    private final String name;
    private final PricingCategory pricingCategory;
    private final int freeInterventionTimeLimit;
}
