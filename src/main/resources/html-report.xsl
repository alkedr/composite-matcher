<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:str="http://exslt.org/strings"
        >

    <xsl:template match="/">
        <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html></xsl:text>
        <html>
            <head>
            </head>
            <body>
                <xsl:for-each select="fieldChecks">
                    <xsl:call-template name="fieldCheck"/>
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>

    <xsl:template name="fieldCheck">
        <xsl:value-of select="/actualValueName"/>
    </xsl:template>

</xsl:stylesheet>