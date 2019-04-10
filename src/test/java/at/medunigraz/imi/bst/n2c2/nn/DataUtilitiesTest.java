package at.medunigraz.imi.bst.n2c2.nn;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.model.Patient;

public class DataUtilitiesTest {

	@Test
	public void processTextReduced() throws IOException {
		String normalized = DataUtilities.processTextReduced("This is a, test    sentence: test_sentence.");
		assertEquals("this is a test sentenc test sent", normalized);
	}

	@Test
	public void getChar3GramRepresentation() throws IOException {
		String normalized = DataUtilities.getChar3GramRepresentation("this is a test sentence");
		assertEquals("_th thi his is_ _is is_ _a_ _te tes est st_ _se sen ent nte ten enc nce ce_", normalized);
	}

	@Test
	public void sample() throws IOException, SAXException {
		final File SAMPLE = new File(getClass().getResource("/gold-standard/sample.xml").getPath());
		Patient p = new PatientDAO().fromXML(SAMPLE);

		StringBuilder normalizedText = new StringBuilder();
		StringBuilder textTrigrams = new StringBuilder();

		List<String> sentences = DataUtilities.getSentences(p.getText());
		for (String sentence : sentences) {
			String normalized = DataUtilities.processTextReduced(sentence);
			String charTrigrams = DataUtilities.getChar3GramRepresentation(normalized);

			normalizedText.append(normalized);
			normalizedText.append("\n");

			textTrigrams.append(charTrigrams);
			textTrigrams.append("\n");
		}

		final File expectedNormalized = new File(getClass().getResource("/nn/sample-normalized.txt").getFile());
		final File expectedTrigrams = new File(getClass().getResource("/nn/sample-trigrams.txt").getFile());

		assertEquals(FileUtils.readFileToString(expectedNormalized, "UTF-8"), normalizedText.toString());
		assertEquals(FileUtils.readFileToString(expectedTrigrams, "UTF-8"), textTrigrams.toString());
	}

	@Test
	public void tokenize() {
		// Example from https://nlp.stanford.edu/software/tokenizer.shtml
		String[] actual = DataUtilities.tokenize("\"Oh, no,\" she's saying, \"our $400 blender can't handle something this hard!\"");
		assertEquals("Oh no she's saying our 400 blender can't handle something this hard", String.join(" ", actual));

		// Examples from https://www.nltk.org/api/nltk.tokenize.html
		actual = DataUtilities.tokenize("Good muffins cost $3.88\nin New York.  Please buy me\ntwo of them.\nThanks.");
		assertEquals("Good muffins cost 3.88 in New York Please buy me two of them Thanks", String.join(" ", actual));

		actual = DataUtilities.tokenize("They'll save and invest more.");
		assertEquals("They'll save and invest more", String.join(" ", actual));

		actual = DataUtilities.tokenize("hi, my name can't hello,");
		assertEquals("hi my name can't hello", String.join(" ", actual));
	}

}
