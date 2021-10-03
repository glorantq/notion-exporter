<#macro getPageTitle page>
    ${page.properties.title.title[0].plainText}
</#macro>

<#macro getUrlFromFile file page>
    <#if file.fileType.name() == "EXTERNAL">
        ${resolveAssetLink(file.externalData.externalUrl, page)}
    <#else>
        ${resolveAssetLink(file.hostedData.hostedUrl, page)}
    </#if>
</#macro>

<#macro getIcon icon imgClass page>
    <#if icon.type.name() == "EMOJI">
        <span>${icon.emojiCharacter}</span>
    <#else>
        <#assign iconFile = icon.toFileObject()>
        <img src="<@getUrlFromFile file=iconFile page=page />" alt="Icon" class="${imgClass}"/>
    </#if>
</#macro>

<#macro getNavbarElement page currentPage>
    <a href="${resolvePageLink(page.pageId!"error-404", currentPage)}">
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