package at.medunigraz.imi.bst.n2c2.nn.input;

import com.robrua.nlp.bert.Bert;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.nio.file.Paths;

/**
 * Sentence embedding from bert.
 */
public class SentenceEmbedding implements InputRepresentation {

    private final Bert bert;

    public SentenceEmbedding() {
        bert = Bert.load(Paths.get("C:\\Users\\gush\\Documents\\Learning\\UIUC\\DL4H\\project\\n2c2\\sampleBert"));
    }

    @Override
    public INDArray getVector(String unit) {
        INDArray featureVector = Nd4j.zeros(getVectorSize());
        float[] embeddings = bert.embedSequence(unit);
        for (int i = 0; i < embeddings.length; i++) {
            featureVector.putScalar(new int[]{i}, embeddings[i]);
        }
        return featureVector;
    }

    @Override
    public boolean hasRepresentation(String unit) {
        return true;
    }

    @Override
    public int getVectorSize() {
        return 768;
    }

    @Override
    public void save(File model) {
        // NOOP While we don't train vectors
    }

    @Override
    public void load(File model) {
        // TODO same as constructor
    }
}
