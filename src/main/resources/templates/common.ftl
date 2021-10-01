<#macro getPageTitle page>
    ${page.properties.title.title[0].plainText}
</#macro>

<#macro getUrlFromFile file>
    <#if file.fileType.name() == "EXTERNAL">
        ${file.externalData.externalUrl}
    <#else>
        ${file.hostedData.hostedUrl}
    </#if>
</#macro>

<#macro getIcon icon imgClass>
    <#if icon.type.name() == "EMOJI">
        <span>${icon.emojiCharacter}</span>
    <#else>
        <#assign iconFile = icon.toFileObject()>
        <img src="<@getUrlFromFile file=iconFile />" alt="Icon" class="${imgClass}"/>
    </#if>
</#macro>

<#macro getNavbarElement page>
    <a href="#">
        <#if page.icon??>
            <div class="navbar-icon">
                <@getIcon icon=page.icon imgClass="" />
            </div>
        </#if>
        <span class="navbar-title"><@getPageTitle page=page /></span>
    </a>
    <#-- TODO: Implement links -->
</#macro>

<#macro renderBlockEx block>
    <div class="block" id="${block.id}" data-type="${block.class.simpleName}">
        ${renderBlock(block)}
    </div>
</#macro>