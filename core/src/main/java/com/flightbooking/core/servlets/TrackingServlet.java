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
import javax.jcr.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=JSON path based servlet", "sling.servlet.methods="+HttpConstants.METHOD_GET,"sling.servlet.paths="+"/bin/postservlettrackbooking"})
public class TrackingServlet extends SlingAllMethodsServlet{
    private static final long serialVersionUID=1L;

    private String userPath;
    private String bookingPath;
    private static final Logger log=LoggerFactory.getLogger(TrackingServlet.class);
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
            System.out.println("Data loaded");
            log.info("Data loaded");

            String usertxt=obj.optString("userId");
            String bookingId=obj.optString("bookingId");



            Node node=session.getNode("/content/usergenerated/user");
            String path=node.getPath();
            NodeIterator iterator=node.getNodes();
            int flag=0;
            while(iterator.hasNext()){
                Node user=iterator.nextNode();
                Property userId=user.getProperty("nametxt");
                String userName=userId.getString();


                if(userName.equals(usertxt)){
                    log.info("Data verified");
                    userPath=user.getPath();
                    session.save();

                    response.setStatus(SlingHttpServletResponse.SC_OK);
                    response.getWriter().write("UserName :"+usertxt);
                    flag=1;
                    break;
                }
                else{
                    flag=0;
                }
            }
            String bookingStatsPath=userPath+"/bookingStats";
            Node bookingStatNode=session.getNode(bookingStatsPath);

            log.info(bookingStatsPath);
            NodeIterator nodeIterator=bookingStatNode.getNodes();
            while(nodeIterator.hasNext()){
                Node bookingStat=nodeIterator.nextNode();
                Property bookingStatNodeProperty=bookingStatNode.getProperty("bookingId");
                String bookingStatNodeString=bookingStatNodeProperty.toString();

                if(bookingStatNodeString.equals(bookingId)){
                    log.info("Booking Id verified");
                    bookingPath=bookingStat.getPath();
                    session.save();

                    response.setStatus(SlingHttpServletResponse.SC_OK);
                    break;
                }
            }

            log.info(bookingPath);

            Node bookingStatActual=session.getNode(bookingPath);
            Property checkIn=bookingStatActual.getProperty("checkIn");
            Property boardingPass=bookingStatActual.getProperty("boardingPass");
            Property security=bookingStatActual.getProperty("security");
            Property boardingStat=bookingStatActual.getProperty("boardingStat");

            String checkInString=checkIn.getString();
            String boardingPassString=boardingPass.getString();
            String securityString=security.getString();
            String boardingStatString=boardingStat.getString();

            if(checkInString.equals("true")){
                response.getWriter().println("checkIn");
            }else{
                response.getWriter().println("checkInIncomplete");
            }
            if(checkInString.equals("true") && boardingPassString.equals("true")){
                response.getWriter().println("boardingPass");
            }
            if(checkInString.equals("true") && boardingPassString.equals("true") && securityString.equals("true")){
                response.getWriter().println("security");
            }
            if(checkInString.equals("true") && boardingPassString.equals("true") && securityString.equals("true") && boardingStatString.equals("true")){
                response.getWriter().println("boarding success");
            }

        }catch(RepositoryException ex){
            out.println("We ran into some problem ");
            out.flush();
            out.close();
            ex.printStackTrace();
        }catch(IOException ex){
            ex.printStackTrace();
        }catch(JSONException ex){
            ex.printStackTrace();
        }
        finally{
            session.logout();
        }
    }
}

