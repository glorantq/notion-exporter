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

package hu.glorantq.notion.api.model.blocks;

import com.google.gson.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.UUID;

public class NotionBlockDeserializer implements JsonDeserializer<NotionBlock> {
    @Override
    public NotionBlock deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject blockObject = json.getAsJsonObject();

        try {
            String object = blockObject.get("object").getAsString();
            UUID id = context.deserialize(blockObject.get("id"), UUID.class);
            Date createdTime = context.deserialize(blockObject.get("created_time"), Date.class);
            Date lastEditedTime = context.deserialize(blockObject.get("last_edited_time"), Date.class);
            boolean archived = blockObject.get("archived").getAsBoolean();
            boolean hasChildren = blockObject.get("has_children").getAsBoolean();

            String typeName = blockObject.get("type").getAsString();
            NotionBlock.Type typeEnum = context.deserialize(blockObject.get("type"), NotionBlock.Type.class);

            NotionBlock returnValue;

            try {
                Class<? extends NotionBlock> typeClass = typeEnum.getTypeClass();
                Constructor<? extends NotionBlock> blockConstructor =
                        typeClass.getConstructor(String.class, UUID.class, NotionBlock.Type.class, Date.class, Date.class, boolean.class, boolean.class);

                NotionBlock createdInstance = blockConstructor.newInstance(object, id, typeEnum, createdTime, lastEditedTime, archived, hasChildren);
                JsonObject blockJsonRepresentation = blockObject.get(typeName).getAsJsonObject();

                returnValue = createdInstance.deserialize(blockJsonRepresentation, context);

                if(typeEnum == NotionBlock.Type.VIDEO) {
                    System.out.println(blockObject); // TODO: Remove
                }
            } catch (Exception e0) {
                throw new JsonParseException("Failed to instantiate type-specific block object! " + typeName, e0);
            }

            return returnValue;
        } catch (Exception e) {
            throw new JsonParseException("Failed to retrieve block data!", e);
        }
    }
}
