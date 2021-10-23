<#import "./database.ftlh" as database>

<#macro render page containingPage databaseRecords>
    <#assign columns = getColumnsToRender(page.pageId)>
    <table>
        <tr>
            <#list columns as column>
                <#assign value = page.properties[column]>
                <#assign valueType = value.type.name()>

                <th><span><i data-feather="<@database.getPropertyIcon type=valueType />"></i> ${column}</span></th>
            </#list>
        </tr>
        <#list databaseRecords as record>
            <tr>
                <#list columns as column>
                    <#assign value = page.properties[column]>
                    <#assign valueType = value.type.name()>

                    <#assign recordValue = record.properties[column]>
                    <#if valueType == "TITLE">
                        <td><span><a href="${resolvePageLink(record.pageId, containingPage)}"><@database.getPropertyContent value=recordValue page=page /></a></span></td>
                    <#else>
                        <td><@database.getPropertyContent value=recordValue page=page /></td>
                    </#if>
                </#list>
            </tr>
        </#list>
    </table>
</#macro>