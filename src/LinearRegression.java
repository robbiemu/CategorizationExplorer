/**
 * Linear regression with Gradient Descent - modified from https://github.com/shiffman/NOC-S17-2-Intelligence-Learning/blob/master/week3-classification-regression/06_linear_regression_interactive/sketch.js
 * @author Original Author: Daniel Shiffman
 * Last modified: Robert Collins
 */

import java.util.ArrayList;
import java.util.List;

public class LinearRegression {
    public static final double LEARNING_RATE = 0.2;
    public static final int BASE_LIMIT = 1000;

    private List<Pair<Double>> data; // the data we are training from
    private Double learningRate;
    private double step;
    private int limit;

    private int iterations; // current iteration count

    // Values of b and m for linear regression
    // y = mx + b (formula for a line)
    private Double b;
    private Double m;
    private Line line;
    private double previousError;

    private Double minX;
    private Double maxX;
    private Double minY;
    private Double maxY;

    public LinearRegression() {
        limit = getLimitFromDataSize(BASE_LIMIT);

        learningRate = LEARNING_RATE;
        step = LEARNING_RATE;

        data = new ArrayList<>();

        initSlope();
    }

    public LinearRegression(List<Pair<Double>> data) {
        limit = getLimitFromDataSize(BASE_LIMIT);

        learningRate = LEARNING_RATE;
        step = LEARNING_RATE;

        this.data = data;

        initSlope();
    }

    /**
     * initSlope - prepare to begin on a new set of data
     */
    public void initSlope() {
        b = 0d;
        m = 1d;

        minX = null;
        minY = null;
        maxX = null;
        maxY = null;

        iterations = 0;

        step = learningRate;
    }

    /**
     * learn the line for the current data, up to our limit
     */
    public void learn() {
        while (iterations < limit) {
            train();
            adjustStepSize();
        }
    }

    /**
     * adjustStepSize - simple implementation of convergence from:
     * Frankl, D. (2014, December 10). Machine Learning In JavaScript [Scholarly project].
     * Retrieved from http://cs229.stanford.edu/proj2014/David Frankl,Machine Learning In JavaScript.pdf
     */
    private void adjustStepSize() {
        double error = calculateError();
        if (error > previousError) {
            step = step / 2;
        } else {
            step = step + 0.01;
        }

        if (step < (1d / Math.pow(2d, Math.sqrt(data.size())))) {
            iterations = limit;
        }

        previousError = error;
    }

    /**
     * train on the data for a single iteration
     */
    private void train() {
        double deltaB = 0;
        double deltaM = 0;
        int length = data.size();
        for (Pair<Double> p : data) {
            double x = p.getLeft();
            double y = p.getRight();

            double yGuess = m * x + b;
            double error = y - yGuess;

            deltaB += (2d / length) * error;
            deltaM += (2d / length) * x * error;
        }
        b += (deltaB * learningRate);
        m += (deltaM * learningRate);

        double x1 = minX;
        double y1 = (m * x1 + b);
        double x2 = maxX;
        double y2 = (m * x2 + b);

        line = new Line(x1, y1, x2, y2);
        iterations++;
    }

    /**
     * calculateError - A function to calculate the "loss". Formula for doing this is "sum of squared errors"
     *
     * @return loss
     */
    private double calculateError() {
        double sum = 0;
        for (Pair<Double> p : data) {
            double guess = m * p.getLeft() + b; // This is the guess based on the line
            double error = guess - p.getRight(); // The error is the guess minus the actual temperature

            sum += error * error;
        }

        return sum / data.size(); // Divide by total data points to average
    }

    /**
     * getLimitFromDataSize - a rudimentary limit function
     *
     * @param limit int - the base of the limit
     * @return the number of iterations at which we decide that we've converged enough
     */
    public int getLimitFromDataSize(Integer limit) {
        if (limit == null) {
            limit = this.limit;
        }

        int size = 0;
        if (data != null) {
            size = data.size();
        }

        return (int) (limit + Math.sqrt(size));
    }

    public Boolean isPointAboveLine(Pair<Double> p) {
        double guess = m * p.getLeft() + b; // This is the guess based on the line
        double error = guess - p.getRight(); // The error is the guess minus the actual temperature

        return error > 0;
    }

    public Line getLine() {
        return line;
    }

    public List<Pair<Double>> getData() {
        return data;
    }

    public void setData(List<Pair<Double>> data) {
        this.data = data;
        findMinsAndMaxen();
    }

    private void findMinsAndMaxen() {
        for (Pair<Double> p: data) {
            double x = p.getLeft();
            double y = p.getRight();
            if (minX == null) {
                minX = x;
                maxX = x;
                minY = y;
                maxY = y;
            } else {
                minX = Math.min(x, minX);
                maxX = Math.max(x, maxX);
                minY = Math.min(y, minY);
                maxY = Math.max(y, maxY);
            }
        }
    }

    public Double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(Double learningRate) {
        this.learningRate = learningRate;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
