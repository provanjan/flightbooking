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
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;

@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=JSON path based servlet", "sling.servlet.methods="+HttpConstants.METHOD_GET,"sling.servlet.paths="+"/bin/postservletflightbooking"})
public class SignUpServlet extends SlingAllMethodsServlet{
private static final long serialVersionUID=1L;
private static final String CREATE_PATH= "/content/usergenerated";
private static final Logger log=LoggerFactory.getLogger(SignUpServlet.class);
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
        log.info("!!!!!!");
        String nametxt=obj.optString("nametxt");
        String emailtxt=obj.optString("emailtxt");
        String passtxt=obj.optString("passtxt");
        String confpasstxt=obj.optString("confpasstxt");


        log.info("nametxt......"+nametxt);
        log.info("emailtxt....."+emailtxt);
        log.info("passtxt......"+passtxt);
        log.info("confpasstxt...."+confpasstxt);



        Node firstProduct =JcrUtil.createPath(CREATE_PATH+"/user","cq:Page",session);
        log.info("!!!!!");

        Node firstSubProduct=JcrUtil.createPath(firstProduct.getPath()+"/info_"+(new Date()).getTime(),"nt:unstructured", session);

        log.info("@@@@@@@");


        firstSubProduct.setProperty("nametxt",nametxt);
        firstSubProduct.setProperty("emailtxt",emailtxt);
        firstSubProduct.setProperty("passtxt",passtxt);
        firstSubProduct.setProperty("confpasstxt",confpasstxt);
        session.save();
        response.setStatus(SlingHttpServletResponse.SC_OK);
        response.getWriter().write("Name :"+ nametxt +"email :"+emailtxt);
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
