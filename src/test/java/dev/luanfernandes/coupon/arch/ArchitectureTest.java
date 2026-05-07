package dev.luanfernandes.coupon.arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import jakarta.inject.Inject;
import org.springframework.beans.factory.annotation.Autowired;

@AnalyzeClasses(packages = "dev.luanfernandes.coupon", importOptions = DoNotIncludeTests.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    void classesNamedControllerShouldResideInControllerPackage(JavaClasses classes) {
        ArchRuleDefinition.classes()
                .that().haveSimpleNameEndingWith("Controller")
                .should().resideInAPackage("..controller..")
                .check(classes);
    }

    @ArchTest
    void classesNamedRepositoryShouldResideInRepositoryPackage(JavaClasses classes) {
        ArchRuleDefinition.classes()
                .that().haveSimpleNameEndingWith("Repository")
                .should().resideInAPackage("..repository..")
                .check(classes);
    }

    @ArchTest
    void classesNamedServiceShouldResideInServicePackage(JavaClasses classes) {
        ArchRuleDefinition.classes()
                .that().haveSimpleNameEndingWith("Service")
                .should().resideInAPackage("..service..")
                .check(classes);
    }

    @ArchTest
    void classesNamedConfigShouldResideInConfigPackage(JavaClasses classes) {
        ArchRuleDefinition.classes()
                .that().haveSimpleNameEndingWith("Config")
                .should().resideInAPackage("..config..")
                .check(classes);
    }

    @ArchTest
    void classesNamedConstantsShouldResideInConstantsPackage(JavaClasses classes) {
        ArchRuleDefinition.classes()
                .that().haveSimpleNameEndingWith("Constants")
                .should().resideInAPackage("..constants..")
                .check(classes);
    }

    @ArchTest
    void classesNamedMapperShouldResideInMapperPackage(JavaClasses classes) {
        ArchRuleDefinition.classes()
                .that().haveSimpleNameEndingWith("Mapper")
                .should().resideInAPackage("..dto.mapper..")
                .check(classes);
    }

    @ArchTest
    void classesNamedRequestShouldResideInRequestPackage(JavaClasses classes) {
        ArchRuleDefinition.classes()
                .that().haveSimpleNameEndingWith("Request")
                .should().resideInAPackage("..dto.request..")
                .check(classes);
    }

    @ArchTest
    void classesNamedResponseShouldResideInResponsePackage(JavaClasses classes) {
        ArchRuleDefinition.classes()
                .that().haveSimpleNameEndingWith("Response")
                .should().resideInAPackage("..dto.response..")
                .check(classes);
    }

    @ArchTest
    void noProductionClassShouldUseFieldInjection(JavaClasses classes) {
        ArchRuleDefinition.noFields()
                .that().areDeclaredInClassesThat().haveSimpleNameNotEndingWith("Test")
                .should().beAnnotatedWith(Inject.class)
                .orShould().beAnnotatedWith(Autowired.class)
                .check(classes);
    }

    @ArchTest
    void serviceLayerShouldNotDependOnControllerLayer(JavaClasses classes) {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..service..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .check(classes);
    }

    @ArchTest
    void controllerLayerShouldNotDependDirectlyOnDomainEntities(JavaClasses classes) {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..domain.entity..")
                .check(classes);
    }

    @ArchTest
    void domainLayerShouldNotDependOnDtoLayer(JavaClasses classes) {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..dto..")
                .check(classes);
    }

    @ArchTest
    void domainLayerShouldNotDependOnControllerLayer(JavaClasses classes) {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .check(classes);
    }

    @ArchTest
    void repositoryLayerShouldNotDependOnControllerLayer(JavaClasses classes) {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..repository..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .check(classes);
    }

    @ArchTest
    void repositoryLayerShouldNotDependOnDtoLayer(JavaClasses classes) {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..repository..")
                .should().dependOnClassesThat().resideInAPackage("..dto..")
                .check(classes);
    }

    @ArchTest
    void requestAndResponseClassesShouldNotResideInDomainPackage(JavaClasses classes) {
        ArchRuleDefinition.noClasses()
                .that().haveSimpleNameEndingWith("Request")
                .or().haveSimpleNameEndingWith("Response")
                .should().resideInAPackage("..domain..")
                .check(classes);
    }

    @ArchTest
    void domainEntityClassesShouldNotBeNamedLikeDtos(JavaClasses classes) {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..domain.entity..")
                .should().haveSimpleNameNotEndingWith("Request")
                .andShould().haveSimpleNameNotEndingWith("Response")
                .check(classes);
    }
}
