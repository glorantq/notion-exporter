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
            <@common.getIcon icon=page.icon />
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
        <span>[Possibly dark mode?]</span>
    </div>
</div>

<script>
    twemoji.parse(document.body);

    let mathElements = document.body.getElementsByClassName("katex-math");
    for(let math of mathElements) {
        katex.render(math.innerHTML.replaceAll("&lt;", "<").replaceAll("&gt;", ">"), math, { throwOnError: false });
    }

    let liCounter = 0;
    let allElements = document.body.getElementsByClassName("block");
    for (let element of allElements) {
        let type = element.getAttribute("data-type");
        if (type === "NotionNumberedListItemBlock") {
            element.getElementsByClassName("numbered-list-marker")[0].setAttribute("data-before", `${r"${++liCounter}"}.`);
        } else {
            liCounter = 0;
        }
    }

    let loaderElements = document.body.getElementsByClassName("loader-container");
    for (let loader of loaderElements) {
        loader.classList.toggle("loader-hidden");
    }
</script>
</body>

</html>