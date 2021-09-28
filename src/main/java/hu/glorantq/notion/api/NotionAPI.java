package hu.glorantq.notion.api;

import hu.glorantq.notion.api.model.NotionPage;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/*
    https://developers.notion.com/reference/intro
 */
public interface NotionAPI {
    // Pages
    @GET("pages/{pageId}")
    Call<NotionPage> retrievePage(@Path("pageId") String pageId);
}
