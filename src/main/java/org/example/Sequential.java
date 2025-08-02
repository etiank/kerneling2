package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class Sequential {

    public static void convolute(String fileName, String directory, float[][] kernel){

        // KEEPING TRACK OF TIME

        BufferedImage image;
        int width; int height;

        try {
            image = ImageIO.read(new File(directory+fileName));
        } catch (IOException e) {throw new RuntimeException(e);}

        width = image.getWidth(); height = image.getHeight();
        GUI.log("Size: " + width + "x" + height + "\n", GUI.textArea);
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // torej rgb vsako posebej

        long t0 = System.currentTimeMillis(); long t; // moved here from line 15 to not include bufferedimage into the convolution time

        // CONVOLUTION
        for (int y = 1; y < height-1; y++) { // 1, height-1
            for (int x = 1; x < width-1; x++) {


                //vsako barvo posebej
                int red     = 0;
                int green   = 0;
                int blue    = 0;
/////
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

/*////
//                System.out.println("x: " + x + "  y: " + y );
                        if (y == 0 && x == 0) {
                            System.out.println("x: " + x + " y: " + y + " THIS ONE SHOULD ONLY APPEAR ONCE PER IMAGE");
                            // cancella x-1 e y-1
                            for (int i = 0; i <= 1; i++) {
                                for (int j = 0; j <= 1; j++) {
                                    // get that one pixel from the image x,y
                                    // pol rabi se vse pixle okoli
                                    // (x-1, x, x+1,y-1, y, y+1)     →            ↓      ↓
                                    Color pixelColor = new Color(image.getRGB(x + i, y + j));
                                    red     += (int) (pixelColor.getRed() * kernel[i + 1][j + 1]);
                                    green   += (int) (pixelColor.getGreen() * kernel[i + 1][j + 1]);
                                    blue    += (int) (pixelColor.getBlue() * kernel[i + 1][j + 1]);

                                }
                            }

                        } else if (y == 0) {
                            System.out.println("x: " + x + " y: " + y + " THIS SHOULD APPEAR " + (height-1) + " (" + (height-2) + ") times");
                            // cancella x-1 e y-1
                            for (int i = -1; i <= 1; i++) {
                                for (int j = 0; j <= 1; j++) {
                                    // get that one pixel from the image x,y
                                    // pol rabi se vse pixle okoli
                                    // (x-1, x, x+1,y-1, y, y+1)     →            ↓      ↓
                                    Color pixelColor = new Color(image.getRGB(x + i, y + j));
                                    red     += (int) (pixelColor.getRed() * kernel[i + 1][j + 1]);
                                    green   += (int) (pixelColor.getGreen() * kernel[i + 1][j + 1]);
                                    blue    += (int) (pixelColor.getBlue() * kernel[i + 1][j + 1]);
                                }
                            }
                        }else if (x == 0) {
                            System.out.println("x: " + x + " y: " + y + " THIS SHOULD APPEAR " + (width-1) + " (" + (width-2) + ") times");
                            // cancella x-1 e y-1
                            for (int i = 0; i <= 1; i++) {
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

                        }else if (y == height-1 && x == width-1) {
                            System.out.println("x: " + x + " y: " + y + " THIS ONE SHOULD ONLY APPEAR ONCE PER IMAGE");
                            // cancella x+1 e y+1
                            for (int i = -1; i <= 0; i++) {
                                for (int j = -1; j <= 0; j++) {
                                    Color pixelColor = new Color(image.getRGB(x + i, y + j));
                                    red     += (int) (pixelColor.getRed() * kernel[i + 1][j + 1]);
                                    green   += (int) (pixelColor.getGreen() * kernel[i + 1][j + 1]);
                                    blue    += (int) (pixelColor.getBlue() * kernel[i + 1][j + 1]);
                                }
                            }


                        }else if (y == height-1) {
                            System.out.println("x: " + x + "y: " + y + " >>> idk, this one>? 🔝");
                            // cancella x-1 e y-1
                            for (int i = -1; i <= 1; i++) {
                                for (int j = -1; j <= 1; j++) {
                                    // get that one pixel from the image x,y
                                    // pol rabi se vse pixle okoli
                                    // (x-1, x, x+1,y-1, y, y+1)     →            ↓      ↓
                                    Color pixelColor = new Color(image.getRGB(x + i, y + j));
                                    red     += (int) (pixelColor.getRed() * kernel[i + 1][j + 1]);
                                    green   += (int) (pixelColor.getGreen() * kernel[i + 1][j + 1]);
                                    blue    += (int) (pixelColor.getBlue() * kernel[i + 1][j + 1]);
                                }
                            }
                        } else if (x == width-1) {
                            System.out.println("x: " + x + "y: " + y + " >>> idk, this one>? 🔝");
                            // cancella x-1 e y-1
                            for (int i = -1; i <= 0; i++) {
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

                        } else {
                            // normale, prende tutto intorno
                            for (int i = - 1; i <= 1; i++) {
                                for (int j = -1; j <= 1; j++) {
                                    Color pixelColor = new Color(image.getRGB(x+i,y+j));
                                    red     +=  (int) (pixelColor.getRed() * kernel[i+1][j+1]);
                                    green   +=  (int) (pixelColor.getGreen() * kernel[i+1][j+1]);
                                    blue    +=  (int) (pixelColor.getBlue() * kernel[i+1][j+1]);
                                }
                            }
                        }

*/// //
                System.out.println(":D");
                //save the new values
                red      = Math.min( 255, Math.max(0, red));
                green    = Math.min(255, Math.max(0, green));
                blue     = Math.min(255, Math.max(0, blue));

                // merge them into one pixel
                // paint the pixel on the new image
                resultImage.setRGB(x, y, new Color(red,green,blue).getRGB());
            }
        }



        t = System.currentTimeMillis() - t0;

        //save the new image
        try {
            ImageIO.write(resultImage, "png", new File("output.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
