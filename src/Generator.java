import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generator {
    private static Random r = new Random();

    public static List<Pair<Double>> getData() {
        List<Pair<Double>> data = new ArrayList<>();

        int a = r.nextInt();
        int b = r.nextInt(Math.abs(a));

        if(a>0) {
            a *= -1;
        }

        int fill = (int) Math.sqrt(Math.sqrt(a*a + b*b));

        long samples = 16;
        while(samples < fill) {
            samples *= 2;
        }

        Pair<Pair<Double>> loci = getRandomLoci(a,b);

        while(samples-- > 0) {
            Pair<Double> point = new Pair<>(r.nextDouble(), r.nextDouble());
            data.add(adjustToLocus(
                    r.nextBoolean() ? loci.getLeft() : loci.getRight(),
                    point));
        }

        return data;
    }

    private static Pair<Double> adjustToLocus(Pair<Double> locus, Pair<Double> point) {
        return new Pair<>(locus.getLeft() * Math.sqrt(point.getLeft()),
                locus.getRight() * Math.sqrt(point.getRight()));
    }

    private static Pair<Pair<Double>> getRandomLoci(int a, int b) {
        Pair<Double> x = new Pair<>(nextDoubleInRange(-1 * a, a), nextDoubleInRange(b, -1 * b));

        double variance = nextDoubleInRange(Math.sqrt(b), -1 * Math.sqrt( -1 * a));

        double altx = nextDoubleInRange(variance, 0) * x.getLeft();
        double alty = nextDoubleInRange(variance, 0) * x.getRight();

        if(x.getLeft() > 0) {
            altx *= -1;
        }
        if(x.getRight() > 0) {
            alty *= -1;
        }
        Pair<Double> y = new Pair<>(altx, alty);

        return new Pair<>(x, y);
    }

    private static Double nextDoubleInRange(int max, int min) {
        return min + (max - min) * r.nextDouble();
    }
    private static Double nextDoubleInRange(double max, double min) {
        return min + (max - min) * r.nextDouble();
    }

    public static String[] dataAsString(List<Pair<Double>> data) {
        List<String> items = new ArrayList<>();

        for(Pair<Double> p: data) {
            System.out.println(p.toString());

            items.add(new BigDecimal(p.getLeft()).toPlainString() + " " + new BigDecimal(p.getRight()).toPlainString());
        }

        return items.toArray(new String[0]);
    }
}
