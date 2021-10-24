<#import "../common.ftl" as common>
<#import "../database/database.ftlh" as database>

<h3><a href="${resolvePageLink(block.id, page)}">${block.title}</a><i data-feather="external-link"></i></h3> <#-- TODO: Move external link button to the right, change to fullscreen icon -->

<#assign linkedDatabase = fetchDatabase(block.id)>
<@database.renderDatabase page=linkedDatabase containingPage=page />

