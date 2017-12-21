package com.itlibrium.cooldomain.app;

import com.itlibrium.cooldomain.BusinessException;
import com.itlibrium.cooldomain.domain.InterventionRepository;
import com.itlibrium.cooldomain.domain.PricePolicyFactory;
import com.itlibrium.cooldomain.domain.Intervention;
import com.itlibrium.cooldomain.domain.PricePolicy;

public class FinishInterventionHandler {
    private final InterventionRepository interventionRepository;
    private final PricePolicyFactory pricePolicyFactory;

    public FinishInterventionHandler(InterventionRepository interventionRepository, PricePolicyFactory pricePolicyFactory) {
        this.interventionRepository = interventionRepository;
        this.pricePolicyFactory = pricePolicyFactory;
    }

    public void finish(FinishInterventionCommand command) throws BusinessException {
        Intervention intervention = interventionRepository.get(command.getInterventionId());
        PricePolicy pricePolicy = this.pricePolicyFactory.createFor(intervention);
        intervention.finish(command.getServiceActions(), pricePolicy);
        interventionRepository.save(intervention);
    }
}

