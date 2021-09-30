<#import "richText.ftlh" as richText>
<h3><#list block.text as text><@richText.renderRichText richText=text /></#list></h3>