<#--
    Copyright (C) 2021 Gerber Lóránt Viktor

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
-->

<#import "../blocks/richText.ftlh" as richText>

<#import "./table.ftlh" as tableRenderer>
<#import "./board.ftlh" as boardRenderer>

<#macro renderDatabaseProperties page>
    <div class="database-property-values">
        <#list page.properties.entrySet() as entry>
            <#assign key = entry.getKey()>
            <#assign value = entry.getValue()>

            <#assign valueType = value.type.name()>

            <#if valueType == "TITLE">
                <#continue>
            </#if>

            <div class="property-container">
                <span class="property-name">
                    <i data-feather="<@getPropertyIcon type=valueType />"></i> ${key}
                </span>
                <span class="property-value">
                    <@getPropertyContent value=value page=page />
                </span>
            </div>
        </#list>
    </div>
</#macro>

<#macro renderDatabase page containingPage>
    <#assign databaseRecords = queryDatabase(page.pageId)>

    <div class="database-container">
        <#assign display = getDatabaseDisplay(page.pageId)>
        <#switch display>
            <#case "TABLE">
                <@tableRenderer.render page=page containingPage=containingPage databaseRecords=databaseRecords />
                <#break>

            <#case "BOARD">
                <@boardRenderer.render page=page containingPage=containingPage databaseRecords=databaseRecords />
                <#break>

            <#-- TODO: Calendar renderer -->
            <#default>
                <blockquote class="renderer-error">No database renderer for ${display}!</blockquote>
        </#switch>
    </div>
</#macro>

<#macro getPropertyIcon type><#switch type><#case "RICH_TEXT">align-left<#break><#case "NUMBER">hash<#break><#case "SELECT"><#case "MULTI_SELECT">tag<#break><#case "TITLE">type<#break><#default>help-circle</#switch></#macro>

<#macro getPropertyContent value page>
    <#assign type = value.type.name()>
    <#switch type>
        <#case "RICH_TEXT">
            <#list value.richText as text>
                <@richText.renderRichText richText=text page=page />
            </#list>
            <#break>

        <#case "NUMBER">
            ${value.number?int}
            <#break>

        <#case "SELECT">
            <@renderSelectValue value.select.name value.select.color />
            <#break>

        <#case "MULTI_SELECT">
            <#list value.multiSelect as select>
                <@renderSelectValue select.name select.color />
            </#list>
            <#break>

        <#case "TITLE">
            <#list value.title as text>
               <@richText.renderRichText richText=text page=page />
            </#list>
            <#break>

        <#default>
            Unknown value!
    </#switch>
</#macro>

<#macro renderSelectValue text color>
    <span class="select-property rich-text-color-${color.name()?string?lower_case}_background">
        ${text}
    </span>
</#macro>