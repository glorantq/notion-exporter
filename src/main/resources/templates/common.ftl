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

<#macro getPageTitle page>${page.getTitle()}</#macro>

<#macro getUrlFromFile file page><#if file.fileType.name() == "EXTERNAL">${resolveAssetLink(file.externalData.externalUrl, page)}<#else>${resolveAssetLink(file.hostedData.hostedUrl, page)}</#if></#macro>

<#macro getIcon icon imgClass page>
    <#if icon??>
        <#if icon.type.name() == "EMOJI">
            <span>${icon.emojiCharacter}</span>
        <#else>
            <#assign iconFile = icon.toFileObject()>
            <img src="<@getUrlFromFile file=iconFile page=page />" alt="Icon" class="${imgClass}"/>
        </#if>
    </#if>
</#macro>

<#macro getNavbarElement page currentPage>
    <a href="${resolvePageLink(page.pageId!"error-404", currentPage)}" class="navbar-element-link">
        <#if page.icon??>
            <div class="navbar-icon">
                <@getIcon icon=page.icon imgClass="" page=currentPage />
            </div>
        </#if>
        <span class="navbar-title"><@getPageTitle page=page /></span>
    </a>
</#macro>

<#macro renderBlockEx block page>
    <div class="block" id="${block.id}" data-type="${block.class.simpleName}">
${renderBlock(block, page)}
    </div>
</#macro>