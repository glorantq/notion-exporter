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

package hu.glorantq.notion.api.model.blocks;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import hu.glorantq.notion.api.model.blocks.impl.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

/*
    https://developers.notion.com/reference/block
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonAdapter(NotionBlockDeserializer.class)
public abstract class NotionBlock {
    @Expose
    protected String object;

    @Expose
    protected UUID id;

    @Expose
    protected Type type;

    @RequiredArgsConstructor
    @Getter
    public enum Type {
        @SerializedName("paragraph") PARAGRAPH(NotionParagraphBlock.class), @SerializedName("heading_1") HEADING_1(NotionHeadingOneBlock.class),
        @SerializedName("heading_2") HEADING_2(NotionHeadingTwoBlock.class), @SerializedName("heading_3") HEADING_3(NotionHeadingThreeBlock.class),
        @SerializedName("bulleted_list_item") BULLETED_LIST_ITEM(NotionBulletedListItemBlock.class),
        @SerializedName("numbered_list_item") NUMBERED_LIST_ITEM(NotionNumberedListItemBlock.class),
        @SerializedName("to_do") TO_DO(NotionToDoBlock.class), @SerializedName("toggle") TOGGLE(NotionToggleBlock.class),
        @SerializedName("child_page") CHILD_PAGE(NotionChildPageBlock.class), @SerializedName("child_database") CHILD_DATABASE(NotionChildDatabaseBlock.class),
        @SerializedName("embed") EMBED(NotionEmbedBlock.class), @SerializedName("image") IMAGE(NotionImageBlock.class),
        @SerializedName("video") VIDEO(NotionVideoBlock.class), @SerializedName("file") FILE(NotionFileBlock.class),
        @SerializedName("pdf") PDF(NotionPDFBlock.class), @SerializedName("bookmark") BOOKMARK(NotionBookmarkBlock.class),
        @SerializedName("code") CODE(NotionCodeBlock.class), @SerializedName("callout") CALLOUT(NotionCalloutBlock.class),
        @SerializedName("equation") EQUATION(NotionEquationBlock.class), @SerializedName("quote") QUOTE(NotionQuoteBlock.class),
        @SerializedName("divider") DIVIDER(NotionDividerBlock.class), @SerializedName("table_of_contents") TABLE_OF_CONTENTS(NotionNotImplementedBlock.class),
        @SerializedName("breadcrumb") BREADCRUMB(NotionBreadcrumbBlock.class), @SerializedName("column_list") COLUMN_LIST(NotionColumnListBlock.class),
        @SerializedName("column") COLUMN (NotionColumnBlock.class),
        @SerializedName("unsupported") UNSUPPORTED(NotionUnsupportedBlock.class);

        private final Class<? extends NotionBlock> typeClass;
    }

    @SerializedName("created_time")
    @Expose
    protected Date createdTime;

    @SerializedName("last_edited_time")
    @Expose
    protected Date lastEditedTime;

    @Expose
    protected boolean archived;

    @SerializedName("has_children")
    @Expose
    protected boolean hasChildren;

    public abstract NotionBlock deserialize(JsonObject jsonObject, JsonDeserializationContext context) throws JsonParseException;
}

/*
    Template for IntelliJ
    ----------------------

    #if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end
    #parse("File Header.java")

    import com.google.gson.JsonDeserializationContext;
    import com.google.gson.JsonObject;
    import com.google.gson.JsonParseException;
    import hu.glorantq.notion.api.model.blocks.NotionBlock;
    import lombok.Getter;
    import lombok.ToString;

    import java.util.Date;
    import java.util.UUID;

    @ToString(callSuper = true)
    @Getter
    public class ${NAME} extends NotionBlock {
        public ${NAME}(String object, UUID id, Type type, Date createdTime, Date lastEditedTime, boolean archived, boolean hasChildren) {
            super(object, id, type, createdTime, lastEditedTime, archived, hasChildren);
        }

        @Override
        public NotionBlock deserialize(JsonObject jsonObject, JsonDeserializationContext context) throws JsonParseException {
            return this;
        }
    }
 */