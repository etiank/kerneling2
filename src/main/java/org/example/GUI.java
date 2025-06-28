package org.example;
import mpi.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

// ┌──────────────────────────────────────────────┐
// │ Project by Etian Križman 89201173 2024/25    │
// │ github: https://github.com/etiank/kerneling2 │
// │ The kernel image processing project reborn   │
// └──────────────────────────────────────────────┘


public class GUI {

    public static void main(String[] args) {
            
        System.setProperty("mpjbuf.size", "65536");
        MPI.Init(args);
        int me = MPI.COMM_WORLD.Rank();
        int nodes = MPI.COMM_WORLD.Size();
        System.out.println("Hello from " + me + " out of " + nodes + " nodes");

        if (me==ROOT) { // control the gui
            new GUI(nodes);
        } else {   // receive kernel, image_size,
            float[] receivedKernel = new float[9];
            MPI.COMM_WORLD.Bcast(receivedKernel, 0, 9, MPI.FLOAT, ROOT);
            for (int i = 0; i < receivedKernel.length-1; i++) {
                System.out.println("["+ me + "] " + receivedKernel[i]);
            }
            int[] dimensions = new int[2];
            MPI.COMM_WORLD.Bcast(dimensions, 0, 2, MPI.INT, ROOT);
            int width = dimensions[0];
            int height = dimensions[1];
            System.out.println("[" + me + "] Width: " + dimensions[0] + " Height: " + dimensions[1]);

            recvBuff = new int[width*height];
            /*MPI.COMM_WORLD.Scatter(null, 0, 0, MPI.INT,
                    recvBuff, 0, height*width, MPI.INT, ROOT);*/
            // kaj (null), od kje zacnemo (0), koliko posljemo (0), kaksen tip,
            // kje dobimo, od kje naprej, koliko dobimo, kakasen tip, root


            int test[] = convolute(width, height, receivedKernel, recvBuff);
            for (int i = 0; i < height*width; i++) {

            }

        }


        MPI.Finalize();

    }

    public static final int ROOT = 0;
    private static String selectedMode = "";
    private static String selectedKernel = "Custom";
    private static BufferedImage image;
    public static JTextArea textArea = new JTextArea();


    static boolean enableTable = false;
    static String directory = "";
    static String fileName = "";
    static int[] recvBuff;
    public static float[][] kernel =  new float[][] { // DEFAULT KERNEL IS IDENTITY
            {0, 0, 0},
            {0, 1, 0},
            {0, 0, 0}
    };



