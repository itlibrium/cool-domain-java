import com.itlibrium.cooldomain.app.FinishInterventionCommand
import com.itlibrium.cooldomain.app.FinishInterventionHandler
import com.itlibrium.cooldomain.crud.EquipmentModel
import com.itlibrium.cooldomain.crud.PricingCategory
import com.itlibrium.cooldomain.domain.Contract
import com.itlibrium.cooldomain.domain.ContractImpl
import com.itlibrium.cooldomain.domain.ContractLimits
import com.itlibrium.cooldomain.domain.ContractRepository
import com.itlibrium.cooldomain.domain.Intervention
import com.itlibrium.cooldomain.domain.InterventionDuration
import com.itlibrium.cooldomain.domain.InterventionRepository
import com.itlibrium.cooldomain.domain.Money
import com.itlibrium.cooldomain.domain.PricePolicyFactory
import com.itlibrium.cooldomain.domain.PricePolicyFactoryImpl
import com.itlibrium.cooldomain.domain.PricingService
import com.itlibrium.cooldomain.domain.PricingServiceImpl
import com.itlibrium.cooldomain.domain.ServiceAction
import com.itlibrium.cooldomain.domain.ServiceActionType
import spock.lang.Specification

import static com.itlibrium.cooldomain.domain.ServiceActionType.*

class AcceptanceTest extends Specification {

    def _interventionRepository = getSingleValueInterventionRepo();
    def _contractRepository = getSingleValueContractRepo();

    def _minPrice = 200;
    def _pricePerHour = 100;
    def _duration = 3;

    def _sparePartsPrices = [:];
    def _usedParts = [];
    def _actionType = Repair;

    def _interventionsLimit = 0;
    def _interventionsUsed = 0;
    def _sparePartsLimit = 0;
    def _sparePartsLimitUsed = 0;


    def "Labour cost calculated correctly"() {
        given:
            _minPrice = minPrice;
            _pricePerHour = pricePerHour;
            _sparePartsPrices = [ 158 : Money.fromDouble(30),
                                 333 : Money.fromDouble(40)];
            _duration = duration;
            _usedParts = usedPartsIds;
            _actionType = actionType;
        when:
            serviceIsFinished();
        then:
            getInterventionPrice() == Money.fromDouble(overallPrice);
        where:
            minPrice | pricePerHour | duration | usedPartsIds| actionType     || overallPrice
               200   |   100        |  4       |  []         | Review         ||     400
               200   |   100        |  1       |  [158, 333] | Repair         ||     270
               200   |   100        |  1       |  []         | Review         ||     200
               200   |   100        |  0       |  [333]      | Repair         ||     240
               300   |   100        |  0       |  []         | Review         ||     300
               400   |   0          |  10      |  [158]      | Repair         ||     430
               400   |   0          |  1       |  []         | WarrantyReview ||     0
               200   |   100        |  2       |  [158]      | WarrantyRepair ||     0

    }

    def "Contracts allows certain number of free interventions"() {
        given:
            _interventionsLimit = i10nsLimit;
            _interventionsUsed = i10nsUsedBefore;
            _actionType = actionType;
        when:
            serviceIsFinished();
        then:
            getInterventionPrice() == Money.fromDouble(total);
            getInterventionsLimitUsed() == i10sUsedAfter;
        where:
            i10nsLimit | i10nsUsedBefore | actionType       || total | i10sUsedAfter
                 2     |    0            |  Repair          || 0     |     1
                 2     |    0            |  WarrantyReview  || 0     |     0
                 2     |    2            |  Repair          || 300   |     2
                 0     |    0            |  Repair          || 300   |     0
    }


    def "Contracts allows spare parts usage up to a limit"() {
        given:
            _sparePartsPrices = [ 5 : Money.fromDouble(50),
                                  2 : Money.fromDouble(20)]
            _sparePartsLimit = partsLimit
            _sparePartsLimitUsed = limitUsedBefore
            labourIsFree()
            _usedParts = usedParts;
            _actionType = actionType;
        when:
            serviceIsFinished();
        then:
            getInterventionPrice() == Money.fromDouble(total);
            getSparePartsLimitUsed() == Money.fromDouble(limitUsedAfter);
        where:
            partsLimit | limitUsedBefore | actionType       | usedParts    || total | limitUsedAfter
                 100   |    0            |  Repair          |  [5,5]       || 0     |     100
                 100   |    0            |  Repair          |  [5,5,2]     || 20    |     100
                 100   |    20           |  Repair          |  [2]         || 0     |     40
                 100   |    50           |  WarrantyRepair  |  [5]         || 0     |     50
                 0     |    0            |  Repair          |  [5,2]       || 70    |     0

    }

