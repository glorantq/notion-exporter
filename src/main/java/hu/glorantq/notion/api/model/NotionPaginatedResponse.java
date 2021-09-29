package hu.glorantq.notion.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/*
    https://developers.notion.com/reference/get-block-children
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotionPaginatedResponse<T> {
    @SerializedName("has_more")
    @Expose
    private boolean hasMore;

    @SerializedName("next_cursor")
    @Expose
    private String nextCursor;

    @Expose
    private String object;

    @Expose
    private List<T> results;
}