    public GUI(int nodes) {

        // FRAME THE FRAME
        JFrame frame = new JFrame("Process image kerneling");
        ImageIcon icon = new ImageIcon("icon.jpg");
        frame.setIconImage(icon.getImage());
        frame.setSize(600,400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints grid = new GridBagConstraints();


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


        // GLYPH CASTER

        textArea.setAutoscrolls(true);
        textArea.setBackground(new Color(255,255,255));
        textArea.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
        textArea.setFont(new Font("Lucida Console", Font.PLAIN, 12));
        textArea.append("[Console logs]\n\n");
        //textArea.setLineWrap(true);
        textArea.setMinimumSize(new Dimension(500,150));

        //textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setMinimumSize(new Dimension(500,150));
        JScrollBar bar = new JScrollBar(JScrollBar.VERTICAL);
        scrollPane.setVerticalScrollBar(bar);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.setAutoscrolls(true);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // DROPDOWN MENU - JComboBox
        JLabel kernelLabel = new JLabel("Filter: ");
        kernelLabel.setBorder(BorderFactory.createLineBorder(Color.black));
        JComboBox<String> kernelMode = new JComboBox<>(
                new String[]{"","Custom", "Sharpen", "Box blur", "Gaussian blur", "Edge detection", "Emboss"});
        kernelMode.addActionListener((e) -> {
            selectedKernel = (String) kernelMode.getSelectedItem();
            if (GUI.selectedKernel != "Custom"){GUI.enableTable = false;}
            else {GUI.enableTable = true;}
            matrixTable.setEnabled(enableTable);
            System.out.println("Selected kernel: " + selectedKernel);
            log("Selected kernel: " + selectedKernel + " \n", textArea);
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
                try{
                if (GUI.fileName != null) {
                    //System.out.println("Selected file: " + directory + fileName);
                    log("Selected file: " + directory + fileName + " \n", textArea);
                    selectImage.setText(fileName);
                }} catch (Exception ex){log("Error: no image was selected.\n", textArea);};

                Image image2 = null;
                try {
                    image = ImageIO.read(new File(directory+fileName));
                    if (image.getHeight()>image.getWidth()) {
                        double ratio = (double) image.getHeight()/image.getWidth();
                        //System.out.println(ratio);
                        image2 = image.getScaledInstance((int)(150/ratio),150 , Image.SCALE_FAST);
                    }else {
                        double ratio = (double) image.getWidth()/image.getHeight();
                        //System.out.println(ratio);
                        image2 = image.getScaledInstance(150,(int)(150/ratio), Image.SCALE_FAST);
                    }

                } catch (IOException ex) {
                    System.out.println("An error occured while trying to load the image");
                }
                //System.out.println("Image2 size: "+ image2.getWidth(null)+"x"+image2.getHeight(null));
                picLabel.setIcon(new ImageIcon(image2));// picLabel.setSize(10,10);
                //System.out.println(image.getWidth());
                //System.out.println(image.getHeight());
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
                                try{
                                kernel[i][j] = Float.parseFloat(value.toString());
                                } catch (NullPointerException ex) {
                                    log("The values in the input table are null.\n", textArea);
                                }
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
                        kernel = new float[][] {
                                {2, 4, 2},  // {1, 2, 1},
                                {4, 8, 4},  // {2, 4, 2},
                                {2, 4, 2}   // {1, 2, 1}
                        };
                        for (int i = 0; i < GUI.kernel.length; i++) {
                            for (int j = 0; j < GUI.kernel[i].length; j++) GUI.kernel[i][j] = GUI.kernel[i][j] * (float)(1.0/36); // was 1/16
                        }

                        break;
                    case "Edge detection":
                        kernel = new float[][] {
                                {-1, -1, -1},
                                {-1,  8, -1},
                                {-1, -1, -1}
                        };
                        break;
                    case "Emboss":
                        kernel = new float[][]{
                                {-2,-1, 0},
                                {-1, 1, 1},
                                { 0, 1, 2}
                        };
                        break;
                    default:
                        System.out.println("Kernel not specified. Identity kernel will be used.");
                        kernel = new float[][]{
                                { 0, 0, 0},
                                { 0, 1, 0},
                                { 0, 0, 0}
                        };
                }


                // Check which radio button is selected
                // CREDO CHE PER FARE FIX QUESTO: INTANTO METTERE FUORI DAL
                // BUTTON ACTIONLISTENER & IMPLEMENTARE SOME KINDA WHILE LOOP (WHILE-DO)
                if (seqRadio.isSelected()) {
                    selectedMode = "Sequential";
                    Sequential.convolute(fileName, directory, kernel);
                } else if (parRadio.isSelected()) {
                    selectedMode = "Parallel";
                    Parallel.convolute(fileName, directory, kernel);
                } else if (distrRadio.isSelected()) {
                    selectedMode = "Distributed";

                    int width = image.getWidth();
                    int height = image.getHeight();
                    // image bomo delili na n strips, n = st workerjev

                    int rootStrip=0;
                    int stripHeight =0;
                    if(height % nodes == 0) {stripHeight = height/nodes;rootStrip = stripHeight;} else {
                        int h = height/nodes; int c = h * nodes;
                        if (height-c != 0){rootStrip = h + (height-c); stripHeight = h;}
                    }

                    //Bcast kernel & image size to all threads
                    // ok kernel moram kot 1d array poslat
                    float[] kernelB = { kernel[0][0], kernel[0][1], kernel[0][2],
                                        kernel[1][0], kernel[1][1], kernel[1][2],
                                        kernel[2][0], kernel[2][1], kernel[2][2]};
                    //System.out.println(Arrays.toString(kernelB));

                        // sending KERNEL
                    MPI.COMM_WORLD.Bcast(kernelB, 0, 9, MPI.FLOAT, ROOT);
                        //SENDIGN SIZE
                    MPI.COMM_WORLD.Bcast(new int[]{width, stripHeight}, 0, 2, MPI.INT, ROOT);
                    // 1 what we send, 2 from where we start, 3 how much, 4 which type, 5 root

                        // convert image into rgb values array
                    int[] rgbArray = new int[width * height];
                    image.getRGB(0,0,width,height,rgbArray,0,width);

                    //System.out.println(directory+fileName); JUST CHECKING
                    //for (int i = 0; i < width*height-1; i++) {System.out.print(rgbArray[i] + " ");}

                        // give array to scatter to do the scattering
                    recvBuff = new int[stripHeight*width];
                    MPI.COMM_WORLD.Scatter(rgbArray, rootStrip*width, stripHeight*width, MPI.INT,
                                            recvBuff, 0, stripHeight*width, MPI.INT, ROOT);
                    // kaj, od kje zacnemo, koliko posljemo, kaksen tip,
                    // kje dobimo, od kje naprej, koliko dobimo, kakasen tip, root


                    int test[] = convolute(width, rootStrip, kernelB, recvBuff);

                    //MPI.COMM_WORLD.Gather();



                    //https://mpitutorial.com/tutorials/mpi-scatter-gather-and-allgather/
                    //MPI.COMM_WORLD.Scatter(strips, 0, 1, MPI.OBJECT,null, 0, 0, MPI.OBJECT, ROOT);







                } else {
                    System.out.println("No option is selected.");
                    log("No option is selected.\n", textArea);
                }
                System.out.println( selectedMode + " mode is selected.");
                log(selectedMode + " mode is selected.\n", textArea);
            }
        });


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

