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

<#import "richText.ftlh" as richText>
<p>
    <#list block.text as text>
        <@richText.renderRichText richText=text page=page />
    </#list>
</p>

<#if block.hasChildren>
    <#assign childBlocks = getBlockChildren(block)>
    <#list childBlocks as child>
        ${renderBlock(child, page)?no_esc}
    </#list>
</#if>