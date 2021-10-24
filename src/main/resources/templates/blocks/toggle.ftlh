<#import "richText.ftlh" as richText>
<details>
    <summary><#list block.text as textObject><@richText.renderRichText richText=textObject page=page/></#list></summary>
    <#assign childBlocks = getBlockChildren(block)>
    <#list childBlocks as child>
        ${renderBlock(child, page)?no_esc}
    </#list>
</details>