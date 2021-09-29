package hu.glorantq.notion.api;

import hu.glorantq.notion.api.model.NotionPage;
import hu.glorantq.notion.api.model.NotionPaginatedResponse;
import hu.glorantq.notion.api.model.blocks.NotionBlock;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
}
