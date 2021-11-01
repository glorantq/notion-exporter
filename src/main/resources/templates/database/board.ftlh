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
                <span class="kanban-header"><@database.getPropertyContent value=key page=page containingPage=containingPage /> <span class="kanban-card-count">${values?size}</span></span>
                <#list values as value>
                    <div class="kanban-card" onclick="window.location.href='${resolvePageLink(value.pageId, containingPage)}';">
                        <span class="kanban-card-title"><@_getTitle properties=value.properties record=value containingPage=containingPage /></span>
                        <#list columns as column>
                            <#assign recordValue = value.properties[column]>
                            <#assign valueType = recordValue.type.name()>

                            <#if valueType != "TITLE">
                                <span class="kanban-value"><@database.getPropertyContent value=recordValue page=value containingPage=containingPage /></span>
                            </#if>
                        </#list>
                    </div>
                </#list>
            </div>
        </#list>
    </div>
</#macro>

<#macro _getTitle properties record containingPage>
    <#list properties.entrySet() as entry>
        <#assign name = entry.getKey()>
        <#assign value = entry.getValue()>

        <#assign valueType = value.type.name()>
        <#if valueType == "TITLE">
            <@database.getPropertyContent value=value page=record containingPage=containingPage />
        </#if>
    </#list>
</#macro>
