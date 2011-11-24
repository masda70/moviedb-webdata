<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="list">
   		<movies>
		   	<xsl:apply-templates/>
        </movies>
    </xsl:template>

    <xsl:template match="entry">
        <xsl:copy-of select="document(concat('../data/movies/',.))" />
    </xsl:template>
</xsl:stylesheet>