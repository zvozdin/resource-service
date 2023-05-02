package com.example.resourceservice.cucumber.component;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        glue = "com.example.resourceservice.cucumber.component",
        features = "src/test/resources/features/component",
        plugin = {"junit:target/failsafe-reports/cucumber-report-IT.xml", "pretty"}
)
public class CucumberComponentTest {

}
