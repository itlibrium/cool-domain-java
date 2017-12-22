package com.itlibrium.cooldomain.domain;

import com.itlibrium.cooldomain.BusinessException;

public interface Contract
{
    ContractLimits getContractLimits();
    void addUsage(ContractLimits interventionPricingContractLimits) throws BusinessException;
}
