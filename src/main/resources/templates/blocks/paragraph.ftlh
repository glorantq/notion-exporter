<#import "richText.ftlh" as richText>
<p>
    <#list block.text as text>
        <@richText.renderRichText richText=text />
    </#list>

    <#-- TODO: Implement children -->
</p>