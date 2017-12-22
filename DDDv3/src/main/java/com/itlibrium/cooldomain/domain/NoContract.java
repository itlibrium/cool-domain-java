package com.itlibrium.cooldomain.domain;

public class NoContract implements Contract
    {
        public ContractLimits getContractLimits() {
            return ContractLimits.NoContract();
        }
        public void addUsage(ContractLimits contractLimits) { }
    }
