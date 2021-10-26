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

package hu.glorantq.notion.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.*;

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
    private List<NotionRichText> title;

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
        if(objectType.equalsIgnoreCase("page")) {
            if(parent.getParentType().equalsIgnoreCase("database_id")) {
                for(Map.Entry<String, NotionPropertyValueObject> entry : properties.entrySet()) {
                    if(entry.getValue().getType() == NotionPropertyValueObject.Type.TITLE) {
                        return entry.getValue().getTitle().get(0).getPlainText();
                    }
                }

                return "";
            }

            return properties.get("title").getTitle().get(0).getPlainText();
        } else {
            return title.get(0).getPlainText();
        }
    }
}
