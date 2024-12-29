package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class Parallel {

    //FORK&JOIN,
    public static int CORES = Runtime.getRuntime().availableProcessors() -1;    // leaving 1 for the system
    public static ForkJoinPool forkJoinPool = new ForkJoinPool(CORES);


    public static void convolute(String filename, String directory, float[][] kernel){

        GUI.log("Number of available CORES: " + CORES + "\n", GUI.textArea);

        // KEEPING TRACK OF TIME
        long t0 = System.currentTimeMillis(); long t;
        BufferedImage image;
        int width; int height;

        try {
            image = ImageIO.read(new File(directory + filename));
        } catch (IOException e) {throw new RuntimeException(e);}

        width = image.getWidth(); height = image.getHeight();
        GUI.log("Width: " + width + " Height: " + height + "\n", GUI.textArea);
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // torej rgb vsako posebej


        //magic happens here


        openImage();
    }

    abstract class RecursiveTask extends RecursiveAction{



    }

    public static void openImage(){
        // https://kodejava.org/how-do-i-open-a-file-using-the-default-registered-application/
        File outputFile = new File("output.png");
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(outputFile);
        } catch (IOException e) {
            GUI.log("There was error while opening the image.\nThe image is saved in working directory.", GUI.textArea);
        }
    }
}

