package com.itlibrium.cooldomain.crud;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class EquipmentModel
{
    private int id;
    private String name;
    private PricingCategory pricingCategory;
}
