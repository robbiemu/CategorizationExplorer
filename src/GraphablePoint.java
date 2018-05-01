/**
 * @author Robert Collins
 * @version 29-4-2018
 */

/* a collection of raw {x,y} data, normalized form {x,y} data, and the category for the data-point */
public class GraphablePoint {
    private Boolean category;
    private Pair<String> raw;
    private Pair<Double> normal;

    public GraphablePoint(Pair<String> raw, Pair<Double> normal, Boolean category) {
        this.raw = raw;
        this.normal = normal;
        this.category = category;
    }

    public Pair<String> getRaw() {
        return raw;
    }

    public void setRaw(Pair<String> raw) {
        this.raw = raw;
    }

    public Pair<Double> getNormal() {
        return normal;
    }

    public void setNormal(Pair<Double> normal) {
        this.normal = normal;
    }

    public Boolean getCategory() {
        return category;
    }

    public void setCategory(Boolean category) {
        this.category = category;
    }
}
