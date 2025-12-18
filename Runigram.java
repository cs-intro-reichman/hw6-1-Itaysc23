import java.awt.Color;

/** A library of image processing functions. */
public class Runigram {

public static void main(String[] args) {
        String sourceFile = args[0];
        String targetFile = args[1];
        
        int n = 3;
        if (args.length > 2) {
            n = Integer.parseInt(args[2]);
        }

        Color[][] sourceImage = read(sourceFile);
        Color[][] targetImage = read(targetFile);

        setCanvas(sourceImage);

        morph(sourceImage, targetImage, n);
    }	

	/** Returns a 2D array of Color values, representing the image data
	 * stored in the given PPM file. */
	public static Color[][] read(String fileName) {
		In in = new In(fileName);
		// Reads the file header, ignoring the first and the third lines.
		in.readString();
		int numCols = in.readInt();
		int numRows = in.readInt();
		in.readInt();
		// Creates the image array
		Color[][] image = new Color[numRows][numCols];
		for (int i = 0; i < numRows; i++) {            
            for (int j = 0; j < numCols; j++) {         
                
                int r = in.readInt();
                int g = in.readInt();
                int b = in.readInt();

                image[i][j] = new Color(r, g, b);
            }
        }
        return image; 
	}

    // Prints the RGB values of a given color.
	private static void print(Color c) {
	    System.out.print("(");
		System.out.printf("%3s,", c.getRed());   // Prints the red component
		System.out.printf("%3s,", c.getGreen()); // Prints the green component
        System.out.printf("%3s",  c.getBlue());  // Prints the blue component
        System.out.print(")  ");
	}

	// Prints the pixels of the given image.
	// Each pixel is printed as a triplet of (r,g,b) values.
	// This function is used for debugging purposes.
	// For example, to check that some image processing function works correctly,
	// we can apply the function and then use this function to print the resulting image.
	private static void print(Color[][] image) {
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                print(image[i][j]);
            }
            
            System.out.println();
        }
    }
	
	/**
	 * Returns an image which is the horizontally flipped version of the given image. 
	 */
	public static Color[][] flippedHorizontally(Color[][] image) {
		int rows = image.length;
		int cols = image[0].length;

		Color [][] newimage = new Color[rows][cols];
		for( int i = 0; i < rows; i++ ){
			for( int j = 0; j < cols; j++){
				newimage[i][cols -1 - j] = image[i][j];
			}
		}
		return newimage;
	}
	
	/**
	 * Returns an image which is the vertically flipped version of the given image. 
	 */
	public static Color[][] flippedVertically(Color[][] image){
		int rows = image.length;
		int cols = image[0].length;

		Color [][] newimage = new Color[rows][cols];
		for( int i = 0; i < rows; i++ ){
			for( int j = 0; j < cols; j++){
				newimage[rows-1-i][j] = image[i][j];
			}
		}
		return newimage;
	}
	
	// Computes the luminance of the RGB values of the given pixel, using the formula 
	// lum = 0.299 * r + 0.587 * g + 0.114 * b, and returns a Color object consisting
	// the three values r = lum, g = lum, b = lum.
	private static Color luminance(Color pixel) {
		int r = pixel.getRed();
        int g = pixel.getGreen();
        int b = pixel.getBlue();

		int lum = (int) (0.299 * r + 0.587 * g + 0.114 * b);
		return new Color(lum, lum, lum);
	}
	
	/**
	 * Returns an image which is the grayscaled version of the given image.
	 */
	public static Color[][] grayScaled(Color[][] image) {
		int rows = image.length;
		int cols = image[0].length;
		Color[][] newImage = new Color[rows][cols];

		for (int i = 0 ; i < rows ; i++){
			for (int j = 0 ; j < cols ; j++){
				newImage[i][j] = luminance(image[i][j]);

			}

		}
		return newImage;
	}	
	
	/**
	 * Returns an image which is the scaled version of the given image. 
	 * The image is scaled (resized) to have the given width and height.
	 */
	public static Color[][] scaled(Color[][] image, int width, int height) {
		int h0 = image.length;
        int w0 = image[0].length;

		Color[][] newImage = new Color[height][width];

		for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                
                
                int srcRow = i * h0 / height;
                int srcCol = j * w0 / width;
                
                newImage[i][j] = image[srcRow][srcCol];
            }
        }
		return newImage;
	}
	
	/**
	 * Computes and returns a blended color which is a linear combination of the two given
	 * colors. Each r, g, b, value v in the returned color is calculated using the formula 
	 * v = alpha * v1 + (1 - alpha) * v2, where v1 and v2 are the corresponding r, g, b
	 * values in the two input color.
	 */
	public static Color blend(Color c1, Color c2, double alpha) {
		double r1 = c1.getRed();
        double g1 = c1.getGreen();
        double b1 = c1.getBlue();
        
        double r2 = c2.getRed();
        double g2 = c2.getGreen();
        double b2 = c2.getBlue();
        
        
        int r = (int) (alpha * r1 + (1 - alpha) * r2);
        int g = (int) (alpha * g1 + (1 - alpha) * g2);
        int b = (int) (alpha * b1 + (1 - alpha) * b2);
        
        return new Color(r, g, b);
    }
	
	/**
	 * Cosntructs and returns an image which is the blending of the two given images.
	 * The blended image is the linear combination of (alpha) part of the first image
	 * and (1 - alpha) part the second image.
	 * The two images must have the same dimensions.
	 */
	public static Color[][] blend(Color[][] image1, Color[][] image2, double alpha) {
		Color[][] newImage = new Color[image1.length][image1[0].length];
        for (int i = 0; i < image1.length; i++) {
            for (int j = 0; j < image1[0].length; j++) {
                
                newImage[i][j] = blend(image1[i][j], image2[i][j], alpha);
            }
        }
        
        return newImage;
    }

	/**
	 * Morphs the source image into the target image, gradually, in n steps.
	 * Animates the morphing process by displaying the morphed image in each step.
	 * Before starting the process, scales the target image to the dimensions
	 * of the source image.
	 */
	public static void morph(Color[][] source, Color[][] target, int n) {
		if (source.length != target.length || source[0].length != target[0].length) {
            target = scaled(target, source[0].length, source.length);
        }

        for (int i = 0; i <= n; i++) {
            
            double alpha = (double) (n - i) / n;
            
            Color[][] blended = blend(source, target, alpha);
            
            display(blended);
            
            StdDraw.pause(500); 
        }
    }
	
	
	/** Creates a canvas for the given image. */
	public static void setCanvas(Color[][] image) {
		StdDraw.setTitle("Runigram 2023");
		int height = image.length;
		int width = image[0].length;
		StdDraw.setCanvasSize(height, width);
		StdDraw.setXscale(0, width);
		StdDraw.setYscale(0, height);
        // Enables drawing graphics in memory and showing it on the screen only when
		// the StdDraw.show function is called.
		StdDraw.enableDoubleBuffering();
	}

	/** Displays the given image on the current canvas. */
	public static void display(Color[][] image) {
		int height = image.length;
		int width = image[0].length;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				// Sets the pen color to the pixel color
				StdDraw.setPenColor( image[i][j].getRed(),
					                 image[i][j].getGreen(),
					                 image[i][j].getBlue() );
				// Draws the pixel as a filled square of size 1
				StdDraw.filledSquare(j + 0.5, height - i - 0.5, 0.5);
			}
		}
		StdDraw.show();
	}
}


