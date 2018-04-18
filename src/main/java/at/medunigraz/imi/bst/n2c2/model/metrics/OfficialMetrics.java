package at.medunigraz.imi.bst.n2c2.model.metrics;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OfficialMetrics implements Metrics {

    /**
     * ------------ met -------------    ------ not met -------    -- overall ---
     * Prec.   Rec.    Speci.  F(b=1)    Prec.   Rec.    F(b=1)    F(b=1)  AUC
     */
    private static final int NUM_TOTAL_FIELDS = 9;

    private static final String PRECISION_MET = "Prec_met";
    private static final String RECALL_MET = "Rec_met";
    private static final String SPECIFICITY_MET = "Speci_met";
    private static final String F1_MET = "F1_met";

    private static final String PRECISION_NOT_MET = "Prec_notmet";
    private static final String RECALL_NOT_MET = "Rec_notmet";
    private static final String F1_NOT_MET = "F1_notmet";

    private static final String F1_OVERALL = "F1_overall";
    private static final String AUC_OVERALL = "AUC_overall";

    private Map<Criterion, Map<Eligibility, Metrics>> metrics = new HashMap<>();

    public OfficialMetrics() {
        // TODO lazy initialization
        for (Criterion c : Criterion.values()) {
            Map<Eligibility, Metrics> mapPerCriterion = new HashMap<>();
            for (Eligibility e : Eligibility.values()) {
                mapPerCriterion.put(e, new Metrics());
            }
            metrics.put(c, mapPerCriterion);
        }
    }

    public OfficialMetrics withPrecision(Criterion criterion, Eligibility eligibility, double value) {
        metrics.get(criterion).get(eligibility).precision = value;
        return this;
    }

    public OfficialMetrics withRecall(Criterion criterion, Eligibility eligibility, double value) {
        metrics.get(criterion).get(eligibility).recall = value;
        return this;
    }

    public OfficialMetrics withSpecificity(Criterion criterion, Eligibility eligibility, double value) {
        metrics.get(criterion).get(eligibility).specificity = value;
        return this;
    }

    public OfficialMetrics withF1(Criterion criterion, Eligibility eligibility, double value) {
        metrics.get(criterion).get(eligibility).f1 = value;
        return this;
    }

    public OfficialMetrics withAreaUnderCurve(Criterion criterion, Eligibility eligibility, double value) {
        metrics.get(criterion).get(eligibility).auc = value;
        return this;
    }

    public double getPrecision(Criterion criterion, Eligibility eligibility) {
        return metrics.get(criterion).get(eligibility).precision;
    }

    public double getRecall(Criterion criterion, Eligibility eligibility) {
        return metrics.get(criterion).get(eligibility).recall;
    }

    public double getSpecificity(Criterion criterion, Eligibility eligibility) {
        return metrics.get(criterion).get(eligibility).specificity;
    }

    public double getAreaUnderCurve(Criterion criterion, Eligibility eligibility) {
        return metrics.get(criterion).get(eligibility).auc;
    }

    public double getF1(Criterion criterion, Eligibility eligibility) {
        return metrics.get(criterion).get(eligibility).f1;
    }

    public double getOfficialRankingMeasureByCriterion(Criterion criterion) {
        return getF1(criterion, Eligibility.OVERALL);
    }

    /**
     * @param c
     * @return
     * @deprecated Use getMetrics() instead
     */
    public double[] getMetricsArray(Criterion c) {
        double[] ret = new double[NUM_TOTAL_FIELDS];

        // MET
        ret[0] = getPrecision(c, Eligibility.MET);
        ret[1] = getRecall(c, Eligibility.MET);
        ret[2] = getSpecificity(c, Eligibility.MET);
        ret[3] = getF1(c, Eligibility.MET);

        // NOT MET
        ret[4] = getPrecision(c, Eligibility.NOT_MET);
        ret[5] = getRecall(c, Eligibility.NOT_MET);
        ret[6] = getF1(c, Eligibility.NOT_MET);

        // OVERALL
        ret[7] = getF1(c, Eligibility.OVERALL);
        ret[8] = getAreaUnderCurve(c, Eligibility.OVERALL);

        return ret;
    }

    public Map<String, Double> getMetrics(Criterion c) {
        Map<String, Double> ret = new HashMap<>();

        ret.put(PRECISION_MET, getPrecision(c, Eligibility.MET));
        ret.put(RECALL_MET, getRecall(c, Eligibility.MET));
        ret.put(SPECIFICITY_MET, getSpecificity(c, Eligibility.MET));
        ret.put(F1_MET, getF1(c, Eligibility.MET));

        ret.put(PRECISION_NOT_MET, getPrecision(c, Eligibility.NOT_MET));
        ret.put(RECALL_NOT_MET, getRecall(c, Eligibility.NOT_MET));
        ret.put(F1_NOT_MET, getF1(c, Eligibility.NOT_MET));

        ret.put(F1_OVERALL, getF1(c, Eligibility.OVERALL));
        ret.put(AUC_OVERALL, getAreaUnderCurve(c, Eligibility.OVERALL));

        return ret;
    }

    public static List<String> getMetricNames() {
        List<String> ret = new ArrayList<>();

        ret.add(PRECISION_MET);
        ret.add(RECALL_MET);
        ret.add(SPECIFICITY_MET);
        ret.add(F1_MET);

        ret.add(PRECISION_NOT_MET);
        ret.add(RECALL_NOT_MET);
        ret.add(F1_NOT_MET);

        ret.add(F1_OVERALL);
        ret.add(AUC_OVERALL);

        return ret;
    }

    @Override
    public void add(at.medunigraz.imi.bst.n2c2.model.metrics.Metrics addend) {
        if (!(addend instanceof OfficialMetrics)) {
            throw new UnsupportedOperationException("Can only add metrics of the same type.");
        }

        add((OfficialMetrics) addend);
    }

    /**
     * Adds another object to this one, used e.g. for cross-validation.
     *
     * @param addend
     * @return
     */
    public void add(OfficialMetrics addend) {
        for (Criterion c : Criterion.values()) {
            for (Eligibility e : Eligibility.values()) {
                metrics.get(c).get(e).add(addend.metrics.get(c).get(e));
            }
        }
    }

    /**
     * Averages a previously added object, effectively dividing all metrics by divisor.
     *
     * @param divisor
     */
    @Override
    public void divideBy(double divisor) {
        for (Criterion c : Criterion.values()) {
            for (Eligibility e : Eligibility.values()) {
                metrics.get(c).get(e).divideBy(divisor);
            }
        }
    }

    public double getOfficialRankingMeasure() {
        return getOfficialRankingMeasureByCriterion(Criterion.OVERALL_MICRO);
    }

    public class Metrics {
        public double precision = 0;
        public double recall = 0;
        public double specificity = 0;
        public double f1 = 0;
        public double auc = 0;

        /**
         * Adds a given Metrics object to the current one.
         *
         * @param addend
         */
        public void add(Metrics addend) {
            this.precision += addend.precision;
            this.recall += addend.recall;
            this.specificity += addend.specificity;
            this.f1 += addend.f1;
            this.auc += addend.auc;
        }

        /**
         * Divide all metrics by a given divisor. Used for averaging over folds in cross-validation.
         *
         * @param divisor
         */
        public void divideBy(double divisor) {
            this.precision /= divisor;
            this.recall /= divisor;
            this.specificity /= divisor;
            this.f1 /= divisor;
            this.auc /= divisor;
        }
    }

}
