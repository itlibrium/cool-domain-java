package com.itlibrium.cooldomain.domain;


import com.itlibrium.cooldomain.BusinessException;

public class ContractImpl implements Contract {
    private int _freeInterventionsLimit;
    private Money _sparePartsCostLimit;

    private int _freeInterventionsLimitUsed;
    private Money _sparePartsCostLimitUsed;

    public ContractImpl(int freeInterventionsLimit, Money sparePartsCostLimit, int freeInterventionsLimitUsed, Money sparePartsCostLimitUsed) {
        _freeInterventionsLimit = freeInterventionsLimit;
        _sparePartsCostLimit = sparePartsCostLimit;
        _freeInterventionsLimitUsed = freeInterventionsLimitUsed;
        _sparePartsCostLimitUsed = sparePartsCostLimitUsed;
    }

    public ContractLimits getContractLimits() {
        return ContractLimits.CreateInitial(
                FreeInterventionsLimit.CreateInitial(_freeInterventionsLimit - _freeInterventionsLimitUsed),
                SparePartsCostLimit.CreateInitial(Money.subtract(_sparePartsCostLimit, _sparePartsCostLimitUsed)));
    }

    public void addUsage(ContractLimits contractLimits) throws BusinessException {
        if (contractLimits.getFreeInterventionsLimit().getUsed() > _freeInterventionsLimit - _freeInterventionsLimitUsed)
            throw new BusinessException("Brak darmowych interwencji do wykorzystania w ramach umowy serwisowej");

        if (contractLimits.getSparePartsCostLimit().getUsed().greaterThan(Money.subtract(_sparePartsCostLimit, _sparePartsCostLimitUsed)))
            throw new BusinessException("Koszt części zamiennych przekracza dopuszczalny limit w ramach umowy serwisowej");

        _freeInterventionsLimitUsed += contractLimits.getFreeInterventionsLimit().getUsed();
        _sparePartsCostLimitUsed = Money.sum(_sparePartsCostLimitUsed, contractLimits.getSparePartsCostLimit().getUsed());
    }
}
