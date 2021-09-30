<#macro renderRichText richText>
    <#assign style = richText.annotations>

    <span class="block-rich-text <#if style.bold>rich-text-bold</#if> <#if style.italic>rich-text-italic</#if> <#if style.strikethrough>rich-text-strikethrough</#if> <#if style.underline>rich-text-underline</#if> <#if style.code>rich-text-code</#if> <#if style.color??>rich-text-color-${style.color.name()?lower_case}</#if>">
        <#if richText.type.name() == "TEXT">
            <#if richText.text.link??>
                <a href="${richText.text.link.url}" target="_blank">
            </#if>
            ${richText.text.content}
            <#if richText.text.link??>
                </a>
            </#if>
        </#if>

        <#if richText.type.name() == "EQUATION">
            <span class="katex-math">${richText.equation.expression}</span>
        </#if>
    </span>

    <#-- TODO: Implement mentions -->
</#macro>