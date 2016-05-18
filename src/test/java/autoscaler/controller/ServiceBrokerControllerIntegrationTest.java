package autoscaler.controller;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import autoscaler.model.Plan;
import autoscaler.model.ServiceDefinition;
import autoscaler.model.fixture.CatalogFixture;
import autoscaler.model.fixture.PlanFixture;
import autoscaler.model.fixture.ServiceFixture;
import autoscaler.service.CatalogService;

public class ServiceBrokerControllerIntegrationTest {
	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	private MockMvc mockMvc;

	@InjectMocks
	private ServiceBrokerController controller;

	@Mock
	private CatalogService catalogService;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
	}

	@Test
	public void catalogIsRetrievedCorrectly() throws Exception {
		when(catalogService.getCatalog()).thenReturn(CatalogFixture.getCatalog());

		ServiceDefinition service = ServiceFixture.getSimpleService();
		List<Plan> plans = PlanFixture.getAllPlans();

		this.mockMvc.perform(get("/v2/catalog").accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.services", hasSize(1)))
				.andExpect(jsonPath("$.services[*].id", contains(service.getId())))
				.andExpect(jsonPath("$.services[*].name", contains(service.getName())))
				.andExpect(jsonPath("$.services[*].description", contains(service.getDescription())))
				.andExpect(jsonPath("$.services[*].bindable", contains(service.isBindable())))
				.andExpect(jsonPath("$.services[*].plan_updateable", contains(service.isPlanUpdateable())))
				.andExpect(jsonPath("$.services[*].requires[*]", empty()))
				.andExpect(jsonPath("$.services[*].plans[*].id", containsInAnyOrder(plans.get(0).getId(), plans.get(1).getId())))
				.andExpect(jsonPath("$.services[*].plans[*].name", containsInAnyOrder(plans.get(0).getName(), plans.get(1).getName())))
				.andExpect(jsonPath("$.services[*].plans[*].description",
						containsInAnyOrder(plans.get(0).getDescription(), plans.get(1).getDescription())))
				.andExpect(jsonPath("$.services[*].plans[*].metadata", containsInAnyOrder(Collections.EMPTY_MAP, plans.get(1).getMetadata())));
	}

}
