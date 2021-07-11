package com.salesmanager.shop.validation;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeanUtils
{
 private BeanUtils(){
        
    }
    
    public static BeanUtils newInstance(){
								System.out.println("$#15819#"); return new BeanUtils();
    }
    
    @SuppressWarnings( "nls" )
    public Object getPropertyValue( Object bean, String property )
        throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        
								System.out.println("$#15820#"); if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
								System.out.println("$#15821#"); if(property == null){
            
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        Class<?> beanClass = bean.getClass();
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor( beanClass, property );
								System.out.println("$#15822#"); if ( propertyDescriptor == null )
        {
            throw new IllegalArgumentException( "No such property " + property + " for " + beanClass + " exists" );
        }

        Method readMethod = propertyDescriptor.getReadMethod();
								System.out.println("$#15823#"); if ( readMethod == null )
        {
            throw new IllegalStateException( "No getter available for property " + property + " on " + beanClass );
        }
								System.out.println("$#15824#"); return readMethod.invoke( bean );
    }

    private PropertyDescriptor getPropertyDescriptor( Class<?> beanClass, String propertyname )
        throws IntrospectionException
    {
        BeanInfo beanInfo = Introspector.getBeanInfo( beanClass );
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        PropertyDescriptor propertyDescriptor = null;
								System.out.println("$#15826#"); System.out.println("$#15825#"); for ( int i = 0; i < propertyDescriptors.length; i++ )
        {
            PropertyDescriptor currentPropertyDescriptor = propertyDescriptors[i];
												System.out.println("$#15827#"); if ( currentPropertyDescriptor.getName().equals( propertyname ) )
            {
                propertyDescriptor = currentPropertyDescriptor;
            }

        }
								System.out.println("$#15828#"); return propertyDescriptor;
    }
    
}
