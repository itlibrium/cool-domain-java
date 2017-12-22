package com.itlibrium.cooldomain.app;

import com.itlibrium.cooldomain.BusinessException;
import com.itlibrium.cooldomain.domain.*;

public class FinishInterventionHandler {
    private final InterventionRepository interventionRepository;
    private final ContractRepository contractRepository;
    private final PricingService pricingService;

    public FinishInterventionHandler(InterventionRepository interventionRepository, ContractRepository contractRepository, PricingService pricingService) {
        this.interventionRepository = interventionRepository;
        this.contractRepository = contractRepository;
        this.pricingService = pricingService;
    }

    public void finish(FinishInterventionCommand command) throws BusinessException {
        Intervention intervention = interventionRepository.get(command.getInterventionId());

        Contract contract = contractRepository.getForClient(intervention.getClientId());
        ContractLimits contractLimits = contract.getContractLimits();

        InterventionPricing interventionPricing =
                pricingService.GetPricingFor(intervention, command.getServiceActions(), contractLimits);

        intervention.finish(command.getServiceActions(), interventionPricing);
        interventionRepository.save(intervention);

        contract.addUsage(interventionPricing.getContractLimits());
        contractRepository.save(contract);
    }
}
