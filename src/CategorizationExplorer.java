/**
 * CSCI E 10b - Professor Leitner, Harvard University
 * Graduate Project
 * @author Robert Collins
 * @version 29-4-2018
 */

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

/* runtime for our categorization explorer */
public class CategorizationExplorer {
    private static final String APP_TITLE = "Categorization Explorer";
    private static final String DEFAULT_TITLE = "Categorization Explorer";
    private static final String LOADFILE_CTA = "Load File";

    public static final Font TITLE_FONT = new Font("Verdana", Font.BOLD, 40);
    public static final Font SYSTEM_FONT = new Font("Verdana", Font.PLAIN, 24);

    private static JFrame frame;
    private static JPanel grid;
    private static GridBagConstraints gbc;

    private static JLabel label_title;
    private static JComboBox<String> select_method;
    private static ResultsGraph graph;
    private static JTextField input_testPoint;
    private static JButton button_loadFile;

    private static List<String []> stringyData;
    private static double [][] normalizedData;
    private static List<GraphablePoint> graphablePoints;
    private static String categorizer;
    private static String [] methodLabels;
    private static List<Categorizer> categorizers;

    public static void main(String[] args) {
        categorizers = new ArrayList<>();
        loadCategorizers();
        loadMethodLabels();
        categorizer = methodLabels[0];

        cleanSlate();

        initUI();
        initGrid();

        // title
        gbc.gridwidth = 2;
        label_title = new JLabel(DEFAULT_TITLE);
        label_title.setFont(TITLE_FONT);
        grid.add(label_title, gbc);

        // combo for categorization method
        gbc.gridwidth = 1;
        gbc.gridx = 2;
        gbc.insets = new Insets(12,6,6,25);
        select_method = new JComboBox<>(methodLabels);
        select_method.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) select_method.getSelectedItem();
                selectMethod(selected);
            }
        });
        grid.add(select_method, gbc);

        // graph
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(1,1,1,1);
        graph = new ResultsGraph();
        graph.setPreferredSize(new Dimension(1600, 900));
        grid.add(graph, gbc);

        // test point input
        gbc.gridwidth = 2;
        gbc.gridy = 2;
        gbc.insets = new Insets(1,25,25,25);
        input_testPoint = new JTextField();
        input_testPoint.addKeyListener(
                new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        considerKey(e);
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        considerKey(e);
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        considerKey(e);
                    }

                    private void considerKey(KeyEvent e) {
                        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                            String point = input_testPoint.getText();
                            if (Utils.isValidPoint(point)) {
                                addTestPoint(point);
                                onceStringyDataChanged();

                                input_testPoint.setText("");
                            }
                        }
                    }
                });
        grid.add(input_testPoint, gbc);

        // load training data button
        gbc.gridwidth = 1;
        gbc.gridx = 2;
        button_loadFile = new JButton(LOADFILE_CTA);
        button_loadFile.setBackground(Color.WHITE);
        button_loadFile.setForeground(Color.BLUE);
        button_loadFile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLUE, 2),
                BorderFactory.createLineBorder(Color.WHITE, 2)
        ));
        button_loadFile.setPreferredSize(new Dimension(250,40));
        button_loadFile.addActionListener(new ActionListener() {
            private JFileChooser fileChooser;

            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser = new JFileChooser();

                int returnVal = fileChooser.showOpenDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    //This is where a real application would open the file.
                    System.out.println("[CategorizationExplorer - loadFile actionPerformed] Opening: " + file.getName() + ".");

                    processFile(file);
                } else {
                    System.out.println("[CategorizationExplorer - loadFile actionPerformed] Open command cancelled by user.");
                }

            }
        });
        grid.add(button_loadFile, gbc);

        loadInitialRandomData();

        startUI();
    }

    /**
     * loadMethodLabels - when categorizers have loaded, this is used to populate the dropdown menu for selecting one
     * of them.
     */
    private static void loadMethodLabels() {
        List<String> labels = new ArrayList<>();
        for(Categorizer c: categorizers) {
            labels.add(c.TITLE);
        }
        methodLabels = labels.toArray(new String[categorizers.size()]);
        //System.out.println("[CategorizationExplorer - loadMethodLabels] " + Arrays.toString(methodLabels));
    }

    /**
     * loadCategorizers - for now, we're just manually adding categorizers here
     *
     * Each categorizer must extend the Categorizer abstract class
     */
    private static void loadCategorizers() {
        categorizers.add(new LinearRegressionCategorizer());
        categorizers.add(new KNNCategorizer());
    }

    /**
     * loadInitialRandomData - get some initial data to show
     */
    private static void loadInitialRandomData() {
        String [] items = Generator.dataAsString(Generator.getData());

        for(String point: items) {
            if (Utils.isValidPoint(point)) {
                addTestPoint(point);
            } else {
                System.out.println("invalid point" + point);
            }
        }
        onceStringyDataChanged();
    }


    /**
     * processFile - try to treat a file as a set of data points for categorization
     *
     * @param file
     */
    private static void processFile(File file) {
        String [] items = null;
        try {
            Scanner sc = new Scanner(file);
            List<String> lines = new ArrayList<String>();
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                lines.add(line);
            }

            items = lines.toArray(new String[0]);

            label_title.setText(file.getName());
        } catch (Exception e) {
            e.printStackTrace();

            label_title.setText(DEFAULT_TITLE);

            items = Generator.dataAsString(Generator.getData());
        } finally {
            cleanSlate();
        }

        for(String point: items) {
            if (Utils.isValidPoint(point)) {
                addTestPoint(point);
            }
        }
        onceStringyDataChanged();
    }

    /**
     * cleanSlate - reset the state so we can start again
     */
    private static void cleanSlate() {
        stringyData = new ArrayList<>();
        normalizedData = null;
        graphablePoints = new ArrayList<>();
    }

    /**
     * addTestPoint - given a valid string representation of a test point, add it to our set
     * @param pointString
     */
    private static void addTestPoint(String pointString) {
        String [] xy = pointString.split(Utils.CARTESIAN_SPLIT);
        stringyData.add(xy);
    }

    /**
     * onceStringDataChanged - generate matching normalized data. Learn on the set and categorize them. Generate
     * graphable points and the slope if valid. Graph them.
     */
    private static void onceStringyDataChanged() {
        graph.resetVariables();

        String [][] matrix = stringyData.toArray(new String[stringyData.size()][]);

        Standardizer normalizer = new Standardizer(matrix, Utils.getColTypes(stringyData));
        normalizedData = normalizer.standardizeAll(matrix);

        Categorizer cat = null;
        for(Categorizer c: categorizers) {
            if(c.TITLE.equals(categorizer)) {
                cat = c;
                break;
            }
        }

        List<Boolean> categories = categorizeData(cat);
        buildGraphablePoints(categories);

        graph.setScores(graphablePoints);

        graph.setLine(cat.getLine());
    }

    /**
     * categorizeData - given a set of normalized data, learn from the set and produce a List<boolean> categoication map
     * @param c - the Categorizer to use
     * @return List<Boolean> cateogorization matching index-wise the data
     */
    private static List<Boolean> categorizeData(Categorizer c) {
        if(c == null) { return null; }

        List<Pair<Double>> data = new ArrayList<>();
        for(double [] nd: normalizedData) {
            data.add(new Pair<Double>(nd[0], nd[1]));
        }

        c.reset();
        c.load(data);

        c.learn();

        return c.getCategorizations();
    }

    /**
     * buildGraphablePoints - given a map of cateogrizations, generate the GraphablePoint for that data (that is,
     * raw form, normal form, and category). These get added to the graphablePoints List.
     * @param categories - the categorization for the data
     */
    private static void buildGraphablePoints(List<Boolean> categories) {
        graphablePoints = new ArrayList<>();
        int i = 0;
        for(String [] data: stringyData) {
            Pair<String> rawData = new Pair(data);
            Pair<Double> normalData = new Pair(normalizedData[i][0], normalizedData[i][1]);

            graphablePoints.add(new GraphablePoint(rawData, normalData, categories.get(i)));

            i++;
        }
    }

    /**
     * selectMethod - method selected has changed.
     * @param selected - String the categorization to use
     */
    private static void selectMethod(String selected) {
        categorizer = selected;
        if(normalizedData != null) {
            onceStringyDataChanged();
        }
    }

    /**
     * setUIFont - set up UI fonts for .. many of the UI elements of the app.
     * @param f a FontUIResource (based on a font) to set as default
     */
    public static void setUIFont (FontUIResource f){
        Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get (key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put (key, f);
        }
    }

    /**
     * initGrid - setup layout constraints
     */
    private static void initGrid() {
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(1,1,1,1);
        gbc.weightx = 1;
        gbc.weighty = 1;

        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        gbc.gridx = 0;
        gbc.gridy = 0;
    }

    /**
     * initUI - prep UI for layout
     */
    private static void initUI() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setTitle(APP_TITLE);

        grid = new JPanel();
        grid.setLayout(new GridBagLayout());

        setUIFont (new javax.swing.plaf.FontUIResource(SYSTEM_FONT));

        UIManager.put("ToolTip.background", new ColorUIResource(255, 247, 200)); //#fff7c8
        Border border = BorderFactory.createLineBorder(new Color(76,79,83));    //#4c4f53
        UIManager.put("ToolTip.border", border);
        UIManager.put("ToolTip.font", SYSTEM_FONT);

        ToolTipManager.sharedInstance().setDismissDelay(15000); // 15 second delay
    }

    /**
     * startUI - display the UI after init and layout
     */
    private static void startUI() {
        frame.add(grid);
        frame.pack();

        frame.setVisible(true);
    }
}
