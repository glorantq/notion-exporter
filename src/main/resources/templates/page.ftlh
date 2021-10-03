<#import "./common.ftl" as common>
<!DOCTYPE html>
<html lang="hu">

<head>
    <title>${pageName} - <@common.getPageTitle page=page /></title>

    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="author" content="${pageAuthor}" />

    <link rel="stylesheet" href="${resolveAssetLink("bundled:///assets/page.css", page)}">
    <link rel="stylesheet" href="${resolveAssetLink("bundled:///assets/lib/katex.min.css", page)}">
    <link rel="stylesheet" id="prism-stylesheet" href="">

    <#-- TODO: Mirror -->
    <script src="${resolveAssetLink("bundled:///assets/lib/twemoji.min.js", page)}"></script>
    <script src="${resolveAssetLink("bundled:///assets/lib/katex.min.js", page)}"></script>
    <script src="${resolveAssetLink("bundled:///assets/lib/feather.min.js", page)}"></script>

    <script>
        let prismStylesheets = {
            light: "${resolveAssetLink("bundled:///assets/lib/prism.css", page)}",
            dark: "${resolveAssetLink("bundled:///assets/lib/prism-dark.css", page)}"
        }
    </script>
</head>

<body>
<div class="loader-container">
    <div class="cssload-container">
        <div class="cssload-speeding-wheel"></div>
    </div>
</div>
<div class="navbar-container">
    <div class="navbar-content navbar-left">
        <#list pageHierarchy as currentPage>
            <@common.getNavbarElement page=currentPage currentPage=page />
            <#if currentPage?has_next>
                <span class="navbar-path-separator">/</span>
            </#if>
        </#list>
    </div>
    <div class="navbar-content navbar-right">
        <span><button id="share-button" class="navbar-button"><i data-feather="share"></i></button></span>
    </div>
</div>
<div class="page-header">
    <#if page.cover??>
        <img class="header-cover" src="<@common.getUrlFromFile file=page.cover page=page />" alt="Cover" />
    <#else>
        <div class="header-cover no-cover-image"></div>
    </#if>
    <#if page.icon??>
        <div class="header-icon">
            <@common.getIcon icon=page.icon imgClass="" page=page />
        </div>
    </#if>
</div>
<div class="page-content">
    <h1 class="page-title"><@common.getPageTitle page=page /></h1>
    <#assign blockChildren = getBlockChildren(page)>
    <#list blockChildren as block>
<@common.renderBlockEx block=block page=page />
    </#list>
</div>
<div id="image-popup-container" class="image-popup-invisible">
    <img id="image-popup" src="">
    <span id="image-popup-close">Close</span>
</div>
<div class="navbar-container footer">
    <div class="navbar-content navbar-left">
        <span>© ${pageAuthor} ${.now?date?string.yyyy}</span>
    </div>

    <div class="navbar-content navbar-right">
         <span><button id="theme-toggle" class="navbar-button"></button></span>
    </div>
</div>

<script src="${resolveAssetLink("bundled:///assets/lib/prism.js", page)}"></script>
<script defer src="${resolveAssetLink("bundled:///assets/page0.js", page)}"></script>
</body>

</html>