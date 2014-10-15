/**
 * CalculatorPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package nl.backendtest.kvk.dp;

public interface CalculatorPortType extends java.rmi.Remote {
    public java.lang.String optellen(int waarde1, int waarde2) throws java.rmi.RemoteException;
    public java.lang.String aftrekken(int waarde1, int waarde2) throws java.rmi.RemoteException;
}
