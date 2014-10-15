/**
 * Calculator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package nl.backendtest.kvk.dp;

public interface Calculator extends javax.xml.rpc.Service {
    public java.lang.String getCalculatorPortAddress();

    public nl.backendtest.kvk.dp.CalculatorPortType getCalculatorPort() throws javax.xml.rpc.ServiceException;

    public nl.backendtest.kvk.dp.CalculatorPortType getCalculatorPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
