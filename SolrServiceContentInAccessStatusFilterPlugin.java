package org.dspace.discovery;

import org.apache.solr.common.SolrInputDocument;
import org.dspace.access.status.service.AccessStatusService;

import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.discovery.indexobject.IndexableItem;

import java.sql.SQLException;
import java.util.List;

public class SolrServiceContentInAccessStatusFilterPlugin  implements SolrServiceIndexPlugin{
    private final AccessStatusService accessStatusService;

    public SolrServiceContentInAccessStatusFilterPlugin(AccessStatusService accessStatusService) {
        this.accessStatusService = accessStatusService;
    }

    @Override
    public void additionalIndex(Context context, IndexableObject indexableObject, SolrInputDocument document)  {

        if (indexableObject instanceof IndexableItem) {
            Item item = ((IndexableItem) indexableObject).getIndexedObject();
            String hasAccessStatusWithContent = hasAccessStatusWithContent(item,context);

            // _keyword and _filter because
            // they are needed in order to work as a facet and filter.
            if ((hasAccessStatusWithContent != null)) {
                // no content in the original bundle
                document.addField("has_content_in_access_status", hasAccessStatusWithContent);
                document.addField("has_content_in_access_status_keyword", hasAccessStatusWithContent);
                document.addField("has_content_in_access_status_filter", hasAccessStatusWithContent);
            } else {
                document.addField("has_content_in_access_status", null);
                document.addField("has_content_in_access_status_keyword", null);
                document.addField("has_content_in_access_status_filter", null);
            }
        }

    }

    private String hasAccessStatusWithContent(Item item,Context context)  {

        String accessStatus = null;
        try {
            accessStatus = accessStatusService.getAccessStatus(context,item);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return accessStatus;
    }
}
