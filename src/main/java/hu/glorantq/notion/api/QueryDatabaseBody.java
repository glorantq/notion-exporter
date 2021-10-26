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

package hu.glorantq.notion.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class QueryDatabaseBody {
    @Expose
    @SerializedName("start_cursor")
    private String startCursor;

    @Expose
    @SerializedName("page_size")
    private int pageSize;

    @Expose
    private Map<String, Object> filter;

    @Expose
    private NotionSortObject[] sorts;

    @ToString
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NotionSortObject {
        @Expose
        private String property;

        @Expose
        private Timestamp timestamp;

        @Expose
        private Direction direction;

        public enum Direction {
            @SerializedName("ascending") ASCENDING, @SerializedName("descending") DESCENDING
        }

        public enum Timestamp {
            @SerializedName("created_time") CREATED_TIME, @SerializedName("last_edited_time") LAST_EDITED_TIME
        }
    }
}
