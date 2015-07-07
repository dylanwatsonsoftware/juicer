package net.matthaynes.juicer.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.gravity.goose.Article;
import com.gravity.goose.Goose;

import net.matthaynes.juicer.network.NetworkHelper;

public class ArticleExtractorServiceTest {

	private ArticleExtractorService articleExtractorService;
	private Goose goose;
	private NamedEntityService namedEntityService;
	private NetworkHelper networkHelper;

	/**
	 */
	@Before
	public void before() {
		networkHelper = mock(NetworkHelper.class);
		namedEntityService = mock(NamedEntityService.class);
		goose = mock(Goose.class);

		articleExtractorService = new ArticleExtractorService(networkHelper, goose, namedEntityService);
	}

	@Test
	public void testArticleExtract() {
		String url = "http://www.bbc.co.uk/news/world-africa-16377824";

		Article article = new Article();
		article.setCanonicalLink(url);
		article.setDomain("www.bbc.co.uk");
		article.setLinkhash("ac2f2e739421184f01c942b057f8449d");
		article.setTitle("South Sudan 'sends more troops' to strife-torn town Pibor");
		article.setMetaDescription(
				"South Sudan's government says it is sending more troops and police to the town ...");
		article.setCleanedArticleText(
				"South Sudan's government says it is sending more troops and police to the town ...");

		when(networkHelper.getHtml(url)).thenReturn("article html");
		when(goose.extractContent(Mockito.eq(url), Mockito.anyString(), Mockito.anyString())).thenReturn(article);

		net.matthaynes.juicer.service.ArticleExtractorService.Article extracted = articleExtractorService.extract(url);

		assertThat(extracted.title, is(article.title()));
		assertThat(extracted.description, is(article.metaDescription()));
		assertThat(extracted.body, is(article.cleanedArticleText()));
		assertThat(extracted.hash, is(article.linkhash()));
		assertThat(extracted.url, is(article.canonicalLink()));
		assertThat(extracted.domain, is(article.domain()));

		verify(namedEntityService).entities(article.title() + " " + article.cleanedArticleText());
		verify(networkHelper).getHtml(url);
	}
}
