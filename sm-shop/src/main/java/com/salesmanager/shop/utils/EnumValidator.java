package com.salesmanager.shop.utils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * Validates values of a String used as payload in REST service
 * Solution taken from https://funofprograming.wordpress.com/2016/09/29/java-enum-validator/
 * @author c.samson
 *
 */
public class EnumValidator implements ConstraintValidator<Enum, String>
{
    private Enum annotation;
 
    @Override
    public void initialize(Enum annotation)
    {
        this.annotation = annotation;
    }
 
    @Override
    public boolean isValid(String valueForValidation, ConstraintValidatorContext constraintValidatorContext)
    {
        boolean result = false;
         
        Object[] enumValues = this.annotation.enumClass().getEnumConstants();
         
								System.out.println("$#15699#"); if(enumValues != null)
        {
            for(Object enumValue:enumValues)
            {
																System.out.println("$#15700#"); if(valueForValidation.equals(enumValue.toString())
                   || (this.annotation.ignoreCase() && valueForValidation.equalsIgnoreCase(enumValue.toString())))
                {
                    result = true; 
                    break;
                }
            }
        }
         
								System.out.println("$#15704#"); System.out.println("$#15703#"); return result;
    }
}
