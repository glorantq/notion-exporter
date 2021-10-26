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

<#import "./richText.ftlh" as richText>
<#import "../common.ftl" as common>
<div class="callout-container rich-text-color-gray_background">
    <span class="callout-icon">
        <@common.getIcon icon=block.icon imgClass="" page=page/>
    </span>
    <div class="callout-content">
        <#list block.text as textObject>
            <@richText.renderRichText richText=textObject page=page />
        </#list>

        <#if block.hasChildren>
            <#assign blockChildren = getBlockChildren(block)>
            <#list blockChildren as child>
                ${renderBlock(child, page)?no_esc}
            </#list>
        </#if>
    </div>
</div>