package hu.glorantq.notion.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.Map;
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
}
