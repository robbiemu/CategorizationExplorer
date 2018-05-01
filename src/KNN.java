/**
 * @author Robert Collins
 * @version 29-4-2018
 */

import java.util.*;

/* kNN categorizer class */
public class KNN {
    public static final int DEFAULT_NUMBER_OF_NEIGHBORS = 4;

    private List<KNNNode> data; // the data we are training from
    private int numberOfNeighbors;

    private List<Boolean> classifications; // the result

    public KNN () {
        numberOfNeighbors = DEFAULT_NUMBER_OF_NEIGHBORS;
        init();
    }

    /**
     * init - method to reset the regressor
     */
    public void init() {
        this.data = null;
        this.classifications = null;
    }

    /**
     * classify - produce a categorization of the data
     * @return - list of categories from the data
     */
    public List<Boolean> classify () {
        classifications = new ArrayList<>();
        for(KNNNode node: data) {
            double [] n = Utils.dataTupleToArray(node.getData());
            Map<Double, Boolean> neighbors = new HashMap<>();
            // first we need to find the nearest N neighbors
            for(KNNNode c: data) {
                if (c.equals(n)) { continue; }

                Pair<Double> cd = c.getData();
                double distance = Utils.euclidean(Utils.dataTupleToArray(cd), n);

                if(neighbors.size() < numberOfNeighbors) {
                    neighbors.put(distance, c.getCategory());
                } else {
                    List<Double> keys = Arrays.asList(neighbors.keySet().toArray(new Double[0]));
                    keys.sort(Comparator.naturalOrder());

                    int j = 0;
                    while(j < numberOfNeighbors && keys.get(j) > distance) {
                        j++;
                    }

                    if(j <= numberOfNeighbors && j > 0) {
                        neighbors.remove(keys.get(j-1));
                        neighbors.put(distance, c.getCategory());
                    }
                }
            }

            // now we take the average of the labels and set the label for the point
            int parity = 0;
            for(Boolean v: neighbors.values()) {
                if(v) {
                    parity++;
                } else {
                    parity--;
                }
            }
//            if((parity > 0) != node.getCategory()) {
//                System.out.println("[KNN classify] " + node.getData().toString() + " changed sides!");
//            } else {
//                System.out.println("[KNN classify] parity: " + parity);
//            }

            classifications.add(parity > 0);
        }
        return null;
    }


    public List<KNNNode> getData() {
        return data;
    }

    public void setData(List<KNNNode> data) {
        this.data = data;
        if(data == null) { return; }
        Collections.shuffle(this.data);
    }

    public int getNumberOfNeighbors() {
        return numberOfNeighbors;
    }

    public void setNumberOfNeighbors(int numberOfNeighbors) {
        this.numberOfNeighbors = numberOfNeighbors;
    }

    public List<Boolean> getClassifications() {
        return classifications;
    }
}
