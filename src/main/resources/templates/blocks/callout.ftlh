<#import "./richText.ftlh" as richText>
<#import "../common.ftl" as common>
<div class="callout-container rich-text-color-gray_background">
    <span class="callout-icon">
        <@common.getIcon icon=block.icon imgClass="" page=page/>
    </span>
    <div class="callout-content">
        <#list block.text as textObject>
            <@richText.renderRichText richText=textObject page=page />
        </#list>

        <#if block.hasChildren>
            <#assign blockChildren = getBlockChildren(block)>
            <#list blockChildren as child>
                ${renderBlock(child, page)?no_esc}
            </#list>
        </#if>
    </div>
</div>