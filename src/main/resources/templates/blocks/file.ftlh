<#import "../common.ftl" as common>
<#import "richText.ftlh" as richText>
<a class="block-link" href="<@common.getUrlFromFile file=block.fileObject page=page />" target="_blank" download="${block.getTitle()}"><i data-feather="link"></i><span>${block.getTitle()}</span></a>
<#if block.caption?? && block.caption?size gt 0>
    <span class="caption"><#list block.caption as text><@richText.renderRichText richText=text page=page/></#list></span>
</#if>