package hu.glorantq.notion.api.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NotionPropertyValueObjectDeserializer implements JsonDeserializer<NotionPropertyValueObject> {
    @Override
    public NotionPropertyValueObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String id = context.deserialize(jsonObject.get("id"), String.class);
        NotionPropertyValueObject.Type type = context.deserialize(jsonObject.get("type"), NotionPropertyValueObject.Type.class);

        List<NotionRichText> title;
        JsonElement titleElement = jsonObject.get("title");
        if(titleElement != null) {
            if (titleElement.isJsonArray()) {
                title = context.deserialize(titleElement, TypeToken.getParameterized(List.class, NotionRichText.class).getType());
            } else {
                NotionRichText richText = context.deserialize(titleElement, NotionRichText.class);

                title = new ArrayList<>();
                title.add(richText);
            }
        } else {
            title = new ArrayList<>();
        }

        List<NotionRichText> richText;
        JsonElement richTextElement = jsonObject.get("rich_text");
        if(richTextElement != null) {
            if(richTextElement.isJsonArray()) {
                richText = context.deserialize(richTextElement, TypeToken.getParameterized(List.class, NotionRichText.class).getType());
            } else {
                richText = new ArrayList<>();
                richText.add(context.deserialize(richTextElement, NotionRichText.class));
            }
        } else {
            richText = new ArrayList<>();
        }

        double number;
        if(jsonObject.has("number") && !jsonObject.get("number").isJsonObject()) {
            number = context.deserialize(jsonObject.get("number"), double.class);
        } else {
            number = 0d;
        }

        NotionPropertyValueObject.SelectPropertyValue selectPropertyValue = context.deserialize(jsonObject.get("select"), NotionPropertyValueObject.SelectPropertyValue.class);

        List<NotionPropertyValueObject.SelectPropertyValue> multiSelect;
        JsonElement multiSelectElement = jsonObject.get("multi_select");
        if(multiSelectElement != null) {
            if(multiSelectElement.isJsonObject()) {
                multiSelect = context.deserialize(multiSelectElement.getAsJsonObject().get("options"), TypeToken.getParameterized(List.class, NotionPropertyValueObject.SelectPropertyValue.class).getType());
            } else {
                multiSelect = context.deserialize(multiSelectElement, TypeToken.getParameterized(List.class, NotionPropertyValueObject.SelectPropertyValue.class).getType());
            }
        } else {
            multiSelect = null;
        }

        NotionPropertyValueObject.DatePropertyValue date = context.deserialize(jsonObject.get("date"), NotionPropertyValueObject.DatePropertyValue.class);
        NotionPropertyValueObject.FormulaPropertyValue formula = context.deserialize(jsonObject.get("formula"), NotionPropertyValueObject.FormulaPropertyValue.class);
        List<NotionPropertyValueObject.RelationPropertyValue> relation = context.deserialize(jsonObject.get("relation"), TypeToken.getParameterized(List.class, NotionPropertyValueObject.RelationPropertyValue.class).getType());

        return new NotionPropertyValueObject(id, type, title, richText, number, selectPropertyValue, multiSelect, date, formula, relation);
    }
}
