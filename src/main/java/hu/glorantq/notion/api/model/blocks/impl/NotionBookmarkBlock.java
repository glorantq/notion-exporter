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

package hu.glorantq.notion.api.model.blocks.impl;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import hu.glorantq.notion.api.model.NotionRichText;
import hu.glorantq.notion.api.model.blocks.NotionBlock;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@ToString(callSuper = true)
@Getter
public class NotionBookmarkBlock extends NotionBlock {
    @Expose
    private String url;

    @Expose
    private List<NotionRichText> caption;

    public NotionBookmarkBlock(String object, UUID id, Type type, Date createdTime, Date lastEditedTime, boolean archived, boolean hasChildren) {
        super(object, id, type, createdTime, lastEditedTime, archived, hasChildren);
    }

    @Override
    public NotionBlock deserialize(JsonObject jsonObject, JsonDeserializationContext context) throws JsonParseException {
        url = context.deserialize(jsonObject.get("url"), String.class);
        caption = context.deserialize(jsonObject.get("caption"), TypeToken.getParameterized(List.class, NotionRichText.class).getType());

        return this;
    }

    @Data
    public static class BookmarkDetails {
        private final String title;
        private final String iconUrl;
    }
}
