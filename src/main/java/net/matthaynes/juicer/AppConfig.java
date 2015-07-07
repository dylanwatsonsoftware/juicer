package net.matthaynes.juicer;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gravity.goose.Goose;
import com.gravity.goose.Language;
import com.gravity.goose.network.HtmlFetcher;

import net.matthaynes.juicer.network.NetworkHelper;
import net.matthaynes.juicer.service.ArticleExtractorService;
import net.matthaynes.juicer.service.EntityInformationService;
import net.matthaynes.juicer.service.NamedEntityService;

@SuppressWarnings("deprecation")
@Configuration
public class AppConfig {

	@Bean
	public NetworkHelper networkHelper() {
		return new NetworkHelper();
	}

	@Bean
	public NamedEntityService namedEntityService() throws ClassCastException, ClassNotFoundException, IOException {
		return new NamedEntityService();
	}

	@Bean
	public ArticleExtractorService articleExtractorService()
			throws ClassCastException, ClassNotFoundException, IOException {
		return new ArticleExtractorService(networkHelper(), goose(), namedEntityService());
	}

	@Bean
	public EntityInformationService entityInformationService() {
		return new EntityInformationService(networkHelper());
	}

	@Bean
	public Goose goose() {
		return new Goose(configuration());
	}

	@Bean
	public com.gravity.goose.Configuration configuration() {
		Language language = new Language.English$();
		String localStoragePath = Properties.TEMP_DIRECTORY;
		int minBytesForImages = 4500;
		int minWidth = 120;
		int minHeight = 120;
		boolean enableImageFetching = true;
		boolean enableAllImagesFetching = true;
		String imagemagickConvertPath = Properties.IMAGE_MAGICK_CONVERT_HOME;
		String imagemagickIdentifyPath = Properties.IMAGE_MAGICK_IDENTIFY_HOME;
		int connectionTimeout = 10000; // 10 seconds
		int socketTimeout = 10000; // 10 seconds
		int imageConnectionTimeout = 2000; // 2 seconds;
		int imageSocketTimeout = 5000; // 5 seconds;
		String browserUserAgent = "Mozilla/5.0 (X11; U; Linux x86_64; de; rv:1.9.2.8) Gecko/20100723 Ubuntu/10.04 (lucid) Firefox/3.6.8";
		String browserReferer = "https://www.google.com";

		com.gravity.goose.Configuration config = new com.gravity.goose.Configuration(language, localStoragePath,
				minBytesForImages, minWidth, minHeight, enableImageFetching, enableAllImagesFetching,
				imagemagickConvertPath, imagemagickIdentifyPath, connectionTimeout, socketTimeout,
				imageConnectionTimeout, imageSocketTimeout, browserUserAgent, browserReferer);

		if (Properties.PROXY_HOST != null) {
			HtmlFetcher.getHttpClient().getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					new HttpHost(Properties.PROXY_HOST, Properties.PROXY_PORT));
		}

		return config;
	}
}
