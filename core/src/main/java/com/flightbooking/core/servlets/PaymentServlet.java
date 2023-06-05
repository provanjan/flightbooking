package com.flightbooking.core.servlets;

import com.day.cq.commons.jcr.JcrUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Random;
import javax.jcr.*;
import javax.servlet.Servlet;

@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=JSON path based servlet", "sling.servlet.methods="+HttpConstants.METHOD_GET,"sling.servlet.paths="+"/bin/postservletpayment"})
public class PaymentServlet extends SlingAllMethodsServlet{
    private static final long serialVersionUID=1L;

    private String userPath;
    private String bookingPath;
    private static final Logger log=LoggerFactory.getLogger(SeatServlet.class);
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException{
        response.setContentType("Application/json");
        response.setCharacterEncoding("UTF-8");

        ResourceResolver resourceResolver=request.getResourceResolver();
        Session session =resourceResolver.adaptTo(Session.class);
        PrintWriter out=response.getWriter();


        try{
            String load=request.getParameter("load");
            JSONObject obj=new JSONObject(load);
            log.info("Data loaded");

            String price=obj.optString("price");
            String profile=obj.optString("profile");
            String bookingId=obj.optString("bookingId");

            log.info("price......"+price);
            log.info("profile......"+profile);
            log.info("bookingId......"+bookingId);


            Node node=session.getNode("/content/usergenerated/user");
            String path=node.getPath();
            NodeIterator iterator=node.getNodes();

            while(iterator.hasNext()){
                Node user=iterator.nextNode();
                Property userId=user.getProperty("nametxt");

                String userName=userId.getString();

                if(userName.equals(profile)){
                    log.info("Data verified");
                    userPath=user.getPath();
                    session.save();
                    response.setStatus(SlingHttpServletResponse.SC_OK);

//                    response.getWriter().write("UserName :"+name);
                    log.info(userPath);
                    break;
                }

            }





            Node firstProduct =JcrUtil.createPath(userPath+"/payments","cq:Page",session);
            log.info("Path created till seats");

            Node firstSubProduct=JcrUtil.createPath(firstProduct.getPath()+"/payment_"+(new Date()).getTime(),"nt:unstructured", session);

            log.info("Path created under seats node");


            firstSubProduct.setProperty("payment",price);
            firstSubProduct.setProperty("bookingId",bookingId);

            session.save();
            response.setStatus(SlingHttpServletResponse.SC_OK);
            //           response.getWriter().write("Name :"+ name +"email :"+email);
            response.getWriter().println(price);
        }catch(RepositoryException ex){
            out.println("some problem ");
            out.flush();
            out.close();
            ex.printStackTrace();
        }catch(IOException ex){
            ex.printStackTrace();
        }catch(JSONException ex){
            ex.printStackTrace();
        }finally{
            session.logout();
        }
    }
}

