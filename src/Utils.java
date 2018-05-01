/**
 * @author Robert Collins
 * @version 29-4-2018
 */
import java.util.Arrays;
import java.util.List;

/* a collection of utility methods for the Categorization Explorer */
public class Utils {
    public static final String NUMERIC = "[-+]?\\d+(?:\\.\\d+)?";
    public static final String CARTESIAN_SPLIT = "(?:(?:, ?)| )";
    public static final String CARTESIAN_FORMAT = "^(?:\\w+|"+NUMERIC+")"+CARTESIAN_SPLIT+"(?:\\w+|"+NUMERIC+")$";

    /**
     * isNumeric - returns true IFF str is numeric
     * taken wholesale from https://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-numeric-in-java
     *
     * @param str string to test
     * @return boolean
     */
    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch(NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * angleOf - given a non-UI line, determine it's positive angle in cartesian coordinates
     * @param line Line with start and end points
     * @return double angle
     */
    public static double angleOf(Line line) {
        System.out.print("[Utils - angleOf] Finding angle for line: " + line.toString());
        double radians = Math.atan2(
                line.getEndY() - line.getStartY(),
                line.getEndX() - line.getStartX());

        double angle = Math.toDegrees(radians);

        if(angle < 0){
            angle += 360;
        }

        System.out.println(". angle is " + angle);

        return angle;
    }

    /**
     * isValidPoint - check if the string representation of a point is valid for consideration
     * @param point the string representation of a point
     * @return boolean
     */
    static boolean isValidPoint(String point) {
        return point.matches(CARTESIAN_FORMAT);
    }

    /**
     * getColTypes - generate a list of the types of each column in the data
     *
     * @param stringyData
     * @return
     */
    static String[] getColTypes(List<String[]> stringyData) {
        String [] colTypes = new String [stringyData.get(0).length];
        for(int i = 0; i < colTypes.length; i++) {
            colTypes[i] = Standardizer.NUMERIC;
        }

        int breaker = 0;
        for(String[] a: stringyData) {
            int i = 0;
            for(String s: a) {
                if(!isNumeric(s)) {
                    colTypes[i] = Standardizer.CATEGORICAL;
                    if(++breaker == colTypes.length) {
                        break;
                    }
                }
                i++;
            }
            if(++breaker == colTypes.length) {
                break;
            }
        }

        System.out.println("[Utils - getColTypes] " + Arrays.toString(colTypes));

        return colTypes;
    }

    // the following two methods are from https://github.com/mybreeze77/Simple-KNN-with-Java8/blob/master/src/main/java/com/sample/DistanceUtil.java
    /**
     * Euclidean distance algorithm
     * d(i,j)=sqrt((x1-x2)^2+(y1-y2)^2)
     * @param d1 the first vector
     * @param d2 the second vector
     * @return distance
     */
    public static double euclidean(double[] d1, double[] d2) {
        if(d1.length != d2.length) {
            throw new RuntimeException("The length of characters are not identical!");
        }

        double sum = 0;
        for(int i = 0; i < d1.length; i++) {
            sum += Math.pow(d1[i] - d2[i], 2);
        }

        return Math.sqrt(sum);
    }

    /**
     * Manhattan distance algorithm
     * d(i,j)=|X1-X2|+|Y1-Y2|
     * @param d1 the first vector
     * @param d2 the second vector
     * @return distance
     */
    public static double manhattan(double[] d1, double[] d2) {
        if(d1.length != d2.length) {
            throw new RuntimeException("The length of characters are not identical!");
        }

        double sum = 0;
        for(int i = 0; i < d1.length; i++) {
            sum += Math.abs(d1[i] - d2[i]);
        }

        return sum;
    }

    public static double[] dataTupleToArray(Pair<Double> r) {
        return new double [] {r.getLeft(), r.getRight()};
    }
}
