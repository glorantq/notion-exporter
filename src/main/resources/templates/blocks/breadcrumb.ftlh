<#import "../common.ftl" as common>
<div class="breadcrumb-container">
    <#list pageHierarchy as currentPage>
        <@common.getNavbarElement page=currentPage currentPage=page />
        <#if currentPage?has_next>
            <span class="navbar-path-separator">/</span>
        </#if>
    </#list>
</div>