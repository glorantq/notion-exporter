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
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/*
    https://developers.notion.com/reference/page#all-property-values
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@JsonAdapter(NotionPropertyValueObjectDeserializer.class)
public class NotionPropertyValueObject {
    @Expose
    private String id;

    @Expose
    private Type type;

    @Expose
    private List<NotionRichText> title;

    @SerializedName("rich_text")
    @Expose
    private List<NotionRichText> richText;

    @Expose
    private double number;

    @Expose
    private SelectPropertyValue select;

    @SerializedName("multi_select")
    @Expose
    private List<SelectPropertyValue> multiSelect;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @EqualsAndHashCode
    public static class SelectPropertyValue {
        @Expose
        private UUID id;

        @Expose
        private String name;

        @Expose
        private NotionRichText.Annotations.Color color;
    }

    @Expose
    private DatePropertyValue date;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @EqualsAndHashCode
    public static class DatePropertyValue {
        @Expose
        private Date start;

        @Expose
        private Date end;
    }

    @Expose
    private FormulaPropertyValue formula;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @EqualsAndHashCode
    public static class FormulaPropertyValue {
        @Expose
        private Type type;

        @Expose
        private String string;

        @Expose
        private double number;

        @SerializedName("boolean")
        @Expose
        private boolean booleanValue;

        @Expose
        private DatePropertyValue date;

        @Expose
        private String expression;

        public enum Type {
            @SerializedName("string") STRING, @SerializedName("number") NUMBER, @SerializedName("boolean") BOOLEAN,
            @SerializedName("date") DATE
        }
    }

    // TODO: Relations
    // TODO: Rollup

    @Expose
    private List<NotionUser> people;

    @Expose
    private List<NotionFile> files;

    public enum Type {
        @SerializedName("rich_text") RICH_TEXT, @SerializedName("number") NUMBER, @SerializedName("select") SELECT,
        @SerializedName("multi_select") MULTI_SELECT, @SerializedName("date") DATE, @SerializedName("formula") FORMULA,
        @SerializedName("relation") RELATION, @SerializedName("rollup") ROLLUP, @SerializedName("title") TITLE,
        @SerializedName("people") PEOPLE, @SerializedName("files") FILES, @SerializedName("checkbox") CHECKBOX,
        @SerializedName("url") URL, @SerializedName("email") EMAIL, @SerializedName("phone_number") PHONE_NUMBER,
        @SerializedName("created_time") CREATED_TIME, @SerializedName("created_by") CREATED_BY, @SerializedName("last_edited_time") LAST_EDITED_TIME,
        @SerializedName("last_edited_by") LAST_EDITED_BY
    }
}
