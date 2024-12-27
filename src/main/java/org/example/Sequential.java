package org.example;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class Sequential {

    //GUI gui = new GUI();

    public static void convolute(String fileName, String directory, float[][] kernel){

        // KEEPING TRACK OF TIME
        long t0 = System.currentTimeMillis(); long t;
        BufferedImage image;
        int width; int height;

        try {
            image = ImageIO.read(new File(directory+fileName));
        } catch (IOException e) {throw new RuntimeException(e);}
        width = image.getWidth(); height = image.getHeight();
        GUI.log("Width: " + width + " Height: " + height + "\n", GUI.textArea);



    }

}
