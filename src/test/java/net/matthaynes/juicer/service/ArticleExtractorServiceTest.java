package net.matthaynes.juicer.service;

import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.gravity.goose.Goose;

public class ArticleExtractorServiceTest {

	private ArticleExtractorService articleExtractorService;
	private Goose goose;
	private NamedEntityService namedEntityService;

	@Before
	public void before() {
		namedEntityService = mock(NamedEntityService.class);
		goose = mock(Goose.class);

		articleExtractorService = new ArticleExtractorService(goose, namedEntityService);
	}

	@Test
	public void testExtractArticleData() {
	}
}
