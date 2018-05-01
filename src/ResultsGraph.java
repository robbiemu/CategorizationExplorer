/**
 * modified from the basic graphing component from http://stackoverflow.com/questions/8693342/drawing-a-simple-line-graph-in-java
 * @arthor Original author: user1058210 (https://stackoverflow.com/users/1058210/user1058210)
 * Primary contributor: Rodrigo Castro
 * Latest contributor: Robert Collins
 * @version 28-4-2018
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class ResultsGraph extends JPanel implements MouseMotionListener {
    private final int PADDING = 25;
    private final int POINT_WIDTH = 40;
    private final Stroke GRAPH_STROKE = new BasicStroke(2f);

    private Color lineColor;
    private Color leftColor;
    private Color rightColor;

    private List<Pair<Double>> scores;
    private List<Pair<String>> tooltipData;
    private List<Point> graphPoints;
    private List<Boolean> categories;
    private Line categorizationLine;

    public ResultsGraph () {
        init();

        lineColor = new Color(44, 102, 230, 180);
        leftColor = new Color(200, 100, 100, 180);
        rightColor = new Color(100, 200, 100, 180);
    }
    public ResultsGraph (Color lineColor, Color leftColor, Color rightColor) {
        init();

        this.lineColor = lineColor;
        this.leftColor = leftColor;
        this.rightColor = rightColor;
    }
    public ResultsGraph (List<GraphablePoint> graphablePoints, Color lineColor, Color leftColor, Color rightColor) {
        init();

        this.setScores(graphablePoints);

        this.lineColor = lineColor;
        this.leftColor = leftColor;
        this.rightColor = rightColor;
    }

    /**
     * init - commmon helper for constructors
     */
    private void init() {
        addMouseMotionListener(this);

        resetVariables();
    }

    /**
     * resetVariables - a means to reset the graph
     */
    public void resetVariables() {
        categorizationLine = null;

        scores = new ArrayList<>();
        tooltipData = new ArrayList<>();
        categories = new ArrayList<>();
        graphPoints = new ArrayList<>();
    }

    /**
     * paintComponent - graph our data
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(PADDING, PADDING, getWidth() - 2 * PADDING, getHeight() - 2 * PADDING);
        g2.setColor(Color.BLACK);

        // create x and y axes
        g2.drawLine(PADDING, getHeight() - PADDING, PADDING, PADDING);
        g2.drawLine(PADDING, getHeight() - PADDING, getWidth() - PADDING, getHeight() - PADDING);

        // find x,y UI coordinates for normalized data
        double maxX = getMaxXScore();
        double minX = getMinXScore();

        double offsetX = 0;
        if(minX < 0) {
            offsetX = -1 * minX;
            minX = 0;
            maxX += offsetX;
        }

        double dataCenterX = (maxX - minX)/2;

        double maxY = getMaxYScore();
        double minY = getMinYScore();

        double offsetY = 0;
        if(minY < 0) {
            offsetY = -1 * minY;
            minY = 0;
            maxY += offsetY;
        }

        double dataCenterY = (maxY - minY)/2;

        // if we have data to graph:
        if(!(getMaxXScore() == getMinXScore() || getMaxYScore() == getMinYScore() || scores == null)) {
            double xScale = ((double)(getWidth() - 2 * PADDING - 2 * POINT_WIDTH)/getWidth());
            double yScale = ((double)(getHeight() - 2 * PADDING - 2 * POINT_WIDTH)/getHeight());

            double cartesianCenterX = getWidth()/2d;
            double cartesianCenterY = getHeight()/2d;

            double dataCartesianRatioX = cartesianCenterX/dataCenterX;
            double dataCartesianRatioY = cartesianCenterY/dataCenterY;

            graphPoints = new ArrayList<>();
            for (int i = 0; i < scores.size(); i++) {
                double unscaledGraphCenterX = (scores.get(i).left - dataCenterX + offsetX) * dataCartesianRatioX;
                int x1 = (int) (unscaledGraphCenterX * xScale + cartesianCenterX);
                double unscaledGraphCenterY = (scores.get(i).right - dataCenterY + offsetY) * dataCartesianRatioY;
                int y1 = (int)(getHeight() - (unscaledGraphCenterY * yScale + cartesianCenterY));

                System.out.printf("[ResultsGraph - paintComponent] {%.2f, %.2f} at {%d, %d}\n", scores.get(i).left, scores.get(i).right, x1, y1);

                graphPoints.add(new Point(x1, y1));
            }

            for (int i = 0; i < graphPoints.size(); i++) {
                g2.setColor(categories.get(i) ? leftColor : rightColor);

                int x = graphPoints.get(i).x - POINT_WIDTH / 2;
                int y = graphPoints.get(i).y - POINT_WIDTH / 2;
                int ovalW = POINT_WIDTH;
                int ovalH = POINT_WIDTH;
                g2.fillOval(x, y, ovalW, ovalH);
            }

            // if we have a line to graph
            if(categorizationLine != null) {
                g2.setColor(lineColor);
                Stroke oldStroke = g2.getStroke();
                g2.setStroke(GRAPH_STROKE);

                double xGraphScale = ((double)(getWidth() - 2 * PADDING)/getWidth());
                double yGraphScale = ((double)(getHeight() - 2 * PADDING)/getHeight());

                double unscaledGraphCenterX1 = (categorizationLine.startX - dataCenterX + offsetX) * dataCartesianRatioX;
                int x1 = (int) (unscaledGraphCenterX1 * xGraphScale + cartesianCenterX);
                double unscaledGraphCenterY1 = (categorizationLine.startY - dataCenterY + offsetY) * dataCartesianRatioY;
                int y1 = (int)(getHeight() - (unscaledGraphCenterY1 * yGraphScale + cartesianCenterY));

                double unscaledGraphCenterX2 = (categorizationLine.endX - dataCenterX + offsetX) * dataCartesianRatioX;
                int x2 = (int) (unscaledGraphCenterX2 * xGraphScale + cartesianCenterX);
                double unscaledGraphCenterY2 = (categorizationLine.endY - dataCenterY + offsetY) * dataCartesianRatioY;
                int y2 = (int)(getHeight() - (unscaledGraphCenterY2 * yGraphScale + cartesianCenterY));

                System.out.printf("[ResultsGraph - paintComponent] Line at angle %2f: "+
                                "{%.2f, %.2f},{%.2f, %.2f} at {%d, %d},{%d, %d}\n",
                        Utils.angleOf(categorizationLine),
                        categorizationLine.startX, categorizationLine.startX,
                        categorizationLine.endX, categorizationLine.endY,
                        x1,y1, x2,y2);

                g2.draw(new Line2D.Double(x1, y1, x2, y2));

                g2.setStroke(oldStroke);
            }
        }
    }

    /**
     * getMinXScore - get the minimal X value in the set
     * @return the minimal X value in the set
     */
    private double getMinXScore() {
        double minScore = Double.MAX_VALUE;
        if(scores != null) {
            for (Pair<Double> score : scores) {
                minScore = Math.min(minScore, score.left);
            }
        }
        return minScore;
    }

    /**
     * getMaxXScore - get the maximal X value in the set
     * @return the maximal X value in the set
     */
    private double getMaxXScore() {
        double maxScore = Double.MIN_VALUE;
        if(scores != null) {
            for (Pair<Double> score : scores) {
                maxScore = Math.max(maxScore, score.left);
            }
        }
        return maxScore;
    }

    /**
     * getMinYScore - get the minimal Y value in the set
     * @return the minimal Y value in the set
     */
    private double getMinYScore() {
        double minScore = Double.MAX_VALUE;
        if(scores != null) {
            for (Pair<Double> score : scores) {
                minScore = Math.min(minScore, score.right);
            }
        }
        return minScore;
    }

    /**
     * getMaxYScore - get the maximal Y value in the set
     * @return the maximal y value in the set
     */
    private double getMaxYScore() {
        double maxScore = Double.MIN_VALUE;
        if(scores != null) {
            for (Pair<Double> score : scores) {
                maxScore = Math.max(maxScore, score.right);
            }
        }
        return maxScore;
    }

    /**
     * setLine - set the bifurcation line of the graph
     * @param line - a Line representation of the categorization
     */
    public void setLine(Line line) {
        categorizationLine = line;
    }

    /**
     * setScores - given a List of GraphablePoints, set the rendering data sufficient to draw them and repaint
     * @param graphablePoints GraphablePoints to graph
     */
    public void setScores(List<GraphablePoint> graphablePoints) {
        this.scores.clear();
        this.tooltipData.clear();
        this.categories.clear();

        for(GraphablePoint p: graphablePoints) {
            this.categories.add(p.getCategory());
            this.tooltipData.add(p.getRaw());
            this.scores.add(p.getNormal());
        }

        invalidate();
        this.repaint();
    }

    public List<Pair<Double>> getScores() {
        return scores;
    }

    /**
     * getToolTipText - given a current mouse position, determine the tooltip for any point being hovered
     * @param e - MouseEvent
     * @return text or null
     */
    public String getToolTipText(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        int i = 0;
        for(Point p: graphPoints) {
            if (Math.pow(mouseX - p.x, 2) + Math.pow(mouseY - p.y, 2) < Math.pow(POINT_WIDTH / 2, 2)) {
                //System.out.println("[ResultsGraph - getToolTipText] UI:" + p.getX() + ", " + p.getY());

                return "{ " + tooltipData.get(i).left + ", " + tooltipData.get(i).right + " }";
            }
            i++;
        }
        return null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    /**
     * mouseMoved - on mouseMovement over this component, check to see if we have a tooltip to show.
     * @param e
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        setToolTipText(getToolTipText(e));
    }
}
