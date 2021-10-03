package hu.glorantq.notion.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

/*
    https://developers.notion.com/reference/rich-text
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotionRichText {
    @SerializedName("plain_text")
    @Expose
    private String plainText;

    @SerializedName("href")
    @Expose
    private String linkLocation;

    @Expose
    private Type type;

    @Expose
    private Annotations annotations;

    // Type-specific

    @Expose
    private TextObject text;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class TextObject {
        @Expose
        private String content;

        @Expose
        private Link link;

        @Getter
        @ToString
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Link {
            @Expose
            private String type;

            @Expose
            private String url;
        }
    }

    @Expose
    private MentionObject mention;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class MentionObject {
        @Expose
        private Type type;

        @Expose
        private Map<String, Object> user;

        @Expose
        private Map<String, Object> page;

        @Expose
        private Map<String, Object> database;

        public enum Type {
            @SerializedName("user") USER, @SerializedName("page") PAGE, @SerializedName("database") DATABASE, @SerializedName("date") DATE
        }
    }

    @Expose
    private EquationObject equation;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class EquationObject {
        @Expose
        private String expression;
    }

    public enum Type {
        @SerializedName("text") TEXT, @SerializedName("mention") MENTION, @SerializedName("equation") EQUATION
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Annotations {
        @Expose
        private boolean bold;

        @Expose
        private boolean italic;

        @Expose
        private boolean strikethrough;

        @Expose
        private boolean underline;

        @Expose
        private boolean code;

        @Expose
        private Color color;

        public enum Color {
            @SerializedName("default") DEFAULT, @SerializedName("gray") GRAY, @SerializedName("brown") BROWN,
            @SerializedName("orange") ORANGE, @SerializedName("yellow") YELLOW, @SerializedName("green") GREEN,
            @SerializedName("blue") BLUE, @SerializedName("purple") PURPLE, @SerializedName("pink") PINK,
            @SerializedName("red") RED, @SerializedName("gray_background") GRAY_BACKGROUND, @SerializedName("brown_background") BROWN_BACKGROUND,
            @SerializedName("orange_background") ORANGE_BACKGROUND, @SerializedName("yellow_background") YELLOW_BACKGROUND,
            @SerializedName("green_background") GREEN_BACKGROUND, @SerializedName("blue_background") BLUE_BACKGROUND,
            @SerializedName("purple_background") PURPLE_BACKGROUND, @SerializedName("pink_background") PINK_BACKGROUND,
            @SerializedName("red_background") RED_BACKGROUND
        }
    }
}
