<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
	<movie>
		<rt_rating>
			<xsl:value-of select="//p[@class='critic_stats'][contains(.,'Average Rating:')]/span[1]"/>
		</rt_rating>
		<rt_meter>
			<xsl:value-of select="//span[@itemprop='ratingValue']"/>
		</rt_meter>
	</movie>
</xsl:template>
</xsl:transform>
