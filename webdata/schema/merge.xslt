<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 <xsl:template match="/">
    <xsl:element name="{movies/@newRoot}">
      <xsl:apply-templates select="mergeData/fileList/fileItem"/>
    </xsl:element>
  </xsl:template>
  <xsl:template match="fileItem">    
    <xsl:copy-of select="document(.)"/>
  </xsl:template>
    <xsl:template match="list">
    	<movies>
          	<xsl:apply-templates/>
        </movies>
    </xsl:template>

    <xsl:template match="entry">
        <xsl:value-of select="document(concat('../data/movies/',.,'.xml'))" />
    </xsl:template>
</xsl:stylesheet>