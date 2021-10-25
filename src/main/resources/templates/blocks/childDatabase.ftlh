<#import "../common.ftl" as common>
<#import "../database/database.ftlh" as database>

<h3><a href="${resolvePageLink(block.id, page)}">${block.title}</a><span class="maximize-button" onclick="window.location.href='${resolvePageLink(block.id, page)}';"><i data-feather="maximize-2"></i></span></h3> <#-- TODO: Move external link button to the right, change to fullscreen icon -->

<#assign linkedDatabase = fetchDatabase(block.id)>
<@database.renderDatabase page=linkedDatabase containingPage=page />

