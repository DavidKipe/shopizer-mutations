package com.salesmanager.core.business.utils;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

/**
 * Utility class for image resize functions
 * @author Carl Samson
 *
 */
public class ProductImageSizeUtils {
	

	private ProductImageSizeUtils() {

	}
	

	/**
	 * Simple resize, does not maintain aspect ratio
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	
	public static BufferedImage resize(BufferedImage image, int width, int height) {
		int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image
				.getType();
		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		System.out.println("$#3525#"); g.setComposite(AlphaComposite.Src);
		System.out.println("$#3526#"); g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		System.out.println("$#3527#"); g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		System.out.println("$#3528#"); g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(image, 0, 0, width, height, null);
		System.out.println("$#3529#"); g.dispose();
		System.out.println("$#3530#"); return resizedImage;
	}
	
	/**
	 * 
	 * @param img
	 * @param targetWidth
	 * @param targetHeight
	 * @param hint
	 * 	{@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
     *  {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
     *  {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
	 * @param higherQuality
	 * @return
	 */
	public static BufferedImage resizeWithHint(BufferedImage img,
			int targetWidth, int targetHeight, Object hint,
			boolean higherQuality) {
		int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
				: BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = (BufferedImage) img;
		int w, h;
		System.out.println("$#3532#"); if (higherQuality) {
			// Use multi-step technique: start with original size, then
			// scale down in multiple passes with drawImage()
			// until the target size is reached
			w = img.getWidth();
			h = img.getHeight();
		} else {
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}

		do {
			System.out.println("$#3534#"); System.out.println("$#3533#"); if (higherQuality && w > targetWidth) {
				System.out.println("$#3536#"); w /= 2;
				System.out.println("$#3538#"); System.out.println("$#3537#"); if (w < targetWidth) {
					w = targetWidth;
				}
			}

			System.out.println("$#3540#"); System.out.println("$#3539#"); if (higherQuality && h > targetHeight) {
				System.out.println("$#3542#"); h /= 2;
				System.out.println("$#3544#"); System.out.println("$#3543#"); if (h < targetHeight) {
					h = targetHeight;
				}
			}

			BufferedImage tmp = new BufferedImage(w, h, type);
			Graphics2D g2 = tmp.createGraphics();
			System.out.println("$#3545#"); g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
			g2.drawImage(ret, 0, 0, w, h, null);
			System.out.println("$#3546#"); g2.dispose();

			ret = tmp;
		System.out.println("$#3547#"); } while (w != targetWidth || h != targetHeight);

		System.out.println("$#3549#"); return ret;
	}
	
	
	public static BufferedImage resizeWithRatio(BufferedImage image, int destinationWidth, int destinationHeight) {

            int type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();

            //*Special* if the width or height is 0 use image src dimensions
												System.out.println("$#3551#"); if (destinationWidth == 0) {
            	destinationWidth = image.getWidth();
            }
												System.out.println("$#3552#"); if (destinationHeight == 0) {
            	destinationHeight = image.getHeight();
            }

            int fHeight = destinationHeight;
            int fWidth = destinationWidth;

            //Work out the resized width/height
												System.out.println("$#3555#"); System.out.println("$#3553#"); if (image.getHeight() > destinationHeight || image.getWidth() > destinationWidth) {
                fHeight = destinationHeight;
                int wid = destinationWidth;
																System.out.println("$#3557#"); float sum = (float)image.getWidth() / (float)image.getHeight();
																System.out.println("$#3558#"); fWidth = Math.round(fHeight * sum);

																System.out.println("$#3560#"); System.out.println("$#3559#"); if (fWidth > wid) {
                    //rezise again for the width this time
																				System.out.println("$#3561#"); fHeight = Math.round(wid/sum);
                    fWidth = wid;
                }
            }

            BufferedImage resizedImage = new BufferedImage(fWidth, fHeight, type);
            Graphics2D g = resizedImage.createGraphics();
												System.out.println("$#3562#"); g.setComposite(AlphaComposite.Src);

												System.out.println("$#3563#"); g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
												System.out.println("$#3564#"); g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
												System.out.println("$#3565#"); g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.drawImage(image, 0, 0, fWidth, fHeight, null);
												System.out.println("$#3566#"); g.dispose();

												System.out.println("$#3567#"); return resizedImage;
	}
	

}
