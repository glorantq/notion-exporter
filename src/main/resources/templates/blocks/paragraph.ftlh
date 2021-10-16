<#import "richText.ftlh" as richText>
<p>
    <#list block.text as text>
        <@richText.renderRichText richText=text page=page />
    </#list>
</p>

<#if block.hasChildren>
    <#assign childBlocks = getBlockChildren(block)>
    <#list childBlocks as child>
        ${renderBlock(child, page)?no_esc}
    </#list>
</#if>