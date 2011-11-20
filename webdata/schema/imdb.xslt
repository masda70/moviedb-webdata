<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
	<movie>
		<original_title>
			<xsl:value-of select="//title"/>
		</original_title>
	    <xsl:for-each select="TODO">
    		<alt_title>
    			<xsl:value-of select="TODO"/>
    		</alt_title>
    	</xsl:for-each>
    	<review>
			<xsl:value-of select="TODO"/>
    	</review>
		<year>
			<xsl:value-of select="//time[@itemprop='datePublished']"/>
		</year>
		<xsl:for-each select="TODO">
    		<country>
    			<xsl:value-of select="TODO"/>
    		</country>
    	</xsl:for-each>
		<xsl:for-each select="TODO">
    		<director>
    			<xsl:value-of select="TODO"/>
    		</director>
    	</xsl:for-each>
		<xsl:for-each select="TODO">
    		<producer>
    			<xsl:value-of select="TODO"/>
    		</producer>
    	</xsl:for-each>
		<xsl:for-each select="TODO">
    		<genre>
    			<xsl:value-of select="TODO"/>
    		</genre>
    	</xsl:for-each>
		<xsl:for-each select="TODO">
    		<synopsis>
    			<xsl:value-of select="TODO"/>
    		</synopsis>
    	</xsl:for-each>
		<xsl:for-each select="TODO">
    		<actor>
    			<xsl:value-of select="TODO"/>
    		</actor>
    	</xsl:for-each>
	</movie>
</xsl:template>
</xsl:transform>
