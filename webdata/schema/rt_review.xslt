<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
	<xsl:for-each select="//div[@class='media_block_content']">
		<review>
			<reviewer>
				<source>
					http://rottentomatoes.com<xsl:value-of select="descendant::div[@class='criticinfo']/strong/a/@href" />
	 			</source>
	 			<name>
	 				<xsl:value-of select="descendant::div[@class='criticinfo']/strong/a" />
	 			</name>
 			</reviewer>
 			<rating>
 				<xsl:value-of select="descendant::div[@class='reviewsnippet']/p[@class='small subtle']/text()[contains(.,'Original Score: ')]" />
 			</rating>
 			<comment>
				<xsl:value-of select="descendant::div[@class='reviewsnippet']/p[1]"/>
 			</comment>
		</review>
   	</xsl:for-each>
</xsl:template>
</xsl:transform>
    	