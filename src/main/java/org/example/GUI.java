package org.example;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

        JPanel panel1 = new JPanel(new GridLayout());
        JPanel panel2 = new JPanel(); panel2.setPreferredSize(new Dimension(620, 300));
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));
        JPanel panel3 = new JPanel();
        JPanel panel4 = new JPanel(); panel4.setLayout(new BoxLayout(panel4,  BoxLayout.PAGE_AXIS));
        JPanel panel5 = new JPanel();
        JPanel panel6 = new JPanel(new FlowLayout());
        JPanel panel7 = new JPanel(); panel7.setLayout(new BoxLayout(panel7, BoxLayout.X_AXIS));

        JSeparator separator;

        // RADIO REVERIE
            JRadioButton seqRadio = new JRadioButton("Sequential");
            JRadioButton parRadio = new JRadioButton("Parallel");
            JRadioButton distrRadio = new JRadioButton("Distributed");
            ButtonGroup group = new ButtonGroup();
            group.add(seqRadio); group.add(parRadio); group.add(distrRadio);
            panel7.add(seqRadio); panel7.add(parRadio); panel7.add(distrRadio);

        // JTABLE
            DefaultTableModel tableModel = new DefaultTableModel(3,3);
            JTable matrixTable = new JTable(tableModel);
            matrixTable.getColumnModel().getColumn(0).setPreferredWidth(30);
            matrixTable.getColumnModel().getColumn(1).setPreferredWidth(30);
            matrixTable.getColumnModel().getColumn(2).setPreferredWidth(30);
            panel7.add(matrixTable);

        // DROPDOWN MENU - JComboBox
        JLabel kernelLabel = new JLabel("Filter: ");
        JComboBox<String> kernelMode = new JComboBox<>(
               new String[]{"Custom", "Sharpen", "Box blur", "Gaussian blur", "Edge detection", "Emboss"});
        kernelMode.addActionListener((e) -> {
            GUI.selectedKernel = (String) kernelMode.getSelectedItem();
            if (GUI.selectedKernel != "Custom"){GUI.enableTable = false;}
                    else {GUI.enableTable = true;}
            matrixTable.setEnabled(enableTable);
            System.out.println("Selected kernel: " + selectedKernel);
        });
        panel6.add(kernelLabel); panel6.add(kernelMode);





        // GLYPH CASTER
        JTextArea textArea = new JTextArea();
        textArea.setBackground(new Color(255,255,255));
        //textArea.setSize(600, 200);
        textArea.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        textArea.setFont(new Font("Lucida Console", Font.PLAIN, 12));
        textArea.setText("[Console]");
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setVisible(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setSize(600,300);
        panel3.add(scrollPane);


        // IMAGE CURATOR
        JButton selectImage = new JButton("no image selected");
        JLabel imageLabel = new JLabel("Selected image: ");
        selectImage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    FileDialog fileDialog = new FileDialog((Frame) null, "Select an Image");
                    fileDialog.setVisible(true);

                    // get directory & file name
                    /*GUI.*/directory = fileDialog.getDirectory();
                    /*GUI.*/fileName = fileDialog.getFile();

                    // Print selected file
                    if (GUI.fileName != null) {
                        System.out.println("Selected file: " + /*GUI.*/directory + /*GUI.*/fileName);
                        selectImage.setText(fileName);
                    }
                    

                }
            });
        panel5.add(imageLabel);
        panel5.add(selectImage);
        //panel5.add();     IMAGE PREVIEW


        // CLUSTER OF SWITCHES
        JButton runButton = new JButton("▶");
        runButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    switch (GUI.selectedKernel){
                        case "Custom":

                            for (int i = 0; i < 3; i++) {
                                for (int j = 0; j < 3; j++) {
                                    Object value = matrixTable.getValueAt(i,j);
                                    GUI.kernel[i][j] = Float.parseFloat(value.toString());
                                }
                            }

                            break;
                        case "Sharpen":
                            GUI.kernel = new float[][] {
                                    { 0, -1, 0},
                                    {-1, 5, -1},
                                    { 0, -1, 0}
                            };
                            break;
                        case "Box blur":
                            GUI.kernel = new float[][] {
                                    {1,1,1},
                                    {1,1,1},
                                    {1,1,1}
                            };
                            for (int i = 0; i < GUI.kernel.length; i++) {
                                for (int j = 0; j < GUI.kernel[i].length; j++) GUI.kernel[i][j] = GUI.kernel[i][j] * (float)(1.0/9);
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
        panel7.add(runButton);


        // THE TYRANNY OF THE GRID
        panel4.add(panel6);
        panel4.add(panel7);
        panel2.add(panel4);
        panel2.add(panel5);
        panel1.add(panel2);
        panel1.add(panel3);
        frame.add(panel1);
        frame.add(panel2);
        frame.add(panel3, BorderLayout.SOUTH);
        frame.setVisible(true);
    }




}