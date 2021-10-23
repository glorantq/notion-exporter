<#import "../common.ftl" as common>
<#import "../database/database.ftlh" as database>

<h3>${block.title}</h3>

<#assign linkedDatabase = fetchDatabase(block.id)>
<@database.renderDatabase page=linkedDatabase containingPage=page />

