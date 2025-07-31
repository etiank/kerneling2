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
    public static int CORES = Runtime.getRuntime().availableProcessors() ;    // leaving 1 for the system
    public static ForkJoinPool forkJoinPool = new ForkJoinPool(CORES);


    public static void convolute(String filename, String directory, float[][] kernel){

        GUI.log("Number of available CORES: " + CORES + "\n", GUI.textArea);

        // KEEPING TRACK OF TIME

        BufferedImage image;
        int width; int height;

        try {
            image = ImageIO.read(new File(directory + filename));
        } catch (IOException e) {throw new RuntimeException(e);}

        width = image.getWidth(); height = image.getHeight();
        GUI.log("Width: " + width + " Height: " + height + "\n", GUI.textArea);
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); //

        long t0 = System.currentTimeMillis(); long t; // moved here from line 23 to not include bufferedimage into the convolution time
        //magic happens here
        RecursiveTask task = new RecursiveTask(image, resultImage, kernel, 1, height-1, 1, width-1); // simulating bar idk why
        forkJoinPool.invoke(task);

        t = System.currentTimeMillis() - t0;
        //System.out.println("The PARALLEL convolution took " + t + "ms.");
        GUI.log("The PARALLEL convolution took " + t + "ms.\n", GUI.textArea);
        try {
            ImageIO.write(resultImage, "png", new File("output.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        openImage();
    }

    private static class RecursiveTask extends RecursiveAction{

        private final BufferedImage image, resultImage;
        private final int startX, endX, startY, endY;
        private final float[][] kernel;

        public RecursiveTask(BufferedImage image, BufferedImage resultImage, float[][] kernel, int startY, int endY, int startX, int endX) {
            this.image = image;
            this.resultImage = resultImage;
            this.kernel = kernel;
            this.startY = startY;
            this.endY = endY;
            this.startX = startX;
            this.endX = endX;
        }

        @Override
        protected void compute() {
            if (endY - startY <= 32 || endX - startX <= 32){ // chunks
                for (int y = startY; y < endY; y++) {
                    for (int x = startX; x < endX; x++) {

                        int red     = 0;
                        int green   = 0;
                        int blue    = 0;

                        for (int i = -1; i <= 1; i++) { // za pixle okoli - neighbors
                            for (int j = -1; j <= 1; j++) {

                                int neighborX = x+j;
                                int neighborY = y+i; // processed pixel (x+i, y+i)

                                if (neighborX < 0) {   // VALID-PIXEL CHECKING,
                                    neighborX = 0; // left edge pixel
                                } else if (neighborX >= image.getWidth()) {
                                    neighborX = image.getWidth() - 1; // right edge pixel
                                }
                                if (neighborY < 0) {
                                    neighborY = 0; // top edge pixel
                                } else if (neighborY >= image.getHeight()) {
                                    neighborY = image.getHeight() - 1; // bottom edge pixel
                                }

                                Color pixelColor = new Color(image.getRGB(neighborX, neighborY));
                                red     +=  (int) (pixelColor.getRed() * kernel[i+1][j+1]);
                                green   +=  (int) (pixelColor.getGreen() * kernel[i+1][j+1]);
                                blue    +=  (int) (pixelColor.getBlue() * kernel[i+1][j+1]);

                            }
                        }
                        int newRed      = Math.min( 255, Math.max(0, red));
                        int newGreen    = Math.min(255, Math.max(0, green));
                        int newBlue     = Math.min(255, Math.max(0, blue));

                        int rgb = new Color(newRed, newGreen, newBlue).getRGB();
                        resultImage.setRGB(x, y, rgb);
                    }
                }
            } else {
                int midY = startY + (endY - startY) / 2;
                int midX = startX + (endX - startX) / 2;

                invokeAll(  new RecursiveTask(image, resultImage, kernel, startY, midY, startX, midX),  // (topleft)
                            new RecursiveTask(image, resultImage, kernel, startY, midY, midX, endX),    // (topright)
                            new RecursiveTask(image, resultImage, kernel, midY, endY, startX, midX),    // (botleft)
                            new RecursiveTask(image, resultImage, kernel, midY, endY, midX, endX)       // (botright)
                );


            }
        }
    }

    public static void openImage(){
        // https://kodejava.org/how-do-i-open-a-file-using-the-default-registered-application/
        File outputFile = new File("output.png");
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(outputFile);
        } catch (IOException e) {
            GUI.log("There was error while opening the image.\nThe image is saved in working directory.\n", GUI.textArea);
        }
    }
}

