/*
 *      Copyright (C) 2021 Gerber Lóránt Viktor
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package hu.glorantq.notion;

import com.google.gson.annotations.SerializedName;
import hu.glorantq.notion.api.QueryDatabaseBody;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
@Getter
public class DatabaseConfiguration {
    private final QueryDatabaseBody.NotionSortObject[] sorts;
    private final String filterJson;
    private final String[] columnsToRender;
    private final Display display;
    private final String groupBy;

    public enum Display {
        @SerializedName("table") TABLE, @SerializedName("board") BOARD
    }
}
