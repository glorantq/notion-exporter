<#import "./richText.ftlh" as richText>
<pre class="line-numbers"><code class="language-${block.language?string?lower_case}"><#list block.text as text><@richText.renderRichText richText=text page=page /></#list></code></pre>
<#if block.caption?? && block.caption?size gt 0>
    <span class="caption"><#list block.caption as text><@richText.renderRichText richText=text page=page/></#list></span>
</#if>