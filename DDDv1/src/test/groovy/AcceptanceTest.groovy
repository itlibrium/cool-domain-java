import com.itlibrium.cooldomain.app.FinishInterventionCommand
import com.itlibrium.cooldomain.app.FinishInterventionHandler
import com.itlibrium.cooldomain.crud.EquipmentModel
import com.itlibrium.cooldomain.crud.PricingCategory
import com.itlibrium.cooldomain.domain.Intervention
import com.itlibrium.cooldomain.domain.InterventionDuration
import com.itlibrium.cooldomain.domain.InterventionRepository
import com.itlibrium.cooldomain.domain.Money
import com.itlibrium.cooldomain.domain.PricePolicyFactory
import com.itlibrium.cooldomain.domain.PricePolicyFactoryImpl
import com.itlibrium.cooldomain.domain.ServiceAction
import com.itlibrium.cooldomain.domain.ServiceActionType
import spock.lang.Specification

import static com.itlibrium.cooldomain.domain.ServiceActionType.*

class AcceptanceTest extends Specification {

    def _interventionRepository = getSingleValueRepo();

    def _minPrice = 200;
    def _pricePerHour = 100;
    def _sparePartsPrices = [:];

    def _duration = 1;
    def _usedParts = [];
    def _actionType = Review;

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

    }

    void serviceIsFinished() {
        FinishInterventionHandler handler =
                getFinishInterventionHandler(_minPrice, _pricePerHour, _sparePartsPrices );
        FinishInterventionCommand finishInterventionCommand =
                getFinishInterventionCommand(_duration, _usedParts, _actionType);
        handler.finish(finishInterventionCommand);
    }


    FinishInterventionHandler getFinishInterventionHandler(double minPrice, double pricePerHour, Map<Integer, Money> partsPrices) {
        PricePolicyFactoryImpl.CrmFacade crmFacade = getCrmFacade(BigDecimal.valueOf(minPrice), BigDecimal.valueOf(pricePerHour));
        PricePolicyFactoryImpl.SparePartsFacade sparePartsFacade = getSparePartsFacade(partsPrices);
        PricePolicyFactory pricePolicyFactory = new PricePolicyFactoryImpl(crmFacade, sparePartsFacade);
        return new FinishInterventionHandler(_interventionRepository, pricePolicyFactory);
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

    PricePolicyFactoryImpl.CrmFacade getCrmFacade(BigDecimal minimalValue, BigDecimal pricePerHour) {
        return new PricePolicyFactoryImpl.CrmFacade() {
            @Override
            PricingCategory getPricingCategoryForClient(int clientId) {
                return new PricingCategory(1, "Test", minimalValue, pricePerHour)
            }

            @Override
            EquipmentModel GetEquipmentModelForClient(int clientId) {
                return null
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

    private InterventionRepository getSingleValueRepo() {
        new InterventionRepository() {
            private Intervention cachedIntervention = Intervention.createFor(1,1,
                    [Repair, Review]);;

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
}

