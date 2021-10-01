<#import "./common.ftl" as common>
<!DOCTYPE html>
<html lang="hu">

<head>
    <title>${pageName} - <@common.getPageTitle page=page /></title>

    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="author" content="${pageAuthor}" />

    <link rel="stylesheet" href="./assets/default.css" />

    <script src="https://twemoji.maxcdn.com/v/latest/twemoji.min.js" crossorigin="anonymous"></script>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/katex@0.13.18/dist/katex.min.css" integrity="sha384-zTROYFVGOfTw7JV7KUu8udsvW2fx4lWOsCEDqhBreBwlHI4ioVRtmIvEThzJHGET" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/katex@0.13.18/dist/katex.min.js" integrity="sha384-GxNFqL3r9uRJQhR+47eDxuPoNE7yLftQM8LcxzgS4HT73tp970WS/wV5p8UzCOmb" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/feather-icons/dist/feather.min.js"></script>
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
            <@common.getNavbarElement page=currentPage />
            <#if currentPage?has_next>
                <span class="navbar-path-separator">/</span>
            </#if>
        </#list>
    </div>
    <div class="navbar-content navbar-right">
        <span>[Unknown]</span>
    </div>
</div>
<div class="page-header">
    <#if page.cover??>
        <img class="header-cover" src="<@common.getUrlFromFile file=page.cover />" alt="Cover" />
    <#else>
        <div class="header-cover no-cover-image"></div>
    </#if>
    <#if page.icon??>
        <div class="header-icon">
            <@common.getIcon icon=page.icon imgClass="" />
        </div>
    </#if>
</div>
<div class="page-content">
    <h1 class="page-title"><@common.getPageTitle page=page /></h1>
    <#assign blockChildren = getBlockChildren(page)>
    <#list blockChildren as block>
        <@common.renderBlockEx block=block />
    </#list>
</div>
<div class="navbar-container footer">
    <div class="navbar-content navbar-left">
        <span>© ${pageAuthor} ${.now?date?string.yyyy}</span>
    </div>

    <div class="navbar-content navbar-right">
         <span><button id="theme-toggle"></button></span>
    </div>
</div>

<script defer src="./assets/page.js"></script>
</body>

</html>