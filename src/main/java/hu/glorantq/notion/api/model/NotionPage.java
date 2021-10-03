package hu.glorantq.notion.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/*
    https://developers.notion.com/reference/page
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotionPage {
    @SerializedName("object")
    @Expose
    private String objectType;

    @SerializedName("id")
    @Expose
    private UUID pageId;

    @SerializedName("created_time")
    @Expose
    private Date createdTime;

    @SerializedName("last_edited_time")
    @Expose
    private Date lastEditedTime;

    @Expose
    private boolean archived;

    @Expose
    private NotionIcon icon;

    @Expose
    private NotionFile cover;

    @Expose
    private Map<String, NotionPropertyValueObject> properties;

    @Expose
    private NotionParent parent;

    @Expose
    private String url;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotionPage that = (NotionPage) o;
        return Objects.equals(pageId, that.pageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageId);
    }

    public String getTitle() {
        return properties.get("title").getTitle().get(0).getPlainText();
    }
}
