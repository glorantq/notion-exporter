<#import "richText.ftlh" as richText>
<div class="list-item">
    <span class="bullet-list-marker"></span>
    <span class="list-content"><#list block.text as text><@richText.renderRichText richText=text page=page /></#list></span>
</div>
<#if block.hasChildren>
    <div class="list-children">
        <#assign childBlocks = getBlockChildren(block)>
        <#list childBlocks as child>
            ${renderBlock(child, page)?no_esc}
        </#list>
    </div>
</#if>