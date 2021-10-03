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
<div id="share-modal-container"  class="share-modal-invisible">
    <label>Share this page</label>
    <div id="share-modal-text-container">
        <input type="text" id="share-modal-url" value=":(" readonly>
        <button id="share-modal-copy">Copy</button>
    </div>
    <hr>
    <div id="share-modal-buttons">
        <a href="#" class="share-button" id="share-email" target="_blank"><i data-feather="mail"></i></a>
        <a href="#" class="share-button" id="share-facebook" target="_blank"><i data-feather="facebook"></i></a>
        <a href="#" class="share-button" id="share-twitter" target="_blank"><i data-feather="twitter"></i></a>
        <a href="#" class="share-button" id="share-reddit" target="_blank"><svg class="feather" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><path d="M12 2c5.514 0 10 4.486 10 10s-4.486 10-10 10-10-4.486-10-10 4.486-10 10-10zm0-2c-6.627 0-12 5.373-12 12s5.373 12 12 12 12-5.373 12-12-5.373-12-12-12zm6 11.889c0-.729-.596-1.323-1.329-1.323-.358 0-.681.143-.92.373-.905-.595-2.13-.975-3.485-1.023l.742-2.334 2.008.471-.003.029c0 .596.487 1.082 1.087 1.082.599 0 1.086-.485 1.086-1.082s-.488-1.082-1.087-1.082c-.46 0-.852.287-1.01.69l-2.164-.507c-.094-.023-.191.032-.22.124l-.827 2.603c-1.419.017-2.705.399-3.65 1.012-.237-.219-.552-.356-.9-.356-.732.001-1.328.594-1.328 1.323 0 .485.267.905.659 1.136-.026.141-.043.283-.043.429-.001 1.955 2.404 3.546 5.359 3.546 2.956 0 5.36-1.591 5.36-3.546 0-.137-.015-.272-.038-.405.416-.224.703-.657.703-1.16zm-8.612.908c0-.434.355-.788.791-.788.436 0 .79.353.79.788 0 .434-.355.787-.79.787-.436.001-.791-.352-.791-.787zm4.53 2.335c-.398.396-1.024.589-1.912.589l-.007-.001-.007.001c-.888 0-1.514-.193-1.912-.589-.073-.072-.073-.19 0-.262.072-.072.191-.072.263 0 .325.323.864.481 1.649.481l.007.001.007-.001c.784 0 1.324-.157 1.649-.481.073-.072.19-.072.263 0 .073.072.073.19 0 .262zm-.094-1.547c-.436 0-.79-.353-.79-.787 0-.434.355-.788.79-.788.436 0 .79.353.79.788 0 .434-.354.787-.79.787z"/></svg></a>
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
        <span>© ${pageAuthor}, ${.now?date?string.yyyy}</span>
    </div>

    <div class="navbar-content navbar-right">
         <span><button id="theme-toggle" class="navbar-button"></button></span>
    </div>
</div>

<script src="${resolveAssetLink("bundled:///assets/lib/prism.js", page)}"></script>
<script>
    Prism.plugins.autoloader.languages_path = "${resolveAssetLink("bundled:///assets/lib/prism-grammars/", page)}/";
</script>
<script defer src="${resolveAssetLink("bundled:///assets/page.js", page)}"></script>
</body>

</html>