<#import "richText.ftlh" as richText>
<div class="quote">
    <span>
        <#list block.text as text>
            <@richText.renderRichText richText=text page=page />
        </#list>
    </span>
</div>