<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
<movie>
    <xsl:for-each select="//div[@id='tn15content']/hr[preceding-sibling::div[1]/@class = 'yn']">
	 	<review>
	 			<xsl:choose>
				<xsl:when test="preceding-sibling::p[2]/a != ''">
				 		<reviewer>
				 			<url>
					 			http://imdb.com<xsl:value-of select="preceding-sibling::p[2]/a/@href" />
				 			</url>
				 			<from>
				 				<xsl:value-of select="preceding-sibling::p[2]/small/text()[starts-with(.,'from')]" />
				 			</from>
				 			<name>
					 			<xsl:value-of select="preceding-sibling::p[2]/a" />
					 		</name>
				 		</reviewer>
				 		<rating>
				 			<xsl:value-of select="preceding-sibling::p[2]/img/@alt" />
				 		</rating>
				  </xsl:when>
				  <xsl:otherwise>
				 		<reviewer>
				 			<url>
					 			http://imdb.com<xsl:value-of select="preceding-sibling::p[3]/a/@href" />
				 			</url>
				 			<from>
				 				<xsl:value-of select="preceding-sibling::p[3]/small/text()[starts-with(.,'from')]" />
				 			</from>
				 			<name>
					 			<xsl:value-of select="preceding-sibling::p[3]/a" />
					 		</name>
				 		</reviewer>
				 		<rating>
				 			<xsl:value-of select="preceding-sibling::p[3]/img/@alt" />
				 		</rating>
				  </xsl:otherwise>
				</xsl:choose>

	 		<comment>
				<xsl:value-of select="preceding-sibling::p[1]"/>
	 		</comment>
   		</review>
   	</xsl:for-each>
</movie>
</xsl:template>
</xsl:transform>
