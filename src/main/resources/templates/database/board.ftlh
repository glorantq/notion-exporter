<#import "./database.ftlh" as database>

<#macro render page containingPage databaseRecords>
    <#assign columns = getColumnsToRender(page.pageId)>
    <#assign groupBy = getGroupBy(page.pageId)>

    <#assign groupedData = performKanbanGrouping(databaseRecords, groupBy)>

    <div class="kanban-container">
        <#list groupedData.entrySet() as entry>
            <#assign key = entry.getKey()>
            <#assign values = entry.getValue()>
            <div class="kanban-column">
                <span class="kanban-header"><@database.getPropertyContent value=key page=page /> <span class="kanban-card-count">${values?size}</span></span>
                <#list values as value>
                    <div class="kanban-card" onclick="location.href='${resolvePageLink(value.pageId, containingPage)}';">
                        <span class="kanban-card-title"><@_getTitle value.properties /></span>
                        <#list columns as column>
                            <#assign recordValue = value.properties[column]>
                            <#assign valueType = recordValue.type.name()>

                            <#if valueType != "TITLE">
                                <span class="kanban-value"><@database.getPropertyContent value=recordValue page=page /></span>
                            </#if>
                        </#list>
                    </div>
                </#list>
            </div>
        </#list>
    </div>
</#macro>

<#macro _getTitle properties>
    <#list properties.entrySet() as entry>
        <#assign name = entry.getKey()>
        <#assign value = entry.getValue()>

        <#assign valueType = value.type.name()>
        <#if valueType == "TITLE">
            <@database.getPropertyContent value=value page=page />
        </#if>
    </#list>
</#macro>
