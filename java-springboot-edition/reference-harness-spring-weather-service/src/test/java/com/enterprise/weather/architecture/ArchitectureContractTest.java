package com.enterprise.weather.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Phase 2 Locked Architecture Contract (ArchUnit).
 * Prevents parallel Phase 3 subagents from violating package boundaries or injecting repositories into controllers.
 */
@AnalyzeClasses(packages = "com.enterprise.weather", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureContractTest {

    @ArchTest
    static final ArchRule LAYERED_ARCHITECTURE_CONTRACT = layeredArchitecture()
        .consideringAllDependencies()
        .layer("Controller").definedBy("..controller..")
        .layer("Service").definedBy("..service..")
        .layer("Client").definedBy("..client..")
        .layer("Repository").definedBy("..repository..")
        .layer("DTO").definedBy("..dto..")
        .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
        .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
        .whereLayer("Client").mayOnlyBeAccessedByLayers("Service")
        .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service");

    @ArchTest
    static final ArchRule CONTROLLERS_MUST_NOT_ACCESS_REPOSITORIES_OR_CLIENTS = noClasses()
        .that().resideInAPackage("..controller..")
        .should().dependOnClassesThat().resideInAnyPackage("..repository..", "..client..");
}