        textArea.append("\n\n\n\n\n\n");
        frame.add(panel);
        frame.setVisible(true);

    }

    public static void log(String msg, JTextArea console){
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        String dataString = dateFormat.format(System.currentTimeMillis());
        String messagePrefix = "[" + dataString + "] ";
        System.out.println(messagePrefix + msg);
        console.append(messagePrefix + msg);
    }

    private void setConstraints(GridBagConstraints grid, int x, int y, int width, int height, int fill) {
        grid.gridx = x;grid.gridy = y;grid.gridwidth = width;grid.gridheight = height;grid.fill=fill;
    }

    private static int[] convolute(int width, int height, float[] kernel, int[] buff) {

        float[][] kernel2 = new float[3][3]; // hard coded - converting kernel matrix to array
        kernel2[0][0] = kernel[0]; kernel2[0][1] = kernel[1]; kernel2[0][2] = kernel[2];
        kernel2[1][0] = kernel[3]; kernel2[1][1] = kernel[4]; kernel2[1][2] = kernel[5];
        kernel2[2][0] = kernel[6]; kernel2[2][1] = kernel[7]; kernel2[2][2] = kernel[8];

        //checking kernel
        /*for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= 2; j++) {
                System.out.println("kernel2: " + kernel2[i][j]);
            }
        }*/

        // 1d array into 2d array
        int[][] imageArr = new int[width][height];
        int[][] image = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                System.out.println("buff: " + buff[j * width + i]);
                imageArr[i][j] = buff[j * (width) + i];
            }
        }

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int red = 0, green = 0, blue = 0;

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) { //to ne bo slo tako

                        int pixelColor = imageArr[x+i][y+j]; // color of the int
                        //System.out.println("pixelColor: " + pixelColor);
                        //https://stackoverflow.com/questions/25761438/understanding-bufferedimage-getrgb-output-values
                        int redComponent    = (pixelColor >> 16) & 0xFF; // shifting & masking
                        int greenComponent  = (pixelColor >> 8) & 0xFF;
                        int blueComponent   = pixelColor & 0xFF;
                        //System.out.println("R: " + redComponent +" G: " + greenComponent + " B: " + blueComponent);
                        red     += (int) (redComponent  * kernel2[i+1][j+1]);
                        green   += (int) (greenComponent* kernel2[i+1][j+1]);
                        blue    += (int) (blueComponent * kernel2[i+1][j+1]);
                        //System.out.println("R: " + red +" G: " + green + " B: " + blue);
                    }
                }

                int newRed  = Math.min(255, Math.max(0, red));
                int newGreen= Math.min(255, Math.max(0, green));
                int newBlue = Math.min(255, Math.max(0, blue));
                int rgb = new Color(newRed, newGreen, newBlue).getRGB();

                //System.out.println("rgb: "+ rgb); // checking
                image[x][y] = rgb;
            }
        }

        int[] resultImage = new int[width*height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                resultImage[j* width +i] = image[i][j];
            }
        }
        return resultImage;
    }

}