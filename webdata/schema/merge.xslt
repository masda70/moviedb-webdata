<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="list">
    	<list>
          	<xsl:apply-templates/>
        </list>
    </xsl:template>

    <xsl:template match="entry">
        <xsl:value-of select="document(.)" />
    </xsl:template>
</xsl:stylesheet>