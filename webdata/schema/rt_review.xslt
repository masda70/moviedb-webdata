<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="div[@class='criticinfo']">
		<xsl:choose>
		<xsl:when test="strong/a/@href != ''">
			<url>
				http://rottentomatoes.com<xsl:value-of select="strong/a/@href" />
			</url>
  			<xsl:if test="following-sibling::div[@class='reviewsnippet']/p[@class='small subtle']/a/@href != ''">
			  	<url>
					<xsl:value-of select="following-sibling::div[@class='reviewsnippet']/p[@class='small subtle']/a/@href" />
				</url>
			</xsl:if>
			<name>
				<xsl:value-of select="strong/a" />
			</name>
			<from>
			 	<xsl:value-of select="descendant::em[@class='subtle']/text()" />
			</from>
		  </xsl:when>
		  <xsl:otherwise>
			  <xsl:if test="following-sibling::div[@class='reviewsnippet']/p[@class='small subtle']/a/@href != ''">
			  	<url>
					<xsl:value-of select="following-sibling::div[@class='reviewsnippet']/p[@class='small subtle']/a/@href" />
				</url>
			</xsl:if>
			<from>
				<xsl:value-of select="descendant::em[@class='subtle']/text()" />
			</from>
		  </xsl:otherwise>
		</xsl:choose>
</xsl:template>
<xsl:template match="/">
<movie>
	<xsl:for-each select="//div[@class='media_block_content']">
		<review>
			<reviewer>
				<xsl:apply-templates select="descendant::div[@class='criticinfo']"/>
			</reviewer>
			<xsl:variable name="rating">
			<xsl:value-of select="descendant::div[@class='reviewsnippet']/p[@class='small subtle']/text()[contains(.,'Original Score: ')]"/>
 			</xsl:variable>
			<xsl:if test="$rating != ''">
 			<rating>
 				<xsl:value-of select="replace($rating,'.*Original Score: ','')" />
 			</rating>
 			</xsl:if>

 			<comment>
				<xsl:value-of select="descendant::div[@class='reviewsnippet']/p[1]"/>
 			</comment>
		</review>
   	</xsl:for-each>
 </movie>
</xsl:template>
</xsl:transform>
    	