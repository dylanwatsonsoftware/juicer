package net.matthaynes.juicer.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.matthaynes.juicer.network.NetworkHelper;
import net.matthaynes.juicer.service.EntityInformationService.DbpediaJsonResult.DbpediaResults.DbpediaBinding;

@SuppressFBWarnings({ "NP_UNWRITTEN_FIELD", "UWF_NULL_FIELD" })
public class EntityInformationService {

	@Nonnull
	private final Gson gson;

	@Nonnull
	private final NetworkHelper networkHelper;

	/**
	 */
	public EntityInformationService(NetworkHelper networkHelper) {
		this.networkHelper = networkHelper;
		GsonBuilder gsonBuilder = new GsonBuilder();
		this.gson = gsonBuilder.create();
	}

	/**
	 * @param entityName
	 * 
	 * @return the entity
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@Nonnull
	public Entity information(@Nonnull String entityName) throws UnsupportedEncodingException {
		String dbPediaUrl = getDbPediaUrl(entityName);
		String html = networkHelper.getHtml(dbPediaUrl);
		List<DbpediaBinding> results = gson.fromJson(html, DbpediaJsonResult.class).results.bindings;
		if (results.isEmpty()) {
			return new Entity(entityName, null);
		}

		String description = results.iterator().next().wikipedia_data_field_abstract.value;
		return new Entity(entityName, description);

	}

	private String getDbPediaUrl(String entityName) throws UnsupportedEncodingException {
		return "http://dbpedia.org/sparql?output=json&default-graph-uri=http%3A%2F%2Fdbpedia.org&query=PREFIX%20dbpedia-owl%3A%20%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2F%3E%0A%20%20%20%20SELECT%20%3Fwikipedia_data_field_name%20%3Fwikipedia_data_field_abstract%0A%20%20%20%20WHERE%20%7B%0A%20%20%20%20%20%20%20%20%3Fwikipedia_data%20foaf%3Aname%20%22"
				+ URLEncoder.encode(entityName, "UTF-8")
				+ "%22%40en%3B%20foaf%3Aname%20%0A%20%20%20%20%20%20%20%20%3Fwikipedia_data_field_name%3B%20dbpedia-owl%3Aabstract%20%3Fwikipedia_data_field_abstract.%0A%20%20%20%20%20%20%20%20FILTER%20langMatches(lang(%3Fwikipedia_data_field_abstract)%2C%27en%27)%0A%20%20%20%20%20%20%7D&endpoint=/sparql&maxrows=50&timeout=&default-graph-uri=http://dbpedia.org&view=1&raw_iris=true";
	}

	public static class Entity {

		@Nonnull
		String name;

		@CheckForNull
		String description;

		public Entity(@Nonnull String name, @CheckForNull String description) {
			this.name = name;
			this.description = description;
		}

		@Nonnull
		public String getName() {
			return name;
		}

		@CheckForNull
		public String getDescription() {
			return description;
		}
	}

	static class DbpediaJsonResult {
		final DbpediaResults results;

		public DbpediaJsonResult() {
			this.results = null;
		}

		@SuppressFBWarnings("URF_UNREAD_FIELD")
		static class DbpediaResults {
			final List<DbpediaBinding> bindings;

			public DbpediaResults() {
				this.bindings = null;
			}

			static class DbpediaBinding {
				final DbpediaResult wikipedia_data_field_abstract;

				public DbpediaBinding() {
					this.wikipedia_data_field_abstract = null;
				}

				static class DbpediaResult {
					final String type;
					final String value;

					public DbpediaResult() {
						this.type = null;
						this.value = null;
					}
				}
			}
		}
	}
}