    private void labourIsFree() {
        _pricePerHour = 0;
        _minPrice = 0;
    }

    void serviceIsFinished() {
        setupContractData();
        FinishInterventionHandler handler =
                getFinishInterventionHandler(_minPrice, _pricePerHour, _sparePartsPrices );
        FinishInterventionCommand finishInterventionCommand =
                getFinishInterventionCommand(_duration, _usedParts, _actionType);
        handler.finish(finishInterventionCommand);
    }

    void setupContractData() {
        def contract = new ContractImpl(
                _interventionsLimit, Money.fromDouble(_sparePartsLimit),
                _interventionsUsed, Money.fromDouble(_sparePartsLimitUsed)
        );
        _contractRepository.save(contract);
    }


    FinishInterventionHandler getFinishInterventionHandler(double minPrice, double pricePerHour, Map<Integer, Money> partsPrices) {
        PricePolicyFactoryImpl.CrmFacade crmFacade =
                getCrmFacade(BigDecimal.valueOf(minPrice), BigDecimal.valueOf(pricePerHour), 100);
        PricePolicyFactoryImpl.SparePartsFacade sparePartsFacade = getSparePartsFacade(partsPrices);
        PricePolicyFactory pricePolicyFactory = new PricePolicyFactoryImpl(crmFacade, sparePartsFacade);
        PricingService pricingService = new PricingServiceImpl(pricePolicyFactory);
        return new FinishInterventionHandler(_interventionRepository, _contractRepository, pricingService);
    }

    FinishInterventionCommand getFinishInterventionCommand(int duration, List<Integer> usedPartsIds,
                                                           ServiceActionType serviceActionType) {
        ServiceAction serviceAction =
                new ServiceAction(serviceActionType, InterventionDuration.FromHours(duration), usedPartsIds );
        return new FinishInterventionCommand(1, [serviceAction]);
    }

    Money getInterventionPrice() {
       return _interventionRepository.get(1).getPrice();
    }

    Money getSparePartsLimitUsed() {
        getContractLimits().getSparePartsCostLimit().getUsed();
    }

    int getInterventionsLimitUsed() {
        getContractLimits().getFreeInterventionsLimit().getUsed();
    }

    private ContractLimits getContractLimits() {
        _contractRepository.getForClient(1).getContractLimits()
    }

    PricePolicyFactoryImpl.CrmFacade getCrmFacade(BigDecimal minimalValue, BigDecimal pricePerHour,
                                                  int freeInterventionTimeLimit) {
        PricingCategory pricingCategory = new PricingCategory(1, "Test", minimalValue, pricePerHour);
        return new PricePolicyFactoryImpl.CrmFacade() {
            @Override
            PricingCategory getPricingCategoryForClient(int clientId) {
                return pricingCategory;
            }

            @Override
            EquipmentModel GetEquipmentModelForClient(int clientId) {
                return new EquipmentModel(1, "TestEquipment", pricingCategory, freeInterventionTimeLimit);
            }
        }
    }

    PricePolicyFactoryImpl.SparePartsFacade getSparePartsFacade(Map<Integer, Money> pricesMap) {
        return new PricePolicyFactoryImpl.SparePartsFacade() {
            @Override
            Map<Integer, Money> getPrices() {
                return pricesMap;
            }
        }
    }

    private InterventionRepository getSingleValueInterventionRepo() {
        new InterventionRepository() {
            private Intervention cachedIntervention = Intervention.createFor(1,1,
                    [Repair, Review, WarrantyRepair, WarrantyReview]);;

            @Override
            Intervention get(int id) {
                return cachedIntervention;
            }

            @Override
            void save(Intervention intervention) {
                cachedIntervention = intervention;
            }
        }
    }

    private ContractRepository getSingleValueContractRepo() {
        new ContractRepository() {
            private Contract cachedContract = new ContractImpl(
                    0, Money.fromDouble(0),
                    0, Money.fromDouble(0)
            );

            @Override
            Contract getForClient(int id) {
                return cachedContract;
            }

            @Override
            void save(Contract contract) {
                cachedContract = contract;
            }
        }
    }
}

