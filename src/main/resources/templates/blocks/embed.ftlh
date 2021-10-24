<#import "richText.ftlh" as richText>
<div class="embed-container">
    <iframe class="embed" src="${block.url}" loading="lazy"></iframe>
    <#if block.caption?? && block.caption?size gt 0>
        <span class="caption"><#list block.caption as text><@richText.renderRichText richText=text page=page/></#list></span>
    </#if>
</div>