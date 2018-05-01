/**
 * ported from https://visualstudiomagazine.com/Articles/2014/01/01/How-To-Standardize-Data-for-Neural-Networks.aspx
 * @arthor Original author: Dr. James McCaffrey
 * Latest contributor: Robert Collins
 * @version 28-4-2018
 */
import java.util.*;

/* the Standardizer class normalizes numeric and categorical data given columnar definitions */
public class Standardizer {
    public static final String NUMERIC = "numeric";
    public static final String CATEGORICAL = "categorical";

    private static final boolean COLLAPSED = true; // toggle multivariate categorization

    private List<String>[] distinctValues;
    private String [] colTypes;
    private String [] subTypes;
    private double [] means;
    private double [] stdDevs;
    private int numStandardCols;

    public Standardizer(String[][] rawData, String[] colTypes) {
        this.colTypes = new String[colTypes.length];
        System.arraycopy( colTypes, 0, this.colTypes, 0, colTypes.length );

        // get distinct values in each col.
        int numCols = rawData[0].length;
        this.distinctValues = new ArrayList[numCols];
        for (int j = 0; j < numCols; ++j) {
            Set<String> set = new LinkedHashSet<>();
            if (colTypes[j] == NUMERIC) {
                set.add(NUMERIC);
            } else {
                for (int i = 0; i < rawData.length; ++i) {
                    set.add(rawData[i][j]);
                }
            }
            this.distinctValues[j] = new ArrayList<String>(Arrays.asList(set.toArray(new String[0])));
        }

        // compute means of numeric cols
        this.means = new double[numCols];
        for (int j = 0; j < numCols; ++j) {
            if (colTypes[j] == CATEGORICAL) {
                this.means[j] = -1.0; // dummy values
            } else {
                double sum = 0.0;
                for (int i = 0; i < rawData.length; ++i) {
                    double v = Double.valueOf(rawData[i][j]);
                    sum += v;
                }
                this.means[j] = sum / rawData.length;
            }
        }

        // compute standard deviations of numeric cols
        this.stdDevs = new double[numCols];
        for (int j = 0; j < numCols; ++j) {
            if (colTypes[j] == CATEGORICAL) {
                this.stdDevs[j] = -1.0; // dummy
            } else {
                double ssd = 0.0; // sum of squared deviations
                for (int i = 0; i < rawData.length; ++i) {
                    double v = Double.valueOf(rawData[i][j]);
                    ssd += (v - this.means[j]) * (v - this.means[j]);
                }
                this.stdDevs[j] = Math.sqrt(ssd / rawData.length);
            }
        }

        // compute column subTypes
        this.subTypes = new String[numCols];
        for (int j = 0; j < numCols; ++j) {
            if (colTypes[j] == NUMERIC && j != numCols - 1)
                this.subTypes[j] = "numericX";
            else if (colTypes[j] == NUMERIC && j == numCols - 1)
                this.subTypes[j] = "numericY";
            else if (colTypes[j] == CATEGORICAL && j != numCols - 1 &&
                    distinctValues[j].size() == 2)
                this.subTypes[j] = "binaryX";
            else if (colTypes[j] == CATEGORICAL && j == numCols - 1 &&
                    distinctValues[j].size() == 2)
                this.subTypes[j] = "binaryY";
            else if (colTypes[j] == CATEGORICAL && j != numCols - 1 &&
                    distinctValues[j].size() >= 3)
                this.subTypes[j] = "categoricalX";
            else if (colTypes[j] == CATEGORICAL && j == numCols - 1 &&
                    distinctValues[j].size() >= 3)
                this.subTypes[j] = "categoricalY";
        }

        System.out.println("{Standardizer] subtypes: " + Arrays.toString(this.subTypes));

        // compute number of columns of standardized data
        int ct = 0;
        for (int j = 0; j < numCols; ++j) {
            if (this.subTypes[j] == "numericX")
                ++ct;
            else if (this.subTypes[j] == "numericY")
                ++ct;
            else if (this.subTypes[j] == "binaryX")
                ++ct;
            else if (this.subTypes[j] == "binaryY")
                ct += 2;
            else if (this.subTypes[j] == "categoricalX")
                ct += distinctValues[j].size() - 1;
            else if (this.subTypes[j] == "categoricalY")
                ct += distinctValues[j].size();
        }
        this.numStandardCols = ct;
    }

