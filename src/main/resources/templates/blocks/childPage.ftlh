<#import "../common.ftl" as common>
<#assign linkedPage = fetchPage(block.id)>
<a class="block-link" href="${resolvePageLink(block.id, page)}"><#if linkedPage.icon??><@common.getIcon icon=linkedPage.icon imgClass="block-link-icon" page=page /><#else><i data-feather="file-text"></i></#if><span class="content">${block.title}</span></a>
