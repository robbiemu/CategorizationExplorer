/**
 * @author Robert Collins
 * @version 29-4-2018
 */
import java.util.ArrayList;
import java.util.List;

/* the Categorizer wrapper for the KNN class  */
public class KNNCategorizer extends Categorizer {
    public static final String TYPE = "kNN (unary, with LR labels)";
    private KNN knn;

    public KNNCategorizer () {
        super(TYPE);
        knn = new KNN();
    }

    /* kNN requires labels, so first we pre-categorize the data */
    @Override
    public void load(List<Pair<Double>> data) {
        List<KNNNode> nodes = new ArrayList<>();

        LinearRegression lr = new LinearRegression();
        lr.setData(data);
        lr.learn();

        for(Pair<Double> p: lr.getData()) {
            nodes.add(new KNNNode(p, lr.isPointAboveLine(p)));
        }

        knn.setData(nodes);
    }

    @Override
    public void learn() {
        knn.classify();
    }

    @Override
    public void reset() {
        knn.init();
    }

    /* there is no linear bifurcation to kNN */
    @Override
    public Line getLine() {
        return null;
    }

    @Override
    public List<Boolean> getCategorizations() {
        return knn.getClassifications();
    }
}
