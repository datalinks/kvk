<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:dp="http://www.datapower.com/extensions" xmlns:dpconfig="http://www.datapower.com/param/config" extension-element-prefixes="dp" exclude-result-prefixes="dpconfig">
	<xsl:param name="dpconfig:protocolcode" select="'500'"/>
	<dp:param name="dpconfig:protocolcode" type="dmString" required="true" xmlns="">
		<display>HTTP response code</display>
	</dp:param>
	<xsl:param name="dpconfig:protocolreason" select="'Stylesheet processing failed.'"/>
	<dp:param name="dpconfig:protocolreason" type="dmString" required="true" xmlns="">
		<display>Error reason</display>
	</dp:param>
	<xsl:template match="/">
		<!-- Set the HTTP response code and log message for a response -->
		<dp:set-variable name="'var://service/error-protocol-response'" value="$dpconfig:protocolcode"/>
		<dp:set-variable name="'var://service/error-protocol-reason-phrase'" value="$dpconfig:protocolreason"/>
	</xsl:template>
</xsl:stylesheet>


