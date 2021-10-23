<#import "../common.ftl" as common>
<#import "richText.ftlh" as richText>
<div class="image-container">
    <img class="image-block" src="<@common.getUrlFromFile file=block.fileObject page=page />" alt="Image">
    <#if block.caption?? && block.caption?size gt 0>
        <span class="caption"><#list block.caption as text><@richText.renderRichText richText=text page=page/></#list></span>
    </#if>
</div>