    /**
     * getStandardRow - normalizes numeric and categorical data to a 0 to 1 range
     * ex: "30 male 38000.00 suburban democrat" ->
     * [ -0.25 -1.0 -0.75 (1.0  0.0) (0.0  0.0  0.0  1.0) ]
     * @param tuple String[] of data to normalize
     * @return normalized data
     */
    public double[] getStandardRow(String[] tuple) {
        double[] result = new double[this.numStandardCols];

        int p = 0; // ptr into result data
        for (int j = 0; j < tuple.length; ++j) {
            if (this.subTypes[j] == "numericX") {
                double v = Double.valueOf(tuple[j]);
                if(this.stdDevs[j] == 0) {
                    result[p++] = v;
                } else {
                    result[p++] = (v - this.means[j]) / this.stdDevs[j]; // Gaussian normalize
                }
            } else if (this.subTypes[j] == "numericY") {
                double v = Double.valueOf(tuple[j]);
                result[p++] = v; // leave alone (regression problem)
            }
            else if (this.subTypes[j] == "binaryX") {
                String v = tuple[j];
                int index = distinctValues[j].indexOf(v); // 0 or 1. binary x-data -> -1 +1
                if (index == 0) {
                    result[p++] = -1.0;
                } else {
                    result[p++] = 1.0;
                }
            } else if (this.subTypes[j] == "binaryY") { // y-data is 'male' or 'female'
                String v = tuple[j];
                int index = distinctValues[j].indexOf(v); // 0 or 1. binary x-data -> -1 +1
                if (index == 0) {
                    result[p++] = 0.0;
                    result[p++] = 1.0;
                } else {
                    result[p++] = 1.0;
                    result[p++] = 0.0;
                }
            } else if (this.subTypes[j] == "categoricalX") { // ex: x-data is 'democrat' 'republican' 'independent' 'other'
                String v = tuple[j];
                int index = distinctValues[j].indexOf(v); // 0, 1, 2, 3
                if(COLLAPSED) {
                    result[p++] = index;
                } else {
                    int ct = distinctValues[j].size(); // ex: 4
                    double [] tmp = new double[ct-1]; // [ _ _ _ ]

                    if (index == ct - 1) { // last item goes to -1 -1 -1 (effects coding)
                        for (int k = 0; k < tmp.length; ++k) {
                            tmp[k] = -1.0;
                        }
                    } else {
                        tmp[ct - index - 2] = 1.0; // a bit tricky
                    }
                    // copy tmp values into result
                    for (int k = 0; k < tmp.length; ++k) {
                        result[p++] = tmp[k];
                    }
                }
            } else if (this.subTypes[j] == "categoricalY") {
                String v = tuple[j];
                int index = distinctValues[j].indexOf(v); // 0, 1, 2, 3
                if(COLLAPSED) {
                    result[p++] = index;
                } else {
                    int ct = distinctValues[j].size(); // ex: 4
                    double [] tmp = new double[ct]; // [ _ _ _ _ ]

                    tmp[ct - index - 1] = 1.0;
                    for (int k = 0; k < tmp.length; ++k) {
                        result[p++] = tmp[k];
                    }
                }
            }
        } // each j col
        return result;
    } // getStandardRow

    public double [][] standardizeAll(String[][] rawData) {
        double [][] result = new double[rawData.length][];
        for (int i = 0; i < rawData.length; ++i) {
            double [] stdRow = getStandardRow(rawData[i]);
            result[i] = stdRow;
        }

//        String aString = "";
//        for(int row = 0; row < result.length; row++) {
//            aString += "{";
//            for(int col = 0; col < result[row].length; col++) {
//                aString += " " + result[row][col];
//            }
//            aString += "} ";
//        }
//        System.out.println("[Standardizer - standardizeAll] " + aString);

        return result;
    }
}
