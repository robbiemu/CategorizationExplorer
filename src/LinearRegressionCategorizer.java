/**
 * @author Robert Collins
 * @version 29-4-2018
 */
import java.util.ArrayList;
import java.util.List;

/* the Categorizer wrapper for the LinearRegression class */
public class LinearRegressionCategorizer extends Categorizer {
    public static final String TYPE = "Linear Regression";
    private LinearRegression lr;

    public LinearRegressionCategorizer () {
        super(TYPE);
        lr = new LinearRegression();
    }

    @Override
    public void load(List<Pair<Double>> data) {
        reset();
        lr.setData(data);
    }

    @Override
    public void learn() {
        lr.learn();
    }

    @Override
    public void reset() {
        lr.initSlope();
    }

    @Override
    public Line getLine() {
        return lr.getLine();
    }

    @Override
    public List<Boolean> getCategorizations() {
        List<Boolean> categories = new ArrayList<>();

        for(Pair<Double> p: lr.getData()) {
            categories.add(lr.isPointAboveLine(p));
        }

        return categories;
    }
}
