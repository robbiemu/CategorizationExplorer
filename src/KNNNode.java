/**
 * @author Robert Collins
 * @version 29-4-2018
 */

/* kNN node's have data and a label */
public class KNNNode {
    private Pair<Double> data;
    private Boolean category;

    public KNNNode() {
        data = null;
        category = null;
    }
    public KNNNode(Pair<Double> data) {
        this.data = data;
    }
    public KNNNode(Pair<Double> data, Boolean category) {
        this.data = data;
        this.category = category;
    }

    public Pair<Double> getData() {
        return data;
    }

    public void setData(Pair<Double> data) {
        this.data = data;
    }

    public Boolean getCategory() {
        return category;
    }

    public void setCategory(Boolean category) {
        this.category = category;
    }
}
