<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
<result><xsl:value-of select="//a/@href[starts-with(.,'http://www.rottentomatoes.com/m/')]"/></result>
</xsl:template>
</xsl:transform>
