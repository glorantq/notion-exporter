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