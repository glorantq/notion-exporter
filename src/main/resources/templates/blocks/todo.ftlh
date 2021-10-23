<#import "richText.ftlh" as richText>
<label for="${block.id}" class="todo-label"><input class="todo-checkbox" type="checkbox" id="${block.id}" <#if block.checked>checked</#if> disabled><#list block.text as textObject><@richText.renderRichText richText=textObject page=page/></#list></label><br>
<#if block.hasChildren>
    <div class="list-children">
        <#assign childBlocks = getBlockChildren(block)>
        <#list childBlocks as child>
            ${renderBlock(child, page)?no_esc}
        </#list>
    </div>
</#if>