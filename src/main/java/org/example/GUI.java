package org.example;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.IconUIResource;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.zip.CheckedOutputStream;

// ┌──────────────────────────────────────────────┐
// │ Project by Etian Križman 89201173 2024/25 ®  │
// │ github: https://github.com/etiank/kerneling2 │
// │ The kernel image processing project reborn   │
// └──────────────────────────────────────────────┘

/*      TO DO

    - figure out how to get JTable to display
    - dropdown menu
    - find new more interesting kernels

*/

public class GUI {

    public static void main(String[] args) {
        new GUI();
    }

    //SeqKerneling seq = new SeqKerneling();
    //ParKerneling par = new ParKerneling();
    //DistrKerneling distr = new DistrKerneling();
    private static String selectedMode = "";
    private static String selectedKernel = "Custom";
    private static BufferedImage image;

    static boolean enableTable = false;
    static String directory = "";
    static String fileName = "";
    static float[][] kernel =  new float[][] { // DEFAULT KERNEL IS IDENTITY
            {0, 0, 0},
            {0, 1, 0},
            {0, 0, 0}
    };



    public GUI() {

        // FRAME THE FRAME
        JFrame frame = new JFrame("Process image kerneling");
        ImageIcon icon = new ImageIcon("icon.jpg");
        frame.setIconImage(icon.getImage());
        frame.setSize(620,400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints grid = new GridBagConstraints();


/*        JPanel panel1 = new JPanel(new GridLayout());
        JPanel panel2 = new JPanel(); panel2.setPreferredSize(new Dimension(620, 300));
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));
        JPanel panel3 = new JPanel();
        JPanel panel4 = new JPanel(); panel4.setLayout(new BoxLayout(panel4,  BoxLayout.PAGE_AXIS));
        JPanel panel5 = new JPanel();
        JPanel panel6 = new JPanel(new FlowLayout());
        JPanel panel7 = new JPanel(); panel7.setLayout(new BoxLayout(panel7, BoxLayout.X_AXIS));*/



        // RADIO REVERIE
        JRadioButton seqRadio = new JRadioButton("Sequential");
        JRadioButton parRadio = new JRadioButton("Parallel");
        JRadioButton distrRadio = new JRadioButton("Distributed");
        ButtonGroup group = new ButtonGroup();
        group.add(seqRadio); group.add(parRadio); group.add(distrRadio);


        // JTABLE
        DefaultTableModel tableModel = new DefaultTableModel(3,3);
        JTable matrixTable = new JTable(tableModel);
        matrixTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        matrixTable.getColumnModel().getColumn(1).setPreferredWidth(30);
        matrixTable.getColumnModel().getColumn(2).setPreferredWidth(30);


        // DROPDOWN MENU - JComboBox
        JLabel kernelLabel = new JLabel("Filter: ");
        kernelLabel.setBorder(BorderFactory.createLineBorder(Color.black));
        JComboBox<String> kernelMode = new JComboBox<>(
                new String[]{"Custom", "Sharpen", "Box blur", "Gaussian blur", "Edge detection", "Emboss"});
        kernelMode.addActionListener((e) -> {
            GUI.selectedKernel = (String) kernelMode.getSelectedItem();
            if (GUI.selectedKernel != "Custom"){GUI.enableTable = false;}
            else {GUI.enableTable = true;}
            matrixTable.setEnabled(enableTable);
            System.out.println("Selected kernel: " + selectedKernel);
        });
        kernelMode.setBorder(BorderFactory.createLineBorder(Color.black));

        // SEPARATOR
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(10,100));


        // IMAGE CURATOR
        JLabel imageLabel = new JLabel("Selected image: ");
        //imageLabel.setBorder(BorderFactory.createLineBorder(Color.black));
        JButton selectImage = new JButton("none");
        //selectImage.setBorder(BorderFactory.createLineBorder(Color.black));

        JLabel picLabel = new JLabel();
        JPanel imagePanel = new JPanel(); imagePanel.setSize(10,10); imagePanel.add(picLabel);

        selectImage.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) { // OPEN EXPLORER
                FileDialog fileDialog = new FileDialog((Frame) null, "Select an Image");
                fileDialog.setVisible(true);

                // CHOOSE PIC
                directory = fileDialog.getDirectory();
                fileName = fileDialog.getFile();

                // PRINT NAME & DIR
                if (GUI.fileName != null) {
                    System.out.println("Selected file: " + directory + fileName);
                    selectImage.setText(fileName);
                }
                Image image2 = null;
                try {
                    image = ImageIO.read(new File(directory+fileName));
                    if (image.getHeight()>image.getWidth()) {
                        double ratio = (double) image.getHeight()/image.getWidth();
                        System.out.println(ratio);
                        image2 = image.getScaledInstance((int)(200/ratio),200 , Image.SCALE_FAST);
                    }else {
                        double ratio = (double) image.getWidth()/image.getHeight();
                        System.out.println(ratio);
                        image2 = image.getScaledInstance(200,(int)(200/ratio), Image.SCALE_FAST);
                    }

                } catch (IOException ex) {
                    System.out.println("An error occured while trying to load the image");
                }
                System.out.println("Image2 size: "+ image2.getWidth(null)+"x"+image2.getHeight(null));
                picLabel.setIcon(new ImageIcon(image2));// picLabel.setSize(10,10);
                System.out.println(image.getWidth());
                System.out.println(image.getHeight());
                picLabel.setMaximumSize(new Dimension(10,10));

            }
        });
        grid.gridx = 3; grid.gridy = 1; grid.gridwidth = 2; grid.gridheight = 4;
        grid.fill=GridBagConstraints.BOTH; panel.add(imagePanel,grid);


        // CLUSTER OF SWITCHES
        JButton runButton = new JButton("▶");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                switch (selectedKernel){
                    case "Custom":

                        for (int i = 0; i < 3; i++) {
                            for (int j = 0; j < 3; j++) {
                                Object value = matrixTable.getValueAt(i,j);
                                GUI.kernel[i][j] = Float.parseFloat(value.toString());
                            }
                        }

                        break;
                    case "Sharpen":
                        kernel = new float[][] {
                                { 0, -1, 0},
                                {-1, 5, -1},
                                { 0, -1, 0}
                        };
                        break;
                    case "Box blur":
                        kernel = new float[][] {
                                {1,1,1},
                                {1,1,1},
                                {1,1,1}
                        };
                        for (int i = 0; i < kernel.length; i++) {
                            for (int j = 0; j < kernel[i].length; j++) kernel[i][j] = kernel[i][j] * (float)(1.0/9);
                        }

                        break;
                    case "Gaussian blur":
                        GUI.kernel = new float[][] {
                                {1, 2, 1},
                                {2, 4, 2},
                                {1, 2, 1}
                        };
                        for (int i = 0; i < GUI.kernel.length; i++) {
                            for (int j = 0; j < GUI.kernel[i].length; j++) GUI.kernel[i][j] = GUI.kernel[i][j] * (float)(1.0/16);
                        }

                        break;
                    case "Edge detection":
                        GUI.kernel = new float[][] {
                                {-1, -1, -1},
                                {-1,  8, -1},
                                {-1, -1, -1}
                        };
                        break;
                    case "Emboss":
                        GUI.kernel = new float[][]{
                                {-2,-1, 0},
                                {-1, 1, 1},
                                { 0, 1, 2}
                        };
                        break;
                    default:
                        System.out.println("Kernel not specified. Identity kernel will be used.");
                }


                // Check which radio button is selected
                if (seqRadio.isSelected()) {
                    selectedMode = "Sequential";
                } else if (parRadio.isSelected()) {
                    selectedMode = "Parallel";
                } else if (distrRadio.isSelected()) {
                    selectedMode = "Distributed";
                } else {
                    System.out.println("No option is selected");
                }
                System.out.println( selectedMode + " mode is selected");
            }
        });



        // GLYPH CASTER
        JTextArea textArea = new JTextArea();
        textArea.setAutoscrolls(true);
        textArea.setBackground(new Color(255,255,255));
        textArea.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
        textArea.setFont(new Font("Lucida Console", Font.PLAIN, 12));
        textArea.append("[Console logs]\n\n");
        textArea.setLineWrap(true);

        //textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setMaximumSize(new Dimension(500,400));
        JScrollBar bar = new JScrollBar(JScrollBar.VERTICAL);
        scrollPane.setVerticalScrollBar(bar);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.setAutoscrolls(true);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        //scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // THE TYRANNY OF THE GRID
        //radio buttons
        setConstraints(grid,0,1,1,1,GridBagConstraints.BOTH); panel.add(seqRadio, grid);
        setConstraints(grid,0,2,1,1,GridBagConstraints.BOTH); panel.add(parRadio, grid);
        setConstraints(grid,0,3,1,1,GridBagConstraints.BOTH); panel.add(distrRadio, grid);

        setConstraints(grid,0,5,5,1,GridBagConstraints.BOTH); panel.add(scrollPane, grid);
        setConstraints(grid,1,1,1,1,GridBagConstraints.BOTH); panel.add(matrixTable, grid); //🔁
        setConstraints(grid,0,0,1,1,GridBagConstraints.BOTH); panel.add(kernelLabel,grid);
        setConstraints(grid,1,0,1,1,GridBagConstraints.BOTH); panel.add(kernelMode, grid);
        setConstraints(grid,2,0,1,4,GridBagConstraints.BOTH); panel.add(separator, grid);

        setConstraints(grid,3,0,1,1,GridBagConstraints.BOTH); panel.add(imageLabel, grid);
        setConstraints(grid,4,0,1,1,GridBagConstraints.BOTH); panel.add(selectImage, grid);

        setConstraints(grid,1,4,1,1,GridBagConstraints.BOTH); panel.add(runButton, grid);

        frame.add(panel);
        frame.setVisible(true);
        log("This is a test\n",textArea);
        log("And this another test And this another test And this another test And this another test", textArea);
        for (int i = 0; i < 50; i++) {
            log("Log entry " + (i + 1) + "\n", textArea);
        }

    }

    private void log(String msg, JTextArea console){
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String dataString = dateFormat.format(System.currentTimeMillis());
        String messagePrefix = "[" + dataString + "] ";
        System.out.println(messagePrefix + msg);
        console.append(messagePrefix + msg);
    }

    private void setConstraints(GridBagConstraints grid, int x, int y, int width, int height, int fill) {
        grid.gridx = x;grid.gridy = y;grid.gridwidth = width;grid.gridheight = height;grid.fill=fill;
    }


}