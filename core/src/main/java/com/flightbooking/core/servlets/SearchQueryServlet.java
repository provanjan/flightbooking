package com.flightbooking.core.servlets;


import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(service = { Servlet.class })
@SlingServletResourceTypes(resourceTypes = "flightbooking/components/flightpage", methods = HttpConstants.METHOD_GET, selectors = {
        "search" }, extensions = "txt")
@ServiceDescription("Search Query Servlet")
public class SearchQueryServlet extends SlingSafeMethodsServlet {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    private QueryBuilder builder;

    /**
     * Session object
     */
    private Session session;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        try {

            log.info("----------< Executing Query Builder Servlet >----------");

            /**
             * This parameter is passed in the HTTP call
             */
            String param = request.getParameter("param");

            log.info("Search term is: {}", param);

            /**
             * Get resource resolver instance
             */
            ResourceResolver resourceResolver = request.getResourceResolver();

            /**
             * Adapting the resource resolver to the session object
             */
            session = resourceResolver.adaptTo(Session.class);

            /**
             * Map for the predicates
             */
            Map<String, String> predicate = new HashMap<>();

            /**
             * Configuring the Map for the predicate
             */
            predicate.put("path", "/content/flightbooking/language-masters/en");
            predicate.put("type", "cq:Page");
            predicate.put("group.1_property","jcr:content/jcr:title");
            predicate.put("group.1_property.value",param);
            predicate.put("group.p.or", "true");
            predicate.put("group.2_property","jcr:content/jcr:description");
            predicate.put("group.2_property.value",param);
            predicate.put("group.p.or", "true");
            predicate.put("group.3_property","jcr:content/cq:tags");
            predicate.put("group.3_property.value","flightbooking:"+param);
            predicate.put("group.p.or", "true");
            predicate.put("group.4_fulltext", param);
            predicate.put("group.4_fulltext.relPath", "jcr:content");

            predicate.put("p.limit", "-1");

            /**
             * Creating the Query instance
             */
            Query query = builder.createQuery(PredicateGroup.create(predicate), session);

            query.setStart(0);
            query.setHitsPerPage(20);

            /**
             * Getting the search results
             */
            SearchResult searchResult = query.getResult();

            for (Hit hit : searchResult.getHits()) {

                if(!hit.getTitle().isEmpty() && !hit.getPath().isEmpty()){
                    String title = hit.getTitle();
                    String path = hit.getPath();

                    response.getWriter().print(title+" "+path+"|");
                }
                else{
                    response.getWriter().print("Sorry the trip you are looking for is not available !!");
                }

            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {

            if (session != null) {

                session.logout();
            }
        }
    }
}


