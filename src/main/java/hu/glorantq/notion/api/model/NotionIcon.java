package hu.glorantq.notion.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import hu.glorantq.notion.api.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotionIcon {
    @Expose
    private String type;

    @SerializedName("external")
    @Expose
    private NotionFile.External externalFileData;

    @SerializedName("file")
    @Expose
    private NotionFile.File hostedFileData;

    @SerializedName("emoji")
    @Expose
    private String emojiCharacter = null;

    public NotionFile toFileObject() {
        if(isEmoji()) {
            throw new RuntimeException("This icon object contains a file!");
        }

        return new NotionFile(type, externalFileData, hostedFileData);
    }

    public boolean isEmoji() {
        return type.equalsIgnoreCase(StringConstants.TYPE_EMOJI);
    }
}
