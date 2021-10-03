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
public class NotionChildDatabaseBlock extends NotionBlock {
    @Expose
    private String title;

    public NotionChildDatabaseBlock(String object, UUID id, Type type, Date createdTime, Date lastEditedTime, boolean archived, boolean hasChildren) {
        super(object, id, type, createdTime, lastEditedTime, archived, hasChildren);
    }

    @Override
    public NotionBlock deserialize(JsonObject jsonObject, JsonDeserializationContext context) throws JsonParseException {
        title = context.deserialize(jsonObject.get("title"), String.class);

        return this;
    }
}