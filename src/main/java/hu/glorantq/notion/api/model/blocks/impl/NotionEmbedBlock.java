package hu.glorantq.notion.api.model.blocks.impl;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import hu.glorantq.notion.api.model.blocks.NotionBlock;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;
import java.util.UUID;

@ToString(callSuper = true)
@Getter
public class NotionEmbedBlock extends NotionBlock {
    @Expose
    private String url;

    public NotionEmbedBlock(String object, UUID id, Type type, Date createdTime, Date lastEditedTime, boolean archived, boolean hasChildren) {
        super(object, id, type, createdTime, lastEditedTime, archived, hasChildren);
    }

    @Override
    public NotionBlock deserialize(JsonObject jsonObject, JsonDeserializationContext context) throws JsonParseException {
        url = context.deserialize(jsonObject.get("url"), String.class);

        return this;
    }
}
