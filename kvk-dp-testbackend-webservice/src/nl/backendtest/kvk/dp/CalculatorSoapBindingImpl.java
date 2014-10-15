/**
 * CalculatorSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package nl.backendtest.kvk.dp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalculatorSoapBindingImpl implements nl.backendtest.kvk.dp.CalculatorPortType{
	

	public java.lang.String optellen(int waarde1, int waarde2) throws java.rmi.RemoteException {
    	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Date date = new Date();
    	String result = "Resultaat van optellen is: "+(waarde1+waarde2)+" tijd: "+dateFormat.format(date);
        System.out.println(result);
        return result;
    }

	public java.lang.String aftrekken(int waarde1, int waarde2) throws java.rmi.RemoteException {
    	String result = "Resultaat van aftrekken is: "+(waarde1-waarde2);
        System.out.println(result);
        return result;
    }


}
