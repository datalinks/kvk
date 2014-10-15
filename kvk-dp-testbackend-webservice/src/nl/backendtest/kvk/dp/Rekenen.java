/**
 * Rekenen.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package nl.backendtest.kvk.dp;

public class Rekenen  implements java.io.Serializable {
    private int waarde1;

    private int waarde2;

    public Rekenen() {
    }

    public Rekenen(
           int waarde1,
           int waarde2) {
           this.waarde1 = waarde1;
           this.waarde2 = waarde2;
    }


    /**
     * Gets the waarde1 value for this Rekenen.
     * 
     * @return waarde1
     */
    public int getWaarde1() {
        return waarde1;
    }


    /**
     * Sets the waarde1 value for this Rekenen.
     * 
     * @param waarde1
     */
    public void setWaarde1(int waarde1) {
        this.waarde1 = waarde1;
    }


    /**
     * Gets the waarde2 value for this Rekenen.
     * 
     * @return waarde2
     */
    public int getWaarde2() {
        return waarde2;
    }


    /**
     * Sets the waarde2 value for this Rekenen.
     * 
     * @param waarde2
     */
    public void setWaarde2(int waarde2) {
        this.waarde2 = waarde2;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Rekenen)) return false;
        Rekenen other = (Rekenen) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.waarde1 == other.getWaarde1() &&
            this.waarde2 == other.getWaarde2();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += getWaarde1();
        _hashCode += getWaarde2();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Rekenen.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://dp.kvk.backendtest.nl", ">rekenen"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("waarde1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://dp.kvk.backendtest.nl", "waarde1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("waarde2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://dp.kvk.backendtest.nl", "waarde2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
