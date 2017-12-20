package com.itlibrium.cooldomain.domain;

import com.itlibrium.cooldomain.crud.EquipmentModel;
import com.itlibrium.cooldomain.crud.PricingCategory;

import java.util.Map;

public class PricePolicyFactory implements IPricePolicyFactory {
    private final ICrmFacade _crmFacade;
    private final ISparePartsFacade _sparePartsFacade;

    public PricePolicyFactory(ICrmFacade crmFacade, ISparePartsFacade sparePartsFacade) {
        _crmFacade = crmFacade;
        _sparePartsFacade = sparePartsFacade;
    }

    public interface ICrmFacade {
        PricingCategory getPricingCategoryForClient(int clientId);

        EquipmentModel GetEquipmentModelForClient(int clientId);
    }

    public interface ISparePartsFacade {
        Map<Integer, Money> getPrices();
    }

    public PricePolicy createFor(Intervention intervention) {
        PricingCategory pricingCategory = _crmFacade.getPricingCategoryForClient(intervention.getClientId());
        Map<Integer, Money> sparePartPrices = _sparePartsFacade.getPrices();

        return PricePolicies.sum(
            PricePolicies.labour(
                Money.fromDecimal(pricingCategory.getPricePerHour()),
                Money.fromDecimal(pricingCategory.getMinPrice())),
            PricePolicies.sparePartsCost(sparePartPrices));
    }
}