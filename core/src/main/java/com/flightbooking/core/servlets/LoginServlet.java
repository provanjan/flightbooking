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

@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=JSON path based servlet", "sling.servlet.methods="+HttpConstants.METHOD_GET,"sling.servlet.paths="+"/bin/postloginservletflightbooking"})
public class LoginServlet extends SlingAllMethodsServlet{
    private static final long serialVersionUID=1L;

    private static final Logger log=LoggerFactory.getLogger(LoginServlet.class);
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
            String passtxt=obj.optString("password");



            Node node=session.getNode("/content/usergenerated/user");
            String path=node.getPath();
            NodeIterator iterator=node.getNodes();
            int flag=0;
            while(iterator.hasNext()){
                Node user=iterator.nextNode();
                Property userId=user.getProperty("nametxt");
                Property password=user.getProperty("passtxt");


                String userName=userId.getString();
                String userPassword=password.getString();

                if(userName.equals(usertxt) && userPassword.equals(passtxt)){
                    log.info("Data verified");
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
            if(flag==0){
                response.getWriter().println("UserId or password did not match"+flag);
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

