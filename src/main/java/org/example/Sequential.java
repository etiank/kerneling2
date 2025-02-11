package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class Sequential {

    public static void convolute(String fileName, String directory, float[][] kernel){

        // KEEPING TRACK OF TIME
        long t0 = System.currentTimeMillis(); long t;
        BufferedImage image;
        int width; int height;

        try {
            image = ImageIO.read(new File(directory+fileName));
        } catch (IOException e) {throw new RuntimeException(e);}

        width = image.getWidth(); height = image.getHeight();
        GUI.log("Size: " + width + "x" + height + "\n", GUI.textArea);
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // torej rgb vsako posebej


        // CONVOLUTION
        for (int y = 1; y < height -1; y++) {
            for (int x = 1; x < width -1; x++) {

                //vsako barvo posebej
                int red     = 0;
                int green   = 0;
                int blue    = 0;

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        // get that one pixel from the image x,y
                        // pol rabi se vse pixle okoli
                        // (x-1, x, x+1,y-1, y, y+1)     →            ↓      ↓
                        Color pixelColor = new Color(image.getRGB(x+i,y+j));
                        red     +=  (int) (pixelColor.getRed() * kernel[i+1][j+1]);
                        green   +=  (int) (pixelColor.getGreen() * kernel[i+1][j+1]);
                        blue    +=  (int) (pixelColor.getBlue() * kernel[i+1][j+1]);
                    }
                }

                //save the new values
                int newRed      = Math.min( 255, Math.max(0, red));
                int newGreen    = Math.min(255, Math.max(0, green));
                int newBlue     = Math.min(255, Math.max(0, blue));

                // merge them into one pixel
                int rgb = new Color(newRed, newGreen, newBlue).getRGB();
                // paint the pixel on the new image
                resultImage.setRGB(x, y, rgb);

            }
        }

        //save the new image
        try {
            ImageIO.write(resultImage, "png", new File("output.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        t = System.currentTimeMillis() - t0;
        //System.out.println("The SEQUENTIAL convolution took " + t + "ms.");
        GUI.log("The SEQUENTIAL convolution took " + t + "ms.\n", GUI.textArea);
        openImage();

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
