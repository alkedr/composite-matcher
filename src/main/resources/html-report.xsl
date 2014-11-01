<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/checkResult">
        <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html></xsl:text>
        <html>
            <head>
            </head>
            <body>
                <xsl:for-each select="field">
                    <xsl:call-template name="fieldCheck"/>
                </xsl:for-each>

                <xsl:for-each select="nonField">
                    <xsl:call-template name="nonFieldCheck"/>
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>

    <xsl:template name="fieldCheck">
        <xsl:value-of select="/actualValueName"/>
    </xsl:template>

    <xsl:template name="nonFieldCheck">
        <xsl:value-of select="/actualValueName"/>
    </xsl:template>

</xsl:stylesheet>