<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" doctype-system="http://www.dptinfo.ens-cachan.fr/~dmontoya/webdata/movie.dtd" indent="yes" />
	<xsl:template match="list">
  		<movies>
	   		<xsl:apply-templates/>
	   	</movies>
    </xsl:template>
   
    <xsl:template match="entry">
		<xsl:for-each select="document(.)/movies/movie">
			<xsl:copy-of select="."/>
    	</xsl:for-each>
    </xsl:template>
</xsl:stylesheet>