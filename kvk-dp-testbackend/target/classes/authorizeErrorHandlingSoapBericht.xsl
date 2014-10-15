<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
	<xsl:output method="xml"/>
	<!-- Genereer een soap fault behorend bij het fout gaan tijdens authorisatie - authenticatie -->
	<xsl:template match="/">
		<soapenv:Envelope>
			<soapenv:Body>
				<soapenv:Fault>
					<faultcode>soapenv:client</faultcode>
					<faultstring>client not authorized</faultstring>
					<faultactor>Integratielaag</faultactor>
					<detail>
						<!-- Hier komt de eigenlijke foutboodschap -->
					</detail>
				</soapenv:Fault>
			</soapenv:Body>
		</soapenv:Envelope>
	</xsl:template>
</xsl:stylesheet>