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
    private List<Map<String, Object>> sorts;
}
