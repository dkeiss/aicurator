package de.dkeiss.taxishare.config.restassured;

import de.dkeiss.taxishare.store.ScenarioStore;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RequestInteractionFilter implements Filter {

    private final ScenarioStore scenarioStore;

    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        Response response = ctx.next(requestSpec, responseSpec);
        scenarioStore.setResponse(response);
        return response;
    }

}
