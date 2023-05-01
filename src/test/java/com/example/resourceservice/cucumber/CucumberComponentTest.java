package com.example.resourceservice.cucumber;

import com.example.resourceservice.service.FileStorageService;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@AutoConfigureEmbeddedDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(Cucumber.class)
@CucumberContextConfiguration
@CucumberOptions(
        features = "src/test/resources/features",
        plugin = {"junit:target/failsafe-reports/cucumber-report-IT.xml", "pretty"}
)
public class CucumberComponentTest {

    @MockBean
    private FileStorageService fileStorageService;

}
