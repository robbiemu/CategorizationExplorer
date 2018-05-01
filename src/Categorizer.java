/**
 * @author Robert Collins
 * @version 29-4-2018
 */
import java.util.List;

/* abstract Categorizer sufficient to function in the CategorizationExplorer */
public abstract class Categorizer {
    public String TITLE; // THESE MUST BE UNIQUE

    /**
     * constructor - it must provide a title to display to the user.
     * @param title
     */
    public Categorizer(String title) {
        TITLE = title;
    }

    /**
     * load - it must be able to load data
     * @param data
     */
    public abstract void load (List<Pair<Double>> data);

    /**
     * learn - it must be able to learn from the data
     */
    public abstract void learn ();

    /**
     * reset - it must be able to be reset
     */
    public abstract void reset ();

    /**
     * getLine -it must be able to return the categorization line it has found
     * @return
     */
    public abstract Line getLine ();

    /**
     * getCategorizations - it must be able to return a list of cateogrizations based on the list of data it learned
     * @return
     */
    public abstract List<Boolean> getCategorizations ();
}
