package net.matthaynes.juicer;

import java.io.IOException;
import javax.annotation.Nonnull;
import org.apache.http.HttpHost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.common.base.Optional;
import com.gravity.goose.Goose;
import com.gravity.goose.Language;
import com.gravity.goose.network.HtmlFetcher;
import net.matthaynes.juicer.network.NetworkHelper;
import net.matthaynes.juicer.service.ArticleExtractorService;
import net.matthaynes.juicer.service.EntityInformationService;
import net.matthaynes.juicer.service.NamedEntityService;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;

/**
 *
 * @author watsond
 */
@SuppressWarnings("deprecation")
@Configuration
public class AppConfig {

  @Nonnull
  private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36";

  @Bean
  public NetworkHelper networkHelper() {
    return new NetworkHelper();
  }

  @Bean
  public NamedEntityService namedEntityService() throws ClassCastException, ClassNotFoundException,
      IOException {
    return new NamedEntityService();
  }

  @Bean
  public ArticleExtractorService articleExtractorService() throws ClassCastException,
      ClassNotFoundException, IOException {
    return new ArticleExtractorService(networkHelper(), goose(), namedEntityService(),
        defaultingLanguageDetector());
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
  public DefaultingLanguageDetector defaultingLanguageDetector() throws IOException {
    return new DefaultingLanguageDetector(languageDetector(), textObjectFactory());
  }

  @Bean
  public LanguageDetector languageDetector() throws IOException {
    //load all languages:
    List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();

    //build language detector:
    return LanguageDetectorBuilder.create(NgramExtractors.standard())
        .withProfiles(languageProfiles).build();
  }

  @Bean
  public TextObjectFactory textObjectFactory() {
    return CommonTextObjectFactories.forDetectingOnLargeText();
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
    int connectionTimeout = Properties.CONNECT_TIMEOUT;
    int socketTimeout = Properties.CONNECT_TIMEOUT;
    int imageConnectionTimeout = Properties.IMAGE_TIMEOUT; // 2 seconds;
    int imageSocketTimeout = Properties.IMAGE_TIMEOUT; // 5 seconds;
    String browserUserAgent = USER_AGENT;
    String browserReferer = "https://www.google.com";

    com.gravity.goose.Configuration config = new com.gravity.goose.Configuration(language,
        localStoragePath, minBytesForImages, minWidth, minHeight, enableImageFetching,
        enableAllImagesFetching, imagemagickConvertPath, imagemagickIdentifyPath,
        connectionTimeout, socketTimeout, imageConnectionTimeout, imageSocketTimeout,
        browserUserAgent, browserReferer);

    if (Properties.PROXY_HOST != null) {
      HtmlFetcher
          .getHttpClient()
          .getParams()
          .setParameter(ConnRoutePNames.DEFAULT_PROXY,
              new HttpHost(Properties.PROXY_HOST, Properties.PROXY_PORT));
    }

    return config;
  }
  }

  public class DefaultingLanguageDetector {

    private final String defaultLang = "en";

    private final LanguageDetector languageDetector;

    private final TextObjectFactory textObjectFactory;

    DefaultingLanguageDetector(LanguageDetector languageDetector,
        TextObjectFactory textObjectFactory) {
      this.languageDetector = languageDetector;
      this.textObjectFactory = textObjectFactory;
    }

    /**
     * Detect the language
     *
     * @param text
     * @return the language
     */
    @Nonnull
    public String detect(String text) {
      TextObject textObject = textObjectFactory.forText(text);
      Optional<LdLocale> lang = languageDetector.detect(textObject);
      if (lang.isPresent()) {
        return lang.get().getLanguage();
      }

      return defaultLang;

    }
}
