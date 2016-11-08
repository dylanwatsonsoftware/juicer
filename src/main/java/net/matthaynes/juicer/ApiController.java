package net.matthaynes.juicer;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import net.matthaynes.juicer.service.ArticleExtractorService;
import net.matthaynes.juicer.service.ArticleExtractorService.Article;
import net.matthaynes.juicer.service.EntityInformationService;
import net.matthaynes.juicer.service.EntityInformationService.Entity;
import net.matthaynes.juicer.service.NamedEntityService;
import net.matthaynes.juicer.service.NamedEntityService.NamedEntity;

/**
 * The API Controller
 *
 * @author watsond
 */
@RestController
@RequestMapping("/api")
public class ApiController {

  @Nonnull
  private final NamedEntityService namedEntityService;

  @Nonnull
  private final ArticleExtractorService articleExtractorService;

  @Nonnull
  private final EntityInformationService entityInformationService;

  /**
   * @param namedEntityService
   * @param articleExtractorService
   * @param entityInformationService
   */
  @Autowired
  public ApiController(@Nonnull NamedEntityService namedEntityService,
      @Nonnull ArticleExtractorService articleExtractorService,
      @Nonnull EntityInformationService entityInformationService) {
    this.namedEntityService = namedEntityService;
    this.articleExtractorService = articleExtractorService;
    this.entityInformationService = entityInformationService;
  }

  /**
   * @param text
   *          the text to extract entities from
   *
   * @return the entities
   */
  @Nonnull
  @RequestMapping(value = "/entities", method = RequestMethod.POST)
  public Map<String, Collection<NamedEntity>> entities(
      @RequestParam(value = "text", defaultValue = "") String text) {
    Collection<NamedEntity> entities = namedEntityService.entities(text);

    Map<String, Collection<NamedEntity>> result = new HashMap<>();
    result.put("entities", entities);
    return result;
  }

  /**
   * @param url
   *          the url of the article to juice
   *
   * @return the entities
   */
  @Nonnull
  @RequestMapping(value = "/article")
  public Map<String, Article> article(@RequestParam(value = "url", defaultValue = "") String url) {
    Article article = articleExtractorService.extract(url);

    Map<String, Article> result = new HashMap<>();
    result.put("article", article);
    return result;
  }

  /**
   * @param name
   *          the name of the entity to describe
   *
   * @return the entity
   * @throws UnsupportedEncodingException
   */
  @Nonnull
  @RequestMapping(value = "/describe")
  public Entity entity(@RequestParam(value = "name", defaultValue = "") String name)
      throws UnsupportedEncodingException {
    Entity entity = entityInformationService.information(name);

    return entity;
  }

  /**
   * @return a pong
   */
  @Nonnull
  @RequestMapping(value = "/ping")
  public Map<String, String> ping() {

    Map<String, String> result = new HashMap<>();
    result.put("message", "pong");
    return result;
  }
}
