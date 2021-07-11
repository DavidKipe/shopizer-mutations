package com.salesmanager.core.business.utils;

import java.math.BigDecimal;

import com.salesmanager.core.constants.MeasureUnit;
import com.salesmanager.core.model.merchant.MerchantStore;

public class DataUtils {
	
	/**
	 * Removes dashes
	 * @param postalCode
	 * @return
	 */
	public static String trimPostalCode(String postalCode) {

		String pc = postalCode.replaceAll("[^a-zA-Z0-9]", "");

		System.out.println("$#3466#"); return pc;

	}
	
	
	/**
	 * Get the measure according to the appropriate measure base. If the measure
	 * configured in store is LB and it needs KG then the appropriate
	 * calculation is done
	 * 
	 * @param weight
	 * @param store
	 * @param base
	 * @return
	 */
	public static double getWeight(double weight, MerchantStore store,
			String base) {

		double weightConstant = 2.2;
		System.out.println("$#3467#"); if (base.equals(MeasureUnit.LB.name())) {
			System.out.println("$#3468#"); if (store.getWeightunitcode().equals(MeasureUnit.LB.name())) {
				System.out.println("$#3469#"); return new BigDecimal(String.valueOf(weight)).setScale(2,
						BigDecimal.ROUND_HALF_UP).doubleValue();
			} else {// pound = kilogram
				System.out.println("$#3470#"); double answer = weight * weightConstant;
				BigDecimal w = new BigDecimal(answer);
				System.out.println("$#3471#"); return w.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
		} else {// need KG
			System.out.println("$#3472#"); if (store.getWeightunitcode().equals(MeasureUnit.KG.name())) {
				System.out.println("$#3473#"); return new BigDecimal(String.valueOf(weight)).setScale(2,
						BigDecimal.ROUND_HALF_UP).doubleValue();
			} else {

				System.out.println("$#3474#"); double answer = weight / weightConstant;
				BigDecimal w = new BigDecimal(answer);
				System.out.println("$#3475#"); return w.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

			}
		}
	}
	
	/**
	 * Get the measure according to the appropriate measure base. If the measure
	 * configured in store is IN and it needs CM or vise versa then the
	 * appropriate calculation is done
	 * 
	 * @param weight
	 * @param store
	 * @param base
	 * @return
	 */
	public static double getMeasure(double measure, MerchantStore store,
			String base) {

		System.out.println("$#3476#"); if (base.equals(MeasureUnit.IN.name())) {
			System.out.println("$#3477#"); if (store.getSeizeunitcode().equals(MeasureUnit.IN.name())) {
				System.out.println("$#3478#"); return new BigDecimal(String.valueOf(measure)).setScale(2,
						BigDecimal.ROUND_HALF_UP).doubleValue();
			} else {// centimeter (inch to centimeter)
				double measureConstant = 2.54;

				System.out.println("$#3479#"); double answer = measure * measureConstant;
				BigDecimal w = new BigDecimal(answer);
				System.out.println("$#3480#"); return w.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

			}
		} else {// need CM
			System.out.println("$#3481#"); if (store.getSeizeunitcode().equals(MeasureUnit.CM.name())) {
				System.out.println("$#3482#"); return new BigDecimal(String.valueOf(measure)).setScale(2)
						.doubleValue();
			} else {// in (centimeter to inch)
				double measureConstant = 0.39;

				System.out.println("$#3483#"); double answer = measure * measureConstant;
				BigDecimal w = new BigDecimal(answer);
				System.out.println("$#3484#"); return w.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

			}
		}

	}

}
