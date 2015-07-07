package net.matthaynes.juicer.service;

import java.util.Collection;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;

import com.gravity.goose.Goose;

import net.matthaynes.juicer.network.NetworkHelper;
import net.matthaynes.juicer.service.NamedEntityService.NamedEntity;
import scala.collection.JavaConversions;

public class ArticleExtractorService {

	@Nonnull
	private final Goose goose;

	@Nonnull
	private final NamedEntityService namedEntityService;

	@Nonnull
	private final NetworkHelper networkHelper;

	/**
	 * @param goose
	 * @param namedEntityService
	 */
	public ArticleExtractorService(@Nonnull NetworkHelper networkHelper, @Nonnull Goose goose,
			@Nonnull NamedEntityService namedEntityService) {
		this.networkHelper = networkHelper;
		this.goose = goose;
		this.namedEntityService = namedEntityService;
	}

	/**
	 * @param url
	 *            the url of the article
	 * 
	 * @return the article
	 */
	@CheckForNull
	public Article extract(@Nonnull String url) {
		String html = networkHelper.getHtml(url);
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

	private Image getImage(@Nonnull com.gravity.goose.Article article) {
		com.gravity.goose.images.Image topImage = article.topImage();
		if (StringUtils.isBlank(topImage.getImageSrc())) {
			return null;
		} else {
			return new Image(topImage.imageSrc(), topImage.width(), topImage.height());
		}
	}

	public static class Image {

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

	public static class Article {
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
