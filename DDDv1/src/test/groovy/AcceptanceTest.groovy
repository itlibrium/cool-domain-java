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

    def "Labour cost calculated correctly"() {
        given:
            InterventionRepository interventionRepository = getSingleValueRepo();
            PricePolicyFactoryImpl.CrmFacade crmFacade = getCrmFacade();
            PricePolicyFactoryImpl.SparePartsFacade sparePartsFacade = getSparePartsFacade();
            PricePolicyFactory pricePolicyFactory = new PricePolicyFactoryImpl(crmFacade, sparePartsFacade);
            FinishInterventionHandler handler = new FinishInterventionHandler(interventionRepository, pricePolicyFactory);
        when:
            ServiceAction sa1 = new ServiceAction(ServiceActionType.Repair, InterventionDuration.FromHours(4),[] );
            FinishInterventionCommand finishInterventionCommand = new FinishInterventionCommand(1, [sa1]);
            handler.finish(finishInterventionCommand);
        then:
            interventionRepository.get(1).getPrice() == Money.fromDecimal(BigDecimal.valueOf(400));



    }

    PricePolicyFactoryImpl.CrmFacade getCrmFacade() {
        return new PricePolicyFactoryImpl.CrmFacade() {
            @Override
            PricingCategory getPricingCategoryForClient(int clientId) {
                return new PricingCategory(1, "Test", BigDecimal.valueOf(200), BigDecimal.valueOf(100))
            }

            @Override
            EquipmentModel GetEquipmentModelForClient(int clientId) {
                return null
            }
        }
    }

    PricePolicyFactoryImpl.SparePartsFacade getSparePartsFacade() {
        return new PricePolicyFactoryImpl.SparePartsFacade() {
            @Override
            Map<Integer, Money> getPrices() {
                return [:];
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

