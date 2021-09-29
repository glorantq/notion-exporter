package hu.glorantq.notion.api.model.blocks;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lombok.ToString;

import java.util.Date;
import java.util.UUID;

@ToString(callSuper = true)
public class NotionNotImplementedBlock extends NotionBlock {
    public NotionNotImplementedBlock(String object, UUID id, Type type, Date createdTime, Date lastEditedTime, boolean archived, boolean hasChildren) {
        super(object, id, type, createdTime, lastEditedTime, archived, hasChildren);
    }

    @Override
    public NotionBlock deserialize(JsonObject jsonObject, JsonDeserializationContext context) throws JsonParseException {
        return this;
    }
}
