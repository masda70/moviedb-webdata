<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<movies>
   		<xsl:template match="list">
		   	<xsl:apply-templates/>
	    </xsl:template>
    </movies>

    <xsl:template match="entry">
		<xsl:for-each select="document(concat('../data/',.,'.xml'))/movie">
			<xsl:copy-of select="."/>
    	</xsl:for-each>
    </xsl:template>
</xsl:stylesheet>