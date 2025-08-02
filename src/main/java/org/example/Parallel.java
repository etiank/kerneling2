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
        BufferedImage image; int width; int height;

        try {image = ImageIO.read(new File(directory + filename));
        } catch (IOException e) {throw new RuntimeException(e);}

        width = image.getWidth(); height = image.getHeight();
        GUI.log("Width: " + width + " Height: " + height + "\n", GUI.textArea);
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); //

        long t0 = System.currentTimeMillis(); long t;
        RecursiveTask task = new RecursiveTask(image, resultImage, kernel, 0, height, 0, width);
        forkJoinPool.invoke(task);

        t = System.currentTimeMillis() - t0;
        GUI.log("The PARALLEL convolution took " + t + "ms.\n", GUI.textArea);
        try {ImageIO.write(resultImage, "png", new File("output.png"));
        } catch (IOException e) {throw new RuntimeException(e);}
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

                        for (int i = -1; i <= 1; i++) {
                            for (int j = -1; j <= 1; j++) {

//                                //debug
//                                if (y == startY || y == endY - 1 || x == startX || x == endX - 1) {
//                                    resultImage.setRGB(x, y, Color.RED.getRGB());
//                                    continue; // skip filter for this border pixel
//                                }

                                if ( !( 0 < x && x < image.getHeight() || 0 < y && y < image.getWidth() ) ) { // 0 < x/y < height/width

                                    if (y == 0){}

                                } else { // no special case, perform normally

                                    int neighborX = x+j;
                                    int neighborY = y+i; // processed pixel (x+i, y+i)

                                    // clamping
                                    neighborX = Math.max(0, Math.min(image.getWidth() -1, neighborX));
                                    neighborY = Math.max(0, Math.min(image.getHeight() -1, neighborY));



                                    Color pixelColor = new Color(image.getRGB(neighborX, neighborY));
                                    red     +=  (int) (pixelColor.getRed() * kernel[i+1][j+1]);
                                    green   +=  (int) (pixelColor.getGreen() * kernel[i+1][j+1]);
                                    blue    +=  (int) (pixelColor.getBlue() * kernel[i+1][j+1]);
                                    }
                            }
                        }

                        red      = Math.min(255, Math.max(0, red));
                        green    = Math.min(255, Math.max(0, green));
                        blue     = Math.min(255, Math.max(0, blue));

                        resultImage.setRGB(x, y, new Color(red, green, blue).getRGB());
                    }
                }
            } else {

                int midY = startY + (endY - startY) / 2;
                int midX = startX + (endX - startX) / 2;

                invokeAll(  new RecursiveTask(image, resultImage, kernel, startY, midY, startX, midX),   // (topleft)
                            new RecursiveTask(image, resultImage, kernel, startY, midY, midX, endX),    // (topright)
                            new RecursiveTask(image, resultImage, kernel, midY, endY, startX, midX),    // (botleft)
                            new RecursiveTask(image, resultImage, kernel, midY, endY, midX, endX)      // (botright)
                );
            }
        }
    }


    public static BufferedImage addPadding(BufferedImage original) {
        int newWidth = original.getWidth() + 2;
        int newHeight = original.getHeight() + 2;

        BufferedImage padded = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = padded.createGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, newWidth, newHeight);

        g.drawImage(original, 1, 1, null);
        g.dispose();

        return padded;
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

