/*
 *     Copyright (C) 2021 Gerber Lóránt Viktor
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

/*
    https://developers.notion.com/reference/user
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotionUser {
    @Expose
    private String object;

    @Expose
    private UUID id;

    @Expose
    private Type type;

    @Expose
    private String name;

    @Expose
    @SerializedName("avatar_url")
    private String avatarUrl;

    public enum Type {
        @SerializedName("person") PERSON, @SerializedName("bot") BOT
    }
}
