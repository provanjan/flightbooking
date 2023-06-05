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

@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=JSON path based servlet", "sling.servlet.methods="+HttpConstants.METHOD_GET,"sling.servlet.paths="+"/bin/postservletseat"})
public class SeatServlet extends SlingAllMethodsServlet{
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

            String seats=obj.optString("seats");
            String profile=obj.optString("profile");
            String bookingId=obj.optString("bookingId");

            log.info("seats......"+seats);
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


//            log.info("Data loaded");
//            String bookingId=obj.optString("bookingId");
//
//            log.info("seats......"+seats);
//            log.info("profile......"+profile);
//            log.info("bookingId......"+bookingId);
//
//            Node node2=session.getNode(userPath+"/bookings");
//
//            String path2=node2.getPath();
//            log.info(path2);
//            NodeIterator nodeIterator=node2.getNodes();
//
//            while(nodeIterator.hasNext()){
//                Node booking=nodeIterator.nextNode();
//                Property bookingIdProperty=booking.getProperty("bookingId");
//
//                String userBookingId=bookingIdProperty.getString();
//
//                if(userBookingId.equals(bookingId)){
//                    log.info("Data for booking verified");
//                    bookingPath=booking.getPath();
//                    session.save();
//
//                    response.setStatus(SlingHttpServletResponse.SC_OK);
//                    log.info(bookingPath);
////                    response.getWriter().write("Booking Id :"+bookingId);
//
//                    break;
//                }
//
//            }



            Node firstProduct =JcrUtil.createPath(userPath+"/seats","cq:Page",session);
            log.info("Path created till seats");

            Node firstSubProduct=JcrUtil.createPath(firstProduct.getPath()+"/seat_"+(new Date()).getTime(),"nt:unstructured", session);

            log.info("Path created under seats node");


            firstSubProduct.setProperty("seats",seats);
            firstSubProduct.setProperty("bookingId",bookingId);

            session.save();
            response.setStatus(SlingHttpServletResponse.SC_OK);
            //           response.getWriter().write("Name :"+ name +"email :"+email);
            response.getWriter().println(seats);



            Node secondProduct =JcrUtil.createPath(userPath+"/bookingStats","cq:Page",session);
            log.info("Path created till bookingStats");

            Node secondSubProduct=JcrUtil.createPath(secondProduct.getPath()+"/bookingStat_"+(new Date()).getTime(),"nt:unstructured", session);

            log.info("Path created under bookingStats node");

            secondSubProduct.setProperty("bookingId",bookingId);
            secondSubProduct.setProperty("checkIn",false);
            secondSubProduct.setProperty("boardingPass",false);
            secondSubProduct.setProperty("security",false);
            secondSubProduct.setProperty("boardingStat",false);

            session.save();
            response.setStatus(SlingHttpServletResponse.SC_OK);
            //           response.getWriter().write("Name :"+ name +"email :"+email);
            response.getWriter().println(seats);
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
