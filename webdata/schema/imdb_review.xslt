<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
    <xsl:for-each select="//div[@id='tn15content']/hr">
	 	<review>
	 		<reviewer>
	 			<source>
		 			http://imdb.com<xsl:value-of select="following-sibling::p/a/@href" />
	 			</source>
	 			<name>
		 			<xsl:value-of select="following-sibling::p/a" />
		 		</name>
	 		</reviewer>
	 		<rating>
	 			<xsl:value-of select="following-sibling::p/img/@alt" />
	 		</rating>
	 		<comment>
				<xsl:value-of select="following-sibling::p[preceding::hr][not(descendant::img) and not(descendant::small) and not(contains(., 'This review may contain spoilers')) and not(contains(., 'Add another review'))]"/>
	 		</comment>
   		</review>
   	</xsl:for-each>
</xsl:template>
</xsl:transform>
