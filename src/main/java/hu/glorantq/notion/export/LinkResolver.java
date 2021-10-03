package hu.glorantq.notion.export;

public interface LinkResolver {
    String resolveAssetLink(String rawUrl, String ownPageId);
    String resolvePageLink(String pageId, String ownPageId);
}
