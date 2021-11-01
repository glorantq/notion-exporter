<div class="column">
    <#if block.hasChildren>
        <#assign childBlocks = getBlockChildren(block)>
        <#list childBlocks as child>
            <div class="column-item">
                ${renderBlock(child, page)?no_esc}
            </div>
        </#list>
    </#if>
</div>