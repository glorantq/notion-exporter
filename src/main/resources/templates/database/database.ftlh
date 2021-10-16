<#import "../blocks/richText.ftlh" as richText>
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

<#macro getPropertyIcon type><#switch type><#case "RICH_TEXT">align-left<#break><#case "NUMBER">hash<#break><#case "SELECT"><#case "MULTI_SELECT">tag<#break><#default>help-circle</#switch></#macro>

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

        <#default>
            Unknown value!
    </#switch>
</#macro>

<#macro renderSelectValue text color>
    <span class="select-property rich-text-color-${color.name()?string?lower_case}_background">
        ${text}
    </span>
</#macro>