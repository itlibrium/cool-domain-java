package com.itlibrium.cooldomain.app;

import com.itlibrium.cooldomain.BusinessException;
import com.itlibrium.cooldomain.domain.IInterventionRepository;
import com.itlibrium.cooldomain.domain.IPricePolicyFactory;
import com.itlibrium.cooldomain.domain.Intervention;
import com.itlibrium.cooldomain.domain.PricePolicy;

public class FinishInterventionHandler {
    private final IInterventionRepository interventionRepository;
    private final IPricePolicyFactory pricePolicyFactory;

    public FinishInterventionHandler(IInterventionRepository interventionRepository, IPricePolicyFactory pricePolicyFactory) {
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

