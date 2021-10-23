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
