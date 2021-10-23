<#import "../common.ftl" as common><#macro renderRichText richText page><#assign style = richText.annotations><span
    class="block-rich-text <#if style.bold>rich-text-bold</#if> <#if style.italic>rich-text-italic</#if> <#if style.strikethrough>rich-text-strikethrough</#if> <#if style.underline>rich-text-underline</#if> <#if style.code>rich-text-code</#if> <#if style.color??>rich-text-color-${style.color.name()?lower_case}</#if>"><#if richText.type.name() == "TEXT"><#if richText.text.link??><a href="${richText.text.link.url}" target="_blank"></#if>${richText.text.content}<#if richText.text.link??></a></#if></#if><#if richText.type.name() == "EQUATION"><#assign equation = richText.equation.expression>
        <span class="katex-math"
              data-equation="${equation}">${equation}</span></#if><#if richText.type.name() == "MENTION"><#assign mention = richText.mention><#if mention.type.name() == "PAGE"><#assign pageId = mention.page.id><#assign linkedPage = fetchPage(pageId)>
        <a class="block-link external-link"
           href="${resolvePageLink(pageId, page)}"><#if linkedPage.icon??><@common.getIcon icon=linkedPage.icon imgClass="block-link-icon" page=page /><#else><i
            data-feather="file-text"></i></#if><i data-feather="external-link"></i><span
                class="content"><@common.getPageTitle page=linkedPage /></span></a></#if></#if></span></#macro>