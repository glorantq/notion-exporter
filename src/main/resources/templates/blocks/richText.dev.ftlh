<#--
    Copyright (C) 2021 Gerber Lóránt Viktor

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
-->

<#import "../common.ftl" as common><#macro renderRichText richText page><#assign style = richText.annotations><span
    class="block-rich-text <#if style.bold>rich-text-bold</#if> <#if style.italic>rich-text-italic</#if> <#if style.strikethrough>rich-text-strikethrough</#if> <#if style.underline>rich-text-underline</#if> <#if style.code>rich-text-code</#if> <#if style.color??>rich-text-color-${style.color.name()?lower_case}</#if>"><#if richText.type.name() == "TEXT"><#if richText.text.link??><a href="${richText.text.link.url}" target="_blank"></#if>${richText.text.content}<#if richText.text.link??></a></#if></#if><#if richText.type.name() == "EQUATION"><#assign equation = richText.equation.expression>
        <span class="katex-math"
              data-equation="${equation}">${equation}</span></#if><#if richText.type.name() == "MENTION"><#assign mention = richText.mention><#if mention.type.name() == "PAGE"><#assign pageId = mention.page.id><#assign linkedPage = fetchPage(pageId)>
        <a class="block-link external-link"
           href="${resolvePageLink(pageId, page)}"><#if linkedPage.icon??><@common.getIcon icon=linkedPage.icon imgClass="block-link-icon" page=page /><#else><i
            data-feather="file-text"></i></#if><i
                data-feather="external-link"></i><span><@common.getPageTitle page=linkedPage /></span></a></#if></#if>
    </span></#macro>