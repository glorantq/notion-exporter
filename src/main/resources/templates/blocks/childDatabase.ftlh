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

<#import "../common.ftl" as common>
<#import "../database/database.ftlh" as database>

<h3><a href="${resolvePageLink(block.id, page)}">${block.title}</a><span class="maximize-button" onclick="window.location.href='${resolvePageLink(block.id, page)}';"><i data-feather="maximize-2"></i></span></h3>

<#assign linkedDatabase = fetchDatabase(block.id)>
<@database.renderDatabase page=linkedDatabase containingPage=page />

