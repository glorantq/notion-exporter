package hu.glorantq.notion.api.model.blocks.impl;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import hu.glorantq.notion.api.model.NotionRichText;
import hu.glorantq.notion.api.model.blocks.NotionBlock;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@ToString(callSuper = true)
@Getter
public class NotionCalloutBlock extends NotionBlock {
    @Expose
    private List<NotionRichText> text;

    @SerializedName("block_color")
    @Expose
    private NotionRichText.Annotations.Color blockColor;

    @Expose
    private List<NotionBlock> children;

    // TODO: Icons not found in API

    public NotionCalloutBlock(String object, UUID id, Type type, Date createdTime, Date lastEditedTime, boolean archived, boolean hasChildren) {
        super(object, id, type, createdTime, lastEditedTime, archived, hasChildren);
    }

    @Override
    public NotionBlock deserialize(JsonObject jsonObject, JsonDeserializationContext context) throws JsonParseException {
        text = context.deserialize(jsonObject.get("text"), TypeToken.getParameterized(List.class, NotionRichText.class).getType());
        blockColor = context.deserialize(jsonObject.get("block_color"), NotionRichText.Annotations.Color.class);
        children = context.deserialize(jsonObject.get("children"), TypeToken.getParameterized(List.class, NotionBlock.class).getType());

        return this;
    }
}