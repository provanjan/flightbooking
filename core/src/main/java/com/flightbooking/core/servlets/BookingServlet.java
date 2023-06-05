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

@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=JSON path based servlet", "sling.servlet.methods="+HttpConstants.METHOD_GET,"sling.servlet.paths="+"/bin/postservletbooking"})
public class BookingServlet extends SlingAllMethodsServlet{
    private static final long serialVersionUID=1L;

    Random random=new Random();

    private String userPath;

    private static final Logger log=LoggerFactory.getLogger(BookingServlet.class);
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException{
        response.setContentType("Application/json");
        response.setCharacterEncoding("UTF-8");

        ResourceResolver resourceResolver=request.getResourceResolver();
        Session session =resourceResolver.adaptTo(Session.class);
        PrintWriter out=response.getWriter();

        int randNum=random.nextInt(1000);
        String bookingId=Integer.toString(randNum);

        try{
            String load=request.getParameter("load");
            JSONObject obj=new JSONObject(load);
            log.info("Data loaded");

            String name=obj.optString("cardHolderName");
            String address=obj.optString("address");
            String email=obj.optString("emailAddress");
            String phone=obj.optString("phone");
            String adult=obj.optString("adult");
            String child=obj.optString("child");
            String message=obj.optString("message");

            String profile=obj.optString("profile");

            log.info("name......"+name);
            log.info("addresss....."+address);
            log.info("email....."+email);
            log.info("phone......"+phone);
            log.info("adult...."+adult);
            log.info("child...."+child);
            log.info("message...."+message);



            Node node=session.getNode("/content/usergenerated/user");
            String path=node.getPath();
            NodeIterator iterator=node.getNodes();
            int flag=0;
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
                    flag=1;
                    break;
                }
                else{
                    flag=0;
                }
            }


            Node firstProduct =JcrUtil.createPath(userPath+"/bookings","cq:Page",session);
            log.info("Path created till bookings");

            Node firstSubProduct=JcrUtil.createPath(firstProduct.getPath()+"/booking_"+(new Date()).getTime(),"nt:unstructured", session);

            log.info("Path created under bookings node");


            firstSubProduct.setProperty("name",name);
            firstSubProduct.setProperty("address",address);
            firstSubProduct.setProperty("email",email);
            firstSubProduct.setProperty("phone",phone);
            firstSubProduct.setProperty("adult",adult);
            firstSubProduct.setProperty("child",child);
            firstSubProduct.setProperty("message",message);
            firstSubProduct.setProperty("bookingId",bookingId);
            session.save();
            response.setStatus(SlingHttpServletResponse.SC_OK);
 //           response.getWriter().write("Name :"+ name +"email :"+email);
            response.getWriter().println(bookingId);
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
