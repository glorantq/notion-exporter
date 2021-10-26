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
    private Type type;

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

        return new NotionFile(type == Type.EXTERNAL ? NotionFile.Type.EXTERNAL : NotionFile.Type.HOSTED, externalFileData, hostedFileData);
    }

    public boolean isEmoji() {
        return type == Type.EMOJI;
    }

    public enum Type {
        @SerializedName("emoji") EMOJI, @SerializedName("external") EXTERNAL, @SerializedName("file") HOSTED
    }
}
