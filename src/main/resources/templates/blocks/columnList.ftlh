<div class="column-list">
    <#if block.hasChildren>
        <#assign childBlocks = getBlockChildren(block)>
        <#list childBlocks as child>
            ${renderBlock(child, page)?no_esc}
        </#list>
    </#if>
</div>
