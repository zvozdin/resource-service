package com.example.resourceservice.cucumber.e2e;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        glue = "com.example.resourceservice.cucumber.e2e",
        features = "src/test/resources/features/e2e",
        plugin = {"junit:target/failsafe-reports/cucumber-report-IT.xml", "pretty"}
)
public class CucumberE2E {

}
