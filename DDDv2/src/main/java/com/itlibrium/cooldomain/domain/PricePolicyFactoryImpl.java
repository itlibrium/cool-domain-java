package com.itlibrium.cooldomain.domain;

import com.itlibrium.cooldomain.crud.EquipmentModel;
import com.itlibrium.cooldomain.crud.PricingCategory;

import java.util.Map;

public class PricePolicyFactoryImpl implements PricePolicyFactory {
    private final CrmFacade crmFacade;
    private final SparePartsFacade sparePartsFacade;

    public PricePolicyFactoryImpl(CrmFacade crmFacade, SparePartsFacade sparePartsFacade) {
        this.crmFacade = crmFacade;
        this.sparePartsFacade = sparePartsFacade;
    }

    public interface CrmFacade {
        PricingCategory getPricingCategoryForClient(int clientId);

        EquipmentModel GetEquipmentModelForClient(int clientId);
    }

    public interface SparePartsFacade {
        Map<Integer, Money> getPrices();
    }

    public PricePolicy createFor(Intervention intervention) {
        PricingCategory pricingCategory = crmFacade.getPricingCategoryForClient(intervention.getClientId());
        Map<Integer, Money> sparePartPrices = sparePartsFacade.getPrices();

        return PricePolicies.sum(
                PricePolicies.when(
                        serviceAction ->
                                serviceAction.getType() == ServiceActionType.Repair ||
                                        serviceAction.getType() == ServiceActionType.Review,
                        PricePolicies.sum(
                                PricePolicies.labour(
                                        Money.fromDecimal(pricingCategory.getPricePerHour()),
                                        Money.fromDecimal(pricingCategory.getMinPrice())),
                                PricePolicies.sparePartsCost(sparePartPrices))),
                PricePolicies.when(
                        serviceAction ->
                                serviceAction.getType() == ServiceActionType.WarrantyRepair ||
                                        serviceAction.getType() == ServiceActionType.WarrantyReview,
                        PricePolicies.free()));
    }
}