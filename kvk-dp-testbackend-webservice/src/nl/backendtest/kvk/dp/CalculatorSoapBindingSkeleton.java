/**
 * CalculatorSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package nl.backendtest.kvk.dp;

public class CalculatorSoapBindingSkeleton implements nl.backendtest.kvk.dp.CalculatorPortType, org.apache.axis.wsdl.Skeleton {
    private nl.backendtest.kvk.dp.CalculatorPortType impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://dp.kvk.backendtest.nl", "waarde1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://dp.kvk.backendtest.nl", "waarde2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("optellen", _params, new javax.xml.namespace.QName("http://dp.kvk.backendtest.nl", "return"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://dp.kvk.backendtest.nl", "optellen"));
        _oper.setSoapAction("optellen");
        _myOperationsList.add(_oper);
        if (_myOperations.get("optellen") == null) {
            _myOperations.put("optellen", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("optellen")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://dp.kvk.backendtest.nl", "waarde1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://dp.kvk.backendtest.nl", "waarde2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("aftrekken", _params, new javax.xml.namespace.QName("http://dp.kvk.backendtest.nl", "return"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://dp.kvk.backendtest.nl", "aftrekken"));
        _oper.setSoapAction("aftrekken");
        _myOperationsList.add(_oper);
        if (_myOperations.get("aftrekken") == null) {
            _myOperations.put("aftrekken", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("aftrekken")).add(_oper);
    }

    public CalculatorSoapBindingSkeleton() {
        this.impl = new nl.backendtest.kvk.dp.CalculatorSoapBindingImpl();
    }

    public CalculatorSoapBindingSkeleton(nl.backendtest.kvk.dp.CalculatorPortType impl) {
        this.impl = impl;
    }
    public java.lang.String optellen(int waarde1, int waarde2) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.optellen(waarde1, waarde2);
        return ret;
    }

    public java.lang.String aftrekken(int waarde1, int waarde2) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.aftrekken(waarde1, waarde2);
        return ret;
    }

}
