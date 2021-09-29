package hu.glorantq.notion.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/*
    https://developers.notion.com/reference/page#all-property-values
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
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
    public static class DatePropertyValue {
        @Expose
        private Date start;

        @Expose
        private Date end;
    }

    @Expose
    private FormulaPropertyValue formula; // TODO: Unknown name, just a guess

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
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

        public enum Type {
            @SerializedName("string") STRING, @SerializedName("number") NUMBER, @SerializedName("boolean") BOOLEAN,
            @SerializedName("date") DATE
        }
    }

    @Expose
    private List<RelationPropertyValue> relation;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class RelationPropertyValue {
        @Expose
        private UUID id;
    }

    // TODO: Rollup

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
