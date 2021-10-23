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
