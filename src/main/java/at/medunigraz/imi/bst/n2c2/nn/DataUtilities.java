package at.medunigraz.imi.bst.n2c2.nn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeFactory;

/**
 * Refactored from dl4j examples.
 * 
 * @author Markus
 *
 */
public class DataUtilities {

	private static final Pattern CLEANER_REGEX = Pattern.compile("\\p{javaWhitespace}+");

	// stop words
	private CharArraySet stopWords = CharArraySet.EMPTY_SET;

	public DataUtilities() {

		try {
			ArrayList<String> stopWords = new ArrayList<String>();
			InputStream is = this.getClass().getResourceAsStream("/nlp/StopWords.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				stopWords.add(line);
			}
			this.stopWords = new CharArraySet(stopWords, true);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static String[] tokenStreamToArray(TokenStream stream) throws IOException {
		CharTermAttribute charTermAttribute = stream.addAttribute(CharTermAttribute.class);
		stream.reset();

		ArrayList<String> ret = new ArrayList<>();
		while (stream.incrementToken()) {
			ret.add(charTermAttribute.toString());
		}

		stream.end();
		stream.close();

		return ret.toArray(new String[0]);
	}

	private static TokenStream getTokenStream(Reader reader) {
		AttributeFactory factory = AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY;

		StandardTokenizer standardTokenizer = new StandardTokenizer(factory);
		standardTokenizer.setReader(reader);

		return standardTokenizer;
	}

	/**
	 * 
	 * 
	 * @param reader
	 * @return
	 */
	public TokenStream getQuickViewStreamReduced(Reader reader) {
		TokenStream result = getTokenStream(reader);

		result = new StandardFilter(result);
		result = new LowerCaseFilter(result);

		// negations tokens are included therefore no use
		// result = new StopFilter(result, stopWords);
		result = new SnowballFilter(result, "English");

		return result;
	}

	/**
	 * 
	 * 
	 * @param textToProcess
	 * @return
	 */
	public String processTextReduced(String textToProcess) {
		TokenStream stream = this.getQuickViewStreamReduced(new StringReader(textToProcess));
		String[] tokens = new String[0];
		try {
			tokens = tokenStreamToArray(stream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		String normalized = String.join(" ", tokens);;

		// post normalization
		normalized = normalized.replaceAll("[\\.\\,\\_\\:]+", " ");
		normalized = normalized.replaceAll("[\\s]+", " ");
		normalized = normalized.trim();

		return normalized;
	}

	/**
	 * 
	 * 
	 * @param toProcess
	 * @param charNGram
	 * @return
	 * @throws IOException
	 */
	public String getCharNGramRepresentation001(String toProcess, int charNGram) throws IOException {

		String charNGramRepresentation = "";
		String embeddingChar = "_";
		String embedding = "";

		for (int i = 0; i < charNGram - 1; i++) {
			embedding += embeddingChar;
		}

		toProcess = embedding + toProcess.replaceAll("\\s+", embeddingChar) + embedding;

		NGramTokenizer nGramTokenizer = new NGramTokenizer(charNGram, charNGram);
		CharTermAttribute charTermAttribute = nGramTokenizer.addAttribute(CharTermAttribute.class);

		nGramTokenizer.setReader(new StringReader(toProcess));
		nGramTokenizer.reset();

		while (nGramTokenizer.incrementToken()) {
			String characterNGram = charTermAttribute.toString();
			charNGramRepresentation += characterNGram + " ";
		}
		nGramTokenizer.end();
		nGramTokenizer.close();

		return charNGramRepresentation.trim();
	}

    public String getChar3GramRepresentation(String toProcess) throws IOException {

		String charNGramRepresentation = "";
		String embedding = "_";

		NGramTokenizer nGramTokenizer = new NGramTokenizer(3, 3);
		CharTermAttribute charTermAttribute = nGramTokenizer.addAttribute(CharTermAttribute.class);
		for (String split : toProcess.split("\\s")) {

			nGramTokenizer.setReader(new StringReader(embedding + split + embedding));
			nGramTokenizer.reset();

			while (nGramTokenizer.incrementToken()) {
				String characterNGram = charTermAttribute.toString();
				charNGramRepresentation += characterNGram + " ";
			}
			nGramTokenizer.end();
			nGramTokenizer.close();
		}
		return charNGramRepresentation.trim();
	}

	public static List<String> getSentences(String narrative) {

		String abbreviations = "\\d|[mM][rR]|[dD][rR]|[dD][rR][sS]|[sM][sS]|[cC]";
		String cleanPatternA = "[\t\\*_\\%=#]+";
		String cleanPatternB = "&nbsp;|<BR>|\\s+|--";

		String cleanedNarrative = "";
		String tempString = "";

		// cleansing beginning input lines
		try {
			List<String> lines = IOUtils.readLines(new StringReader(narrative));
			for (String line : lines) {
				if (line.length() > 0) {
					tempString = line.replaceAll(cleanPatternA, " ");
					tempString = tempString.replaceAll(cleanPatternB, " ");
					tempString = tempString.replaceAll("\\.+", ".").trim();
					if (tempString.length() > 0)
						cleanedNarrative += tempString + "\n";
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// new line split logic
		String[] newLineSplits = cleanedNarrative.split("\n(?=[A-Z]|[0-9])");
		ArrayList<String> sentences = new ArrayList<String>();

		// period character split logic
		for (String newLineSplit : newLineSplits) {
			newLineSplit = newLineSplit.replaceAll("[\r\n\\s]+", " ").trim();
			if (newLineSplit.length() > 0) {
				sentences.addAll(Arrays.asList(newLineSplit.split("(?<!" + abbreviations + ")(\\.)(\\s+)")));
			}
		}

		// post cleansing
		// sentences.forEach(sentence -> System.out.println(sentence));

		return sentences;
	}

	public static String cleanText(String text) {
		return CLEANER_REGEX.matcher(text).replaceAll(" ");
	}
}
