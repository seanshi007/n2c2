package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HbA1c extends BaseClassifiable {

    private static final Logger LOG = LogManager.getLogger();

    private static final double MIN_VALUE = 6.5;
    private static final double MAX_VALUE = 9.5;

    //	final public String[] a_CriterionID_hba1c = {"hba1c","HB Alc","HgAlC","HbA1c","HBA1c", "Hemoglobin A1C", "Hgb A1c", "hemoglobin A1c"};

    // 117.xml: Hemoglobin A1C 8.5
    // 147.xml: hemoglobin A1c was 7.4 %
    // 367.xml: HBA1C 7.2
    // sample.xml: Hgb A1c 7.30
    // We match only on [0-9] because 10.x would be out anyway...
    private static final Pattern REGEX = Pattern.compile("A1C (?:was )?([0-9]{1,2}\\.[0-9]{1,2})", Pattern.CASE_INSENSITIVE);

	@Override
    public Eligibility isMet(Patient p) {
        Matcher matcher = REGEX.matcher(p.getText());
        while (matcher.find()) {
            String group = matcher.group(1);
            LOG.debug("Got a match for {}", group);

            double value = parseValue(group);
            if (value >= MIN_VALUE && value <= MAX_VALUE) {
                return Eligibility.MET;
            }
        }

		return Eligibility.NOT_MET;
    }

    private double parseValue(String group) {
        double value = 0;
        try {
            value = Double.valueOf(group);
        } catch (NumberFormatException e) {
            LOG.error("Could not parse {} into a double", group);
        }
        return value;
    }
	
}
