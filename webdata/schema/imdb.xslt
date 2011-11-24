<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
	<movie>
		<original_title>
			<xsl:value-of select="//h1[@itemprop='name']/text()"/>
		</original_title>
		<original_title>
			<xsl:value-of select="//span[@itemprop='ratingValue']"/>
		</original_title>
		<year>
			<xsl:value-of select="//h1[@itemprop='name']/span/a"/>
		</year>
		<xsl:for-each select="//h4[contains(., 'Country:')]/following-sibling::a">
    		<country>
    			<xsl:value-of select="."/>
    		</country>
    	</xsl:for-each>
   		<director>
			<xsl:value-of select="//a[@itemprop='director']"/>
   		</director>
		<xsl:for-each select="//h4[contains(., 'Production Co:')]/following-sibling::a">
    		<producer>
    			<xsl:value-of select="."/>
    		</producer>
    	</xsl:for-each>
		<xsl:for-each select="//td[@id='overview-top']/div[@class='infobar']/a">
    		<genre>
    			<xsl:value-of select="."/>
    		</genre>
    	</xsl:for-each>
		<synopsis>
			<xsl:value-of select="//p[@itemprop='description']"/>
		</synopsis>
		<xsl:for-each select="//h4[contains(., 'Stars:')]/following-sibling::a">
    		<actor>
    			<xsl:value-of select="."/>
    		</actor>
    	</xsl:for-each>
	</movie>
</xsl:template>
</xsl:transform>
