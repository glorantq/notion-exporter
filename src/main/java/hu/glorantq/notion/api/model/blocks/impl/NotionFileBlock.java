package hu.glorantq.notion.api.model.blocks.impl;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import hu.glorantq.notion.api.model.NotionFile;
import hu.glorantq.notion.api.model.blocks.NotionBlock;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.io.FilenameUtils;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

@ToString(callSuper = true)
@Getter
public class NotionFileBlock extends NotionBlock {
    @Expose
    private NotionFile fileObject;

    public NotionFileBlock(String object, UUID id, Type type, Date createdTime, Date lastEditedTime, boolean archived, boolean hasChildren) {
        super(object, id, type, createdTime, lastEditedTime, archived, hasChildren);
    }

    public String getTitle() {
        String url;
        if(fileObject.getFileType() == NotionFile.Type.EXTERNAL) {
            url = fileObject.getExternalData().getExternalUrl();
        } else {
            url = fileObject.getHostedData().getHostedUrl();
        }

        try {
            URL parsed = new URL(url);

            return FilenameUtils.getName(parsed.getPath());
        } catch (Exception e) {
            return "Unknown file";
        }
    }

    @Override
    public NotionBlock deserialize(JsonObject jsonObject, JsonDeserializationContext context) throws JsonParseException {
        fileObject = context.deserialize(jsonObject, NotionFile.class);

        return this;
    }
}
