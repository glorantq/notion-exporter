package hu.glorantq.notion.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.Date;

/*
    https://developers.notion.com/reference/file-object
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotionFile {
    @SerializedName("type")
    @Expose
    private Type fileType;

    @SerializedName("external")
    @Expose
    private External externalData = null;

    @SerializedName("file")
    @Expose
    private File hostedData = null;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class External {
        @SerializedName("url")
        @Expose
        private String externalUrl;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class File {
        @SerializedName("url")
        @Expose
        private String hostedUrl;

        @SerializedName("expiry_time")
        @Expose
        private Date expiryTime;
    }

    public enum Type {
        @SerializedName("external") EXTERNAL, @SerializedName("file") HOSTED
    }
}
