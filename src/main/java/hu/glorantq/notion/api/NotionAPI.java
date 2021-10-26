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

package hu.glorantq.notion.api;

import hu.glorantq.notion.api.model.NotionPage;
import hu.glorantq.notion.api.model.NotionPaginatedResponse;
import hu.glorantq.notion.api.model.blocks.NotionBlock;
import retrofit2.Call;
import retrofit2.http.*;

/*
    https://developers.notion.com/reference/intro
 */
public interface NotionAPI {
    // Pages
    @GET("pages/{pageId}")
    Call<NotionPage> retrievePage(@Path("pageId") String pageId);

    // Blocks
    @GET("blocks/{blockId}")
    Call<NotionBlock> retrieveBlock(@Path("blockId") String blockId);

    @GET("blocks/{blockId}/children")
    Call<NotionPaginatedResponse<NotionBlock>> retrieveBlockChildren(@Path("blockId") String blockId, @Query("start_cursor") String startCursor, @Query("page_size") int pageSize);

    @GET("blocks/{blockId}/children")
    Call<NotionPaginatedResponse<NotionBlock>> retrieveBlockChildren(@Path("blockId") String blockId, @Query("page_size") int pageSize);

    @GET("blocks/{blockId}/children")
    Call<NotionPaginatedResponse<NotionBlock>> retrieveBlockChildren(@Path("blockId") String blockId);

    // Databases
    @GET("databases/{databaseId}")
    Call<NotionPage> retrieveDatabase(@Path("databaseId") String databaseId);

    @POST("databases/{databaseId}/query")
    Call<NotionPaginatedResponse<NotionPage>> queryDatabase(@Path("databaseId") String databaseId, @Body QueryDatabaseBody queryBody);
}
