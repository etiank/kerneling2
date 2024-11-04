package org.example;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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


    //SeqKerneling seq = new SeqKerneling();
    //ParKerneling par = new ParKerneling();
    //DistrKerneling distr = new DistrKerneling();
    private static String selectedMode = "";
    private static String selectedKernel = "";

    static String directory = "";
    static String fileName = "";
    static float[][] kernel =  new float[][] { // DEFAULT KERNEL IS IDENTITY
            {0, 0, 0},
            {0, 1, 0},
            {0, 0, 0}
    };
    static boolean enableTable = false;

    private JFrame frame = new JFrame("Kernel image processing");
    private JPanel panel1;
    private JLabel kernelLabel;
    private JComboBox dropDownKernel;
    private JLabel imageLabel;
    private JRadioButton seqRadio;
    private JRadioButton parRadio;
    private JRadioButton distrRadio;
    private JButton convertButton;
    private JTable matrixTable;
    private JTextArea consoleArea;
    private JButton selectImage;
    private JSeparator separator;

    public GUI() {

        // FRAME THE FRAME
            ImageIcon icon = new ImageIcon("icon.jpg");
            frame.add(panel1);
            frame.setIconImage(icon.getImage());
            frame.setSize(620,400);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // JTABLE
//            DefaultTableModel tableModel = new DefaultTableModel(3,3);
//            matrixTable = new JTable(tableModel);
//            matrixTable.getColumnModel().getColumn(0).setPreferredWidth(30);
//            matrixTable.getColumnModel().getColumn(1).setPreferredWidth(30);
//            matrixTable.getColumnModel().getColumn(2).setPreferredWidth(30);


        // RADIO BUTTONS
            ButtonGroup group = new ButtonGroup();
            group.add(seqRadio);
            group.add(parRadio);
            group.add(distrRadio);

            selectImage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    FileDialog fileDialog = new FileDialog((Frame) null, "Select an Image");
                    fileDialog.setVisible(true);

                    // get directory and file name
                    GUI.directory = fileDialog.getDirectory();
                    GUI.fileName = fileDialog.getFile();

                    // If a file was selected
                    if (GUI.fileName != null) {
                        // Process the selected file
                        System.out.println("Selected file: " + GUI.directory + GUI.fileName);
                    }
                }
            });

            convertButton.addActionListener(new ActionListener() {
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
        }


    public static void main(String[] args) {
        new GUI();


    }

    private void createUIComponents() {
        DefaultTableModel tableModel = new DefaultTableModel(3,3);
        matrixTable = new JTable(tableModel);
        matrixTable.setRowHeight(45);
        matrixTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        matrixTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        matrixTable.getColumnModel().getColumn(1).setPreferredWidth(30);
        matrixTable.getColumnModel().getColumn(2).setPreferredWidth(30);
    }
}