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
import hu.glorantq.notion.api.model.blocks.NotionBlock;
import lombok.*;
import org.apache.commons.io.FilenameUtils;

import java.net.URL;
import java.util.Date;

/*
    https://developers.notion.com/reference/file-object
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotionFile {
    @SerializedName("type")
    @Expose
    private Type fileType;

    @SerializedName("external")
    @Expose
    private External externalData = null;

    @SerializedName("file")
    @Expose
    private File hostedData = null;

    @Expose
    private String name; // Used in databases

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class External {
        @SerializedName("url")
        @Expose
        private String externalUrl;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class File {
        @SerializedName("url")
        @Expose
        private String hostedUrl;

        @SerializedName("expiry_time")
        @Expose
        private Date expiryTime;
    }

    public enum Type {
        @SerializedName("external") EXTERNAL, @SerializedName("file") HOSTED
    }
}
