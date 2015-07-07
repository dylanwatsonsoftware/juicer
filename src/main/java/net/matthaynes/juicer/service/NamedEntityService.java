package net.matthaynes.juicer.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;

public class NamedEntityService {

	@Nonnull
	private final CRFClassifier<? extends CoreMap> classifier;

	/**
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws ClassCastException
	 */
	public NamedEntityService() throws ClassCastException, ClassNotFoundException, IOException {
		this.classifier = buildClassifier();
	}

	/**
	 * @param text
	 *            the text to extract entities from
	 * 
	 * @return the entities
	 */
	@Nonnull
	public Collection<NamedEntity> entities(@Nonnull String text) {
		LinkedList<CoreLabel> tokens = compress(classifier.classify(text));

		Map<String, NamedEntity> entities = new HashMap<>();
		while (!tokens.isEmpty()) {

			switch (getAnnotationType(tokens.peek())) {
			case "ORGANIZATION":
				addNamedEntity(entities, getNextOrganization(tokens));
				break;
			case "LOCATION":
				addNamedEntity(entities, getNextLocation(tokens));
				break;
			case "PERSON":
				addNamedEntity(entities, getNextPerson(tokens));
				break;
			default:
				tokens.removeFirst();
				break;
			}
		}

		return entities.values();
	}

	/**
	 * @return an English Stanford NLP Classifier
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws ClassCastException
	 */
	@Nonnull
	private CRFClassifier<? extends CoreMap> buildClassifier()
			throws IOException, ClassCastException, ClassNotFoundException {
		try (InputStream model = NamedEntityService.class.getResourceAsStream("/english.all.3class.distsim.crf.ser.gz");
				InputStream is = new java.io.BufferedInputStream(model);
				GZIPInputStream gzipped = new java.util.zip.GZIPInputStream(is)) {
			CRFClassifier<? extends CoreMap> classifier = CRFClassifier.getClassifier(gzipped);

			return classifier;
		}
	}

	@SuppressWarnings("unchecked")
	@Nonnull
	private LinkedList<CoreLabel> compress(@Nonnull List<?> mapList) {
		LinkedList<CoreLabel> compressed = new LinkedList<>();
		for (Object list : mapList) {
			for (Object elem : (List<CoreLabel>) list) {
				compressed.add((CoreLabel) elem);
			}
		}

		return compressed;
	}

	/**
	 * @param label
	 * @return one of ORGANIZATION, LOCATION or PERSON
	 */
	@Nonnull
	private String getAnnotationType(@Nonnull CoreLabel label) {
		if (label == null) {
			return null;
		}

		return label.get(AnswerAnnotation.class);
	}

	private void addNamedEntity(@Nonnull Map<String, NamedEntity> entities, @Nonnull NamedEntity entity) {
		if (entities.containsKey(entity.text)) {
			entities.get(entity.text).frequency += 1;
		} else {
			entities.put(entity.text, entity);
		}
	}

	/**
	 * @param tokens
	 * @param annotation
	 * 
	 * @return the next set of tokens that all have the same annotation.
	 *         <p>
	 *         This ensures that "Barack Obama" is considered 1 entity, rather
	 *         than 2.
	 */
	@Nonnull
	private String getNextNameForAnnotation(@Nonnull LinkedList<CoreLabel> tokens, @Nonnull String annotation) {
		List<String> words = new ArrayList<>();
		while (annotation.equals(getAnnotationType(tokens.peek()))) {
			words.add(tokens.removeFirst().toString());
		}

		return StringUtils.join(words, " ");
	}

	private Organization getNextOrganization(LinkedList<CoreLabel> tokens) {
		return new Organization(getNextNameForAnnotation(tokens, "ORGANIZATION"));
	}

	private Location getNextLocation(LinkedList<CoreLabel> tokens) {
		return new Location(getNextNameForAnnotation(tokens, "LOCATION"));
	}

	private Person getNextPerson(LinkedList<CoreLabel> tokens) {
		return new Person(getNextNameForAnnotation(tokens, "PERSON"));
	}

	public class NamedEntity {

		@Nonnull
		String type;

		@Nonnull
		String text;

		int frequency;

		public NamedEntity(@Nonnull String type, @Nonnull String text) {
			this.type = type;
			this.text = text;
			this.frequency = 1;
		}

		public String getType() {
			return type;
		}

		public String getText() {
			return text;
		}

		public int getFrequency() {
			return frequency;
		}
	}

	class Organization extends NamedEntity {
		public Organization(@Nonnull String text) {
			super("Organization", text);
		}
	}

	class Person extends NamedEntity {
		public Person(@Nonnull String text) {
			super("Person", text);
		}
	}

	class Location extends NamedEntity {
		public Location(@Nonnull String text) {
			super("Location", text);
		}
	}
}
