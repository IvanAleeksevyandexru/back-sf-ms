<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="mksLogoImageData" select="'mksLogoImageData'"/>
    <xsl:param name="svgLogo" select="'svgLogo'"/>
    <xsl:param name="pngGosuslugiLogo" select="'pngGosuslugiLogo'"/>
    <xsl:param name="orderId" select="'orderId'"/>
    <xsl:param name="serviceId" select="'serviceId'"/>

    <xsl:template match="/">
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master master-name="one" page-height="307mm">
                    <fo:region-body margin-top="20px"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="one">
                <fo:flow flow-name="xsl-region-body">
                    <fo:block-container width="100%" font-family="Helvetica-Neue" border-bottom="3px solid #0063b0"
                                        padding-bottom="15px">
                        <fo:block margin-left="70px" margin-right="35px">
                            <fo:list-block provisional-distance-between-starts="33%">
                                <fo:list-item>
                                    <fo:list-item-label end-indent="label-end()">
                                        <fo:block text-align="left">
                                            <fo:external-graphic src="data:image/png+xml;base64,{$pngGosuslugiLogo}"
                                                                 content-width="135px" content-height="20px"/>
                                        </fo:block>
                                        <fo:block>
                                            <fo:inline font-family="ALSEkibastuz" font-size="15" font-weight="bold" color="#0065B1">Проще, чем кажется</fo:inline>
                                        </fo:block>
                                    </fo:list-item-label>
                                    <fo:list-item-body start-indent="body-start()">
                                        <fo:block text-align="right" font-family="Helvetica-Neue" font-size="12px">
                                            <xsl:if test="not(contains($serviceId, '00308'))">
                                                    <fo:block font-weight="700" color="#000000">Заявление</fo:block>
                                                    <fo:block color="#535B63" padding-top="7px" font-weight="300">
                                                        № <xsl:value-of select="$orderId"/>
                                                    </fo:block>
                                            </xsl:if>
                                        </fo:block>
                                    </fo:list-item-body>
                                </fo:list-item>
                            </fo:list-block>
                        </fo:block>
                    </fo:block-container>

