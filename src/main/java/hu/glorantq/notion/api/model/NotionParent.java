package hu.glorantq.notion.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/*
    https://developers.notion.com/reference/page#database-parent
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotionParent {
    @SerializedName("type")
    @Expose
    private String parentType = null;

    @SerializedName("database_id")
    @Expose
    private String databaseId = null;

    @SerializedName("page_id")
    @Expose
    private String pageId = null;

    @Expose
    private boolean workspace = false;
}
