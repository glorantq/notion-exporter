<#import "richText.ftlh" as richText>
<h2><#list block.text as text><@richText.renderRichText richText=text page=page /></#list></h2>