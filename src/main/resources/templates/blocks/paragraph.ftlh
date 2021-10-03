<#import "richText.ftlh" as richText>
<p>
    <#list block.text as text>
        <@richText.renderRichText richText=text page=page />
    </#list>

    <#-- TODO: Implement children -->
</p>