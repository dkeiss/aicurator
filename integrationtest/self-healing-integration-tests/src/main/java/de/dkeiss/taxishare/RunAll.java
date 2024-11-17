package de.dkeiss.taxishare;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"classpath:"},
        plugin = {"html:target/cucumber-html-report.html", "json:target/cucumber-json-report.json" },
        glue = {"de.dkeiss.taxishare.steps"},
        tags = "not @ignore")
public class RunAll {
}
