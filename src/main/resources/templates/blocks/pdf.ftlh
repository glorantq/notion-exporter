<#import "richText.ftlh" as richText>
<#import "../common.ftl" as common>
<div class="pdf-container">
    <#if block.fileObject??>
        <object type="application/pdf" data="<@common.getUrlFromFile file=block.fileObject page=page />?#zoom=85&scrollbar=0&toolbar=0&navpanes=0&view=FitH">
            <p>Your browser can not render PDF files. Click <a href="<@common.getUrlFromFile file=block.fileObject page=page />">here</a> to download it.</p>
        </object>
    </#if>
    <#if block.caption?? && block.caption?size gt 0>
        <span class="caption"><#list block.caption as text><@richText.renderRichText richText=text page=page/></#list></span>
    </#if>
</div>