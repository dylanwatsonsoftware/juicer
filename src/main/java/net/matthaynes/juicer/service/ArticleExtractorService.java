package net.matthaynes.juicer.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;

import com.gravity.goose.Goose;

import net.matthaynes.juicer.service.NamedEntityService.NamedEntity;
import scala.collection.JavaConversions;

public class ArticleExtractorService {

	@Nonnull
	private final Goose goose;

	@Nonnull
	private final NamedEntityService namedEntityService;

	public ArticleExtractorService(@Nonnull Goose goose, @Nonnull NamedEntityService namedEntityService) {
		this.goose = goose;
		this.namedEntityService = namedEntityService;
	}

	@Nonnull
	public Article extract(@Nonnull String url) {
		String html = getHtml(url);
		if (StringUtils.isBlank(html)) {
			return null;
		}

		// TODO: Look into replacing Goose with:
		// https://github.com/karussell/snacktory
		com.gravity.goose.Article article = goose.extractContent(url, html, "en");
		String text = article.title() + " " + article.cleanedArticleText();
		Collection<NamedEntity> entities = namedEntityService.entities(text);

		Image image = getImage(article);

		return new Article(article.canonicalLink(), article.domain(), article.linkhash(), article.title(),
				article.metaDescription(), article.cleanedArticleText(), image,
				JavaConversions.mapAsJavaMap(article.additionalData()), entities);

	}

	private String getHtml(String url) {
		try {
			return Jsoup.connect(url).get().html();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return StringUtils.EMPTY;
	}

	private Image getImage(@Nonnull com.gravity.goose.Article article) {
		com.gravity.goose.images.Image topImage = article.topImage();
		if (StringUtils.isBlank(topImage.getImageSrc())) {
			return null;
		} else {
			return new Image(topImage.imageSrc(), topImage.width(), topImage.height());
		}
	}

	public class Image {

		String src;

		int width;

		int height;

		public Image(String src, int width, int height) {
			this.src = src;
			this.width = width;
			this.height = height;
		}

		public String getSrc() {
			return src;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
	}

	public class Article {
		String url;
		String domain;
		String hash;
		String title;
		String description;
		String body;
		Image image;
		Map<String, String> additionalData;
		Collection<NamedEntity> entities;

		public Article(String url, String domain, String hash, String title, String description, String body,
				Image image, Map<String, String> map, Collection<NamedEntity> entities) {
			this.url = url;
			this.domain = domain;
			this.hash = hash;
			this.title = title;
			this.description = description;
			this.body = body;
			this.image = image;
			this.additionalData = map;
			this.entities = entities;
		}

		public String getUrl() {
			return url;
		}

		public String getDomain() {
			return domain;
		}

		public String getHash() {
			return hash;
		}

		public String getTitle() {
			return title;
		}

		public String getDescription() {
			return description;
		}

		public String getBody() {
			return body;
		}

		public Image getImage() {
			return image;
		}

		public Map<String, String> getAdditionalData() {
			return additionalData;
		}

		public Collection<NamedEntity> getEntities() {
			return entities;
		}
	}
}
