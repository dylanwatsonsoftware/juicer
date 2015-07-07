package net.matthaynes.juicer.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import net.matthaynes.juicer.network.NetworkHelper;
import net.matthaynes.juicer.service.EntityInformationService.Entity;

public class EntityInformationServiceTest {

	EntityInformationService entityInformationService;
	NetworkHelper networkHelper;

	/**
	 */
	@Before
	public void before() {
		networkHelper = mock(NetworkHelper.class);
		entityInformationService = new EntityInformationService(networkHelper);
	}

	/**
	 */
	@Test
	public void testExtractEntityFromJson() throws Exception {
		String expectedDescription = "Barack Hussein Obama II (/bəˈrɑːk huːˈseɪn ɵˈbɑːmə/; born August 4, 1961) is the 44th and current President of the United States";

		when(networkHelper.getHtml(Mockito.anyString())).thenReturn(
				"{\n\"head\": {\n \"link\": [\n\n],\n \"vars\": [\n\"wikipedia_data_field_name\",\n\"wikipedia_data_field_abstract\"\n ]\n},\n\"results\": {\n \"distinct\": false,\n \"ordered\": true,\n \"bindings\": [\n{\n\"wikipedia_data_field_name\": {\n \"type\": \"literal\",\n \"xml:lang\": \"en\",\n \"value\": \"Barack Obama\"\n},\n\"wikipedia_data_field_abstract\": {\n \"type\": \"literal\",\n \"xml:lang\": \"en\",\n \"value\": \""
						+ expectedDescription + "\"\n}\n}\n]\n}\n}");

		Entity obamaInfo = entityInformationService.information("Barack Obama");

		assertThat(obamaInfo.description, is(expectedDescription));

		verify(networkHelper).getHtml(Mockito.anyString());
	}
}
