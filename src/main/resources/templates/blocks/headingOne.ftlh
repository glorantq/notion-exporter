<#import "richText.ftlh" as richText>
<h1><#list block.text as text><@richText.renderRichText richText=text /></#list></h1>