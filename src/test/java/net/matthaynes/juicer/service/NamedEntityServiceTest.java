package net.matthaynes.juicer.service;

import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Iterables.find;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import com.google.common.base.Predicate;
import net.matthaynes.juicer.service.NamedEntityService.Location;
import net.matthaynes.juicer.service.NamedEntityService.NamedEntity;
import net.matthaynes.juicer.service.NamedEntityService.Organization;
import net.matthaynes.juicer.service.NamedEntityService.Person;

/**
 * Tests for {@link NamedEntityService}
 *
 * @author watsond
 */
public class NamedEntityServiceTest {

  NamedEntityService namedEntityService;

  String testString = "The fate of Lehman Brothers, the beleaguered investment bank, hung in the balance on Sunday\n    as Federal Reserve officials and the leaders of major financial institutions continued to gather in emergency\n    meetings trying to complete a plan to rescue the stricken bank. Several possible plans emerged from the talks,\n    held at the Federal Reserve Bank of New York and led by Timothy R. Geithner, the president of the New York Fed,\n    and Treasury Secretary Henry M. Paulson Jr.\n\n    Meanwhile, in London, the capital city of the United Kingdom there was a strike on the London Underground, this\n    was not an issue for Mr Paulson though, as he live in the US not the United Kingdom";

  /**
   * @throws Exception
   */
  @Before
  public void before() throws Exception {
    namedEntityService = new NamedEntityService();
  }

  /**
	 */
  @SuppressWarnings("boxing")
  @Test
  public void extractEntities() {
    Collection<NamedEntity> entities = namedEntityService.entities(testString);

    Collection<NamedEntity> locations = filter(entities, instanceOf(Location.class));
    Collection<NamedEntity> organizations = filter(entities, instanceOf(Organization.class));
    Collection<NamedEntity> people = filter(entities, instanceOf(Person.class));

    assertThat(locations.size(), is(3));
    assertThat(organizations.size(), is(5));
    assertThat(people.size(), is(3));

    assertThat(find(entities, textThatMatches("United Kingdom")).frequency, is(2));
    assertThat(find(entities, textThatMatches("Lehman Brothers")).frequency, is(1));
  }

  private static Predicate<NamedEntity> textThatMatches(String text) {
    return new EntityTextMatches(text);
  }

  private static class EntityTextMatches implements Predicate<NamedEntity> {

    private final String matchText;

    public EntityTextMatches(String matchText) {
      this.matchText = matchText;
    }

    @Override
    public boolean apply(NamedEntity entity) {
      return entity.text.equals(matchText);
    }
  }
}