<!--                    <xsl:apply-templates select="model/item"/>-->
                    <xsl:apply-templates select="list/item"/>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <xsl:template match="item">
        <xsl:param name="level" select="1"/>
        <xsl:param name="offset" select="'0'"/>
        <xsl:param name="numberOfContainer" select="''"/>

        <xsl:variable name="id" select="fullPath"/>
        <xsl:variable name="visible" select="visible"/>
        <xsl:variable name="className" select="className"/>
        <xsl:variable name="label" select="label"/>
        <xsl:variable name="longLabel" select="longLabel"/>
        <xsl:variable name="title" select="title"/>
        <xsl:variable name="value" select="value"/>
        <xsl:variable name="textValue" select="textValue"/>
        <xsl:variable name="elementTitle">
            <xsl:choose>
                <xsl:when test="normalize-space($title) != '' and normalize-space($title) != ' '">
                    <xsl:value-of select="normalize-space($title)"/>
                </xsl:when>
                <xsl:when test="normalize-space($label) != '' and normalize-space($label) != ' '">
                    <xsl:value-of select="normalize-space($label)"/>
                </xsl:when>
                <xsl:when test="normalize-space($longLabel) != '' and normalize-space($longLabel) != ' '">
                    <xsl:value-of select="normalize-space($longLabel)"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="not($visible) or $visible = 'true'">
                <xsl:choose>
                    <xsl:when test="$className = 'Form'">
                        <fo:block-container width="580px">
                            <fo:block font-family="ALSEkibastuz"
                                      margin='30px 0px 0px 70px'
                                      font-size="25px"
                                      font-weight="400">
                                <xsl:value-of select="$elementTitle"/>
                            </fo:block>
                        </fo:block-container>
                    </xsl:when>
                    <xsl:when test="$className = 'FormStep'">
                        <xsl:choose>
                            <xsl:when test="normalize-space($elementTitle) != ''">
                                <fo:block-container width="100%">
                                    <fo:block width="100%" font-family="ALSEkibastuz" font-weight="700"
                                              font-size="15px" margin='10px 70px 10px 70px'>
                                        <fo:list-block provisional-distance-between-starts="6%">
                                            <fo:list-item>
                                                <fo:list-item-label end-indent="label-end()">
                                                    <fo:block text-align="right">
                                                        <fo:inline font-family="ALSEkibastuz" font-weight="700"
                                                                   font-size="13px">
                                                            <xsl:for-each
                                                                    select="../item[visible = 'true' and normalize-space(title) != '' and normalize-space(title) != ' ']">
                                                                <xsl:if test="fullPath = $id">
                                                                    <xsl:value-of select="position()"/>.
                                                                </xsl:if>
                                                            </xsl:for-each>
                                                        </fo:inline>
                                                    </fo:block>
                                                </fo:list-item-label>
                                                <fo:list-item-body start-indent="body-start()">
                                                    <fo:block text-align="left" padding="0 2px 0 2 px">
                                                        <fo:inline>
                                                            <xsl:value-of select="$elementTitle"/>
                                                        </fo:inline>
                                                    </fo:block>
                                                </fo:list-item-body>
                                            </fo:list-item>
                                        </fo:list-block>
                                    </fo:block>
                                </fo:block-container>
                            </xsl:when>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:when test="$className = 'Panel' or $className = 'ElectronMedicalDocs'">
                        <xsl:choose>
                            <xsl:when test="normalize-space($elementTitle) != ''">
                                <fo:block-container width="580px">
                                    <xsl:variable name="margin" select="number($offset) + 25"/>
                                    <fo:block width="580px" font-family="ALSEkibastuz" font-weight="700"
                                              font-size="12px" margin='20px 0px 20px {$margin + 72}px'>
                                        <fo:inline padding-left="5px">
                                            <xsl:value-of select="$elementTitle"/>
                                        </fo:inline>
                                    </fo:block>
                                </fo:block-container>
                            </xsl:when>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:when test="$className = 'FieldUpload' or $className = 'FieldText' or $className = 'FieldTextDate'
                        or $className = 'FieldTextArea' or $className = 'Kladr' or $className = 'FieldDropdown'
                        or $className = 'FieldRadio' or $className = 'FieldCheckbox'
                        or $className = 'FieldMapWithList' or $className = 'FieldMapWithListDC' or $className = 'ImageEditor'
                        or $className = 'FieldDropdownHierarchic' or $className = 'Fias'
						or $className = 'FieldDropdownHierarchicMultiple'or $className = 'KladrAddressRKN'
						or $className = 'FieldInn'or $className = 'FieldPlaceOfBirth'
						or $className = 'FieldSeriesOfDrivingLicense'or $className = 'FieldNumberOfDrivingLicense'
						or $className = 'FieldValidToOfDrivingLicense'or $className = 'FieldRegistrationAddress'
						or $className = 'FieldAddressOfResidence'or $className = 'FieldVehicleName'
						or $className = 'FieldRegistrationMarkOfTheVehicle' or $className = 'FieldSeriesOfTheRegistrationCertificateTS'
						or $className = 'FieldNumberOfTheRegistrationCertificateTS' or $className = 'FieldDateOfIssueDrivingLicense'
						or $className = 'KladrJuridicalPersonRKN' or $className = 'KladrNaturalPersonRKN'
						or $className = 'FieldYear' or $className = 'FieldMonthExt'">
                        <xsl:call-template name="fieldBlock">
                            <xsl:with-param name="id" select="$id"/>
                            <xsl:with-param name="title" select="$elementTitle"/>
                            <xsl:with-param name="value" select="$value"/>
                            <xsl:with-param name="textValue" select="$textValue"/>
                            <xsl:with-param name="offset" select="$offset"/>
                            <xsl:with-param name="emptyMessage">Не заполнено</xsl:with-param>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$className  = 'FieldDropdownMultiple'">
                        <xsl:variable name="textValue1" select="text__value"/>
                        <xsl:call-template name="fieldBlock">
                            <xsl:with-param name="id" select="$id"/>
                            <xsl:with-param name="title" select="$elementTitle"/>
                            <xsl:with-param name="value" select="$value"/>
                            <xsl:with-param name="textValue" select="$textValue1"/>
                            <xsl:with-param name="offset" select="$offset"/>
                            <xsl:with-param name="emptyMessage">Не заполнено</xsl:with-param>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$className  = 'FieldDropdownMultipleExt2v'">
                        <xsl:variable name="textValue1" select="text__value"/>
                        <xsl:call-template name="fieldBlock">
                            <xsl:with-param name="id" select="$id"/>
                            <xsl:with-param name="title" select="$elementTitle"/>
                            <xsl:with-param name="value" select="$value"/>
                            <xsl:with-param name="textValue" select="$textValue1"/>
                            <xsl:with-param name="offset" select="$offset"/>
                            <xsl:with-param name="emptyMessage">Не заполнено</xsl:with-param>
                        </xsl:call-template>
                    </xsl:when>
                </xsl:choose>

                <xsl:apply-templates select="items/item">
                    <xsl:with-param name="level" select="$level + 1"/>
                    <xsl:with-param name="numberOfContainer">
                        <xsl:choose>
                            <xsl:when test="$className = 'FormStep' and normalize-space(title) != ''">
                                <xsl:for-each select="../item[visible = 'true' and normalize-space(title) != '' and normalize-space(title) != ' ']">
                                    <xsl:if test="fullPath = $id">
                                        <xsl:value-of select="position()"/>
                                    </xsl:if>
                                </xsl:for-each>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="$numberOfContainer"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:with-param>
                    <xsl:with-param name="offset">
                        <xsl:choose>
                            <xsl:when test="$className = 'Panel' and normalize-space($elementTitle) != ''">
                                <xsl:value-of select="number($offset) + 20"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="'0'"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:with-param>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="fieldBlock">
        <xsl:param name="offset"/>
        <xsl:param name="id"/>
        <xsl:param name="title"/>
        <xsl:param name="value"/>
        <xsl:param name="textValue"/>
        <xsl:param name="emptyMessage" select="'Не заполнено'"/>
        <fo:block-container width="320px" margin='12px 0pt 0pt {number($offset)*0.6 + 51}px'>
            <fo:block font-family="Helvetica-Neue">
                <xsl:attribute name="id">
                    <xsl:value-of select="$id"/>
                </xsl:attribute>
                <xsl:if test="$title != ''">
                    <fo:block font-weight="200" font-size="10px" color="#969696" font-family="Helvetica-Neue">
                        <xsl:value-of select="$title"/>
                    </fo:block>
                </xsl:if>
                <fo:block font-weight="200" font-size="10px" color="#31363b" font-family="Helvetica-Neue"
                          border-bottom="1px solid #CDD1D4" margin-top="3px">
                    <xsl:choose>
                        <xsl:when test="$textValue != ''">
                            <xsl:choose>
                                <xsl:when test="contains($textValue, ' ') or string-length($textValue) &lt; 51">
                                    <xsl:value-of select="$textValue"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:call-template name="wrap">
                                        <xsl:with-param name="value" select="$textValue"/>
                                    </xsl:call-template>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:when>
                        <xsl:when test="$value != ''">
                            <xsl:choose>
                                <xsl:when test="contains($value, ' ') or string-length($value) &lt; 51">
                                    <xsl:value-of select="$value"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:call-template name="wrap">
                                        <xsl:with-param name="value" select="$value"/>
                                    </xsl:call-template>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$emptyMessage"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </fo:block>
            </fo:block>
        </fo:block-container>
    </xsl:template>

    <xsl:template name="wrap">
        <xsl:param name="value" select="value"/>
        <xsl:param name="result" select="result"/>
        <xsl:variable name="crlf">
            <xsl:text>&#10;</xsl:text>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="string-length($value) > 0">
                <xsl:variable name="newValue">
                    <xsl:value-of select="substring($value, 51)"/>
                </xsl:variable>
                <xsl:variable name="newResult">
                    <xsl:choose>
                        <xsl:when test="$result = ''">
                            <xsl:value-of select="substring($value, 1, 50)"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="concat($result, $crlf, substring($value, 1, 50))"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:call-template name="wrap">
                    <xsl:with-param name="value" select="$newValue"/>
                    <xsl:with-param name="result" select="$newResult"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$result"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
