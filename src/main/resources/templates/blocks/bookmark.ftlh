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
<div class="bookmark-container" onclick="window.open('${block.url}', '_blank').focus();">
    <#assign bookmark = resolveBookmark(block.url)>

    <span class="bookmark-title">${bookmark.title}</span>
    <div class="bookmark-details">
        <#if bookmark.iconUrl??>
            <img src="${resolveAssetLink(bookmark.iconUrl, page)}" class="bookmark-icon">
        </#if>
        <span class="bookmark-url">${block.url}</span>
    </div>
</div>
<#if block.caption?? && block.caption?size gt 0>
    <span class="caption"><#list block.caption as text><@richText.renderRichText richText=text page=page/></#list></span>
</#if>