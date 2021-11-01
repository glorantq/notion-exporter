/*
 *      Copyright (C) 2021 Gerber Lóránt Viktor
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

        JsonElement peopleElement = jsonObject.get("people");
        List<NotionUser> people;
        if(peopleElement != null && peopleElement.isJsonArray()) {
            people = context.deserialize(peopleElement, TypeToken.getParameterized(List.class, NotionUser.class).getType());
        } else {
            people = new ArrayList<>();
        }

        JsonElement filesElement = jsonObject.get("files");
        List<NotionFile> files;
        if(filesElement != null && filesElement.isJsonArray()) {
            files = context.deserialize(filesElement, TypeToken.getParameterized(List.class, NotionFile.class).getType());
        } else {
            files = new ArrayList<>();
        }

        return new NotionPropertyValueObject(id, type, title, richText, number, selectPropertyValue, multiSelect, date, formula, people, files);
    }
}
