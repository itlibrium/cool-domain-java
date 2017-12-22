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

class AcceptanceTest extends Specification {


    def interventionRepository = getSingleValueRepo();

    def "Labour cost calculated correctly"() {
        given:
            FinishInterventionHandler handler =
                    getFinishInterventionHandler(minPrice, pricePerHour, [ 158 : Money.fromDouble(30),
                                                                           333 : Money.fromDouble(40)]);
        when:
            FinishInterventionCommand finishInterventionCommand = getFinishInterventionCommand(duration, usedPartsIds);
            handler.finish(finishInterventionCommand);
        then:
            getInterventionPrice() == Money.fromDouble(overallPrice);
        where:
            minPrice | pricePerHour | duration | usedPartsIds|| overallPrice
               200   |   100        |  4       |      []     ||     400
               200   |   100        |  1       |  [158, 333] ||     270
               200   |   100        |  1       |  []         ||     200
               200   |   100        |  0       |  [333]      ||     240
               300   |   100        |  0       |  []         ||     300
               400   |   0          |  10      |  [158]      ||     430

    }

    FinishInterventionHandler getFinishInterventionHandler(double minPrice, double pricePerHour, Map<Integer, Money> partsPrices) {
        PricePolicyFactoryImpl.CrmFacade crmFacade = getCrmFacade(BigDecimal.valueOf(minPrice), BigDecimal.valueOf(pricePerHour));
        PricePolicyFactoryImpl.SparePartsFacade sparePartsFacade = getSparePartsFacade(partsPrices);
        PricePolicyFactory pricePolicyFactory = new PricePolicyFactoryImpl(crmFacade, sparePartsFacade);
        return new FinishInterventionHandler(interventionRepository, pricePolicyFactory);
    }

    FinishInterventionCommand getFinishInterventionCommand(int duration, List<Integer> usedPartsIds) {
        ServiceAction serviceAction =
                new ServiceAction(ServiceActionType.Repair, InterventionDuration.FromHours(duration), usedPartsIds );
        return new FinishInterventionCommand(1, [serviceAction]);
    }

    Money getInterventionPrice() {
       return interventionRepository.get(1).getPrice();
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
            private Intervention cachedIntervention = Intervention.createFor(1,1,[ServiceActionType.Repair]);;

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

