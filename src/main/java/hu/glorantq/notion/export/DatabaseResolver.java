package hu.glorantq.notion.export;

import hu.glorantq.notion.DatabaseConfiguration;
import hu.glorantq.notion.api.QueryDatabaseBody;

import java.util.Map;

public interface DatabaseResolver {
    String[] getColumnsToRender(String databaseId);
    QueryDatabaseBody.NotionSortObject[] getSorts(String databaseId);
    Map<String, Object> getFilter(String databaseId);
    DatabaseConfiguration.Display getDisplay(String databaseId);
    String getGroupBy(String databaseId);
}
