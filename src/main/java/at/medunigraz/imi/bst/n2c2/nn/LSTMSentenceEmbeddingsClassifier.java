package at.medunigraz.imi.bst.n2c2.nn;

import at.medunigraz.imi.bst.n2c2.nn.architecture.Architecture;
import at.medunigraz.imi.bst.n2c2.nn.architecture.LSTMArchitecture;
import at.medunigraz.imi.bst.n2c2.nn.input.SentenceEmbedding;
import at.medunigraz.imi.bst.n2c2.nn.iterator.SentenceIterator;

import java.io.IOException;
import java.util.Properties;

/**
 * LSTM classifier using sentence embedding from bert.
 *
 * @author seanshi007
 */
public class LSTMSentenceEmbeddingsClassifier extends BaseNNClassifier {

    /**
     * n2c2 longest training sentence has 840 different character trigrams.
     */
    private static final int TRUNCATE_LENGTH = 840;

    private static final Architecture ARCHITECTURE = new LSTMArchitecture();

    public LSTMSentenceEmbeddingsClassifier() {
        super(ARCHITECTURE);
    }

    @Override
    protected void initializeNetwork() {
        fullSetIterator = new SentenceIterator(patientExamples,
            new SentenceEmbedding(),
            TRUNCATE_LENGTH, BATCH_SIZE);
        this.net = architecture.getNetwork(fullSetIterator.getInputRepresentation().getVectorSize());
    }

    @Override
    public void initializeNetworkFromFile(String pathToModel) throws IOException {
        // TODO move to iterator.
        Properties prop = loadProperties(pathToModel);
        final int truncateLength = Integer.parseInt(prop.getProperty("truncateLength"));
        fullSetIterator = new SentenceIterator(patientExamples,
            new SentenceEmbedding(),
            TRUNCATE_LENGTH, BATCH_SIZE);

        super.initializeNetworkFromFile(pathToModel);
    }

    protected String getModelName() {
        return toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
            + "{truncateLength=" + TRUNCATE_LENGTH
            + ",batchSize=" + BATCH_SIZE
            + ",architecture=" + architecture.toString()
            + "}";
    }
}
