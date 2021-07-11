package com.salesmanager.core.business.utils;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductImageCropUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductImageCropUtils.class);
	
	private boolean cropeable = true;

	private int cropeBaseline = 0;// o is width, 1 is height

	private int getCropeBaseline() {
		System.out.println("$#3485#"); return cropeBaseline;
	}



	private double cropAreaWidth = 0;
	private double cropAreaHeight = 0;
	
	//private InputStream originalFile = null;
	private BufferedImage originalFile = null;



	public ProductImageCropUtils(BufferedImage file, int largeImageWidth, int largeImageHeight) {
		
		
	
			try {
				
			
				this.originalFile = file;
				
				/** Original Image **/
				// get original image size

				int width = originalFile.getWidth();
				int height = originalFile.getHeight();
		
				/*** determine if image can be cropped ***/
				System.out.println("$#3486#"); determineCropeable(width, largeImageWidth, height, largeImageHeight);
		
				/*** determine crop area calculation baseline ***/
				//this.determineBaseline(width, height);
		
				System.out.println("$#3487#"); determineCropArea(width, largeImageWidth, height, largeImageHeight);
			
			} catch (Exception e) {
				LOGGER.error("Image Utils error in constructor", e);
			}
		


		
		
		
	}
	
	
	private void determineCropeable(int width, int specificationsWidth,
			int height, int specificationsHeight) {
		/*** determine if image can be cropped ***/
		// height
		System.out.println("$#3488#"); int y = height - specificationsHeight;
		// width
		System.out.println("$#3489#"); int x = width - specificationsWidth;

		System.out.println("$#3492#"); System.out.println("$#3490#"); if (x < 0 || y < 0) {
			System.out.println("$#3494#"); setCropeable(false);
		}

		System.out.println("$#3495#"); if (x == 0 && y == 0) {
			System.out.println("$#3497#"); setCropeable(false);
		}
		
		
		System.out.println("$#3500#"); System.out.println("$#3498#"); if((height % specificationsHeight) == 0 && (width % specificationsWidth) == 0 ) {
			System.out.println("$#3502#"); setCropeable(false);
		}

		
		
	}


	private void determineCropArea(int width, int specificationsWidth,
			int height, int specificationsHeight) {

		cropAreaWidth = specificationsWidth;
		cropAreaHeight = specificationsHeight;
		
		
		System.out.println("$#3503#"); double factorWidth = new Integer(width).doubleValue() / new Integer(specificationsWidth).doubleValue();
		System.out.println("$#3504#"); double factorHeight = new Integer(height).doubleValue() / new Integer(specificationsHeight).doubleValue();

		double factor = factorWidth;
		
		System.out.println("$#3506#"); System.out.println("$#3505#"); if(factorWidth>factorHeight) {
			factor = factorHeight;
		}
		
		
		// crop factor
/*		double factor = 1;
		if (this.getCropeBaseline() == 0) {// width
			factor = new Integer(width).doubleValue()
					/ new Integer(specificationsWidth).doubleValue();
		} else {// height
			factor = new Integer(height).doubleValue()
					/ new Integer(specificationsHeight).doubleValue();
		}*/

		System.out.println("$#3507#"); double w = factor * specificationsWidth;
		System.out.println("$#3508#"); double h = factor * specificationsHeight;
		
		System.out.println("$#3509#"); if(w==h) {
			System.out.println("$#3510#"); setCropeable(false);
		}
		

		cropAreaWidth = w;
		
		System.out.println("$#3512#"); System.out.println("$#3511#"); if(cropAreaWidth > width)
			cropAreaWidth = width;
		
		cropAreaHeight = h;
		
		System.out.println("$#3514#"); System.out.println("$#3513#"); if(cropAreaHeight > height)
			cropAreaHeight = height;

		/*
		 * if(factor>1) { //determine croping section for(double
		 * i=factor;i>1;i--) { //multiply specifications by factor int newWidth
		 * = (int)(i * specificationsWidth); int newHeight = (int)(i *
		 * specificationsHeight); //check if new size >= original image
		 * if(width>=newWidth && height>=newHeight) { cropAreaWidth = newWidth;
		 * cropAreaHeight = newHeight; break; } } }
		 */

	}
	
	
	public File getCroppedImage(File originalFile, int x1, int y1, int width,
			int height) throws Exception {
		
		System.out.println("$#3515#"); if(!this.cropeable) {
			System.out.println("$#3516#"); return originalFile;
		}

		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String contentType = fileNameMap.getContentTypeFor(originalFile.getName());
		
		String extension = contentType.substring(contentType.indexOf("/"),contentType.length());
		
		BufferedImage image = ImageIO.read(originalFile);
		BufferedImage out = image.getSubimage(x1, y1, width, height);
		File tempFile = File.createTempFile("temp", "." + extension );
		System.out.println("$#3517#"); tempFile.deleteOnExit();
		ImageIO.write(out, extension, tempFile);
		System.out.println("$#3518#"); return tempFile;
	}
	
	public BufferedImage getCroppedImage() throws IOException {
		

			//out if croppedArea == 0 or file is null
		


		
			Rectangle goal = new Rectangle( (int)this.getCropAreaWidth(), (int) this.getCropAreaHeight()); 
			
			//Then intersect it with the dimensions of your image:

			Rectangle clip = goal.intersection(new Rectangle(originalFile.getWidth(), originalFile.getHeight())); 
			
			//Now, clip corresponds to the portion of bi that will fit within your goal. In this case 100 x50.

			//Now get the subImage using the value of clip.

			BufferedImage clippedImg = originalFile.getSubimage(clip.x, clip.y, clip.width, clip.height); 
			

			System.out.println("$#3519#"); return clippedImg;

		
		
		
	}
	


	
	public double getCropAreaWidth() {
		System.out.println("$#3520#"); return cropAreaWidth;
	}

	public void setCropAreaWidth(int cropAreaWidth) {
		this.cropAreaWidth = cropAreaWidth;
	}

	public double getCropAreaHeight() {
		System.out.println("$#3521#"); return cropAreaHeight;
	}

	public void setCropAreaHeight(int cropAreaHeight) {
		this.cropAreaHeight = cropAreaHeight;
	}

	public void setCropeable(boolean cropeable) {
		this.cropeable = cropeable;
	}

	public boolean isCropeable() {
		System.out.println("$#3523#"); System.out.println("$#3522#"); return cropeable;
	}



}
