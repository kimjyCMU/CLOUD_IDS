/* Receive requests from users and interact with "Model" */

package servlet;

import main.*;

import java.io.IOException;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ActionServlet
 */

public class ActionServlet extends HttpServlet {
 private static final long serialVersionUID = 1L;
	getUtilization utilData;
	getRequests requestData;
	getUA UAData;
	getAllUAs allUAsData;
	
    static String name="";
    public ActionServlet(){  
	}

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

  response.setContentType("text/plain");  
  response.setCharacterEncoding("UTF-8"); 
  response.setHeader("Pragma","No-cache");
  response.setDateHeader("Expires",-1);
  response.setHeader("Cache-Control","no-Cache"); 
  response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
  response.setDateHeader("Expires", 0);
  
   // Response utilization history of a clicked machine 
  if(!request.getParameter("system").toString().equals("")){
	name = "";
	name = utilData.getResult(request.getParameter("system").toString(), "system"); 
	response.getWriter().write(name); 
  }
  
  else if(!request.getParameter("network").toString().equals("")){
  	name = "";
	name = utilData.getResult(request.getParameter("network").toString(), "network"); 
	response.getWriter().write(name); 
  }
  
  else if(!request.getParameter("request").toString().equals("")){
  	name = "";
	name = requestData.getResult(request.getParameter("request").toString(), "request"); 
	response.getWriter().write(name); 
  }
  
  else if(!request.getParameter("UA").toString().equals("")){
  	name = "";
	name = UAData.getResult(request.getParameter("UA").toString(), "UA"); 
	response.getWriter().write(name); 
  }
  
  else if(!request.getParameter("allUAs").toString().equals("")){
  	name = "";
	name = allUAsData.getResult("UA");
	response.getWriter().write(name); 
  }
 }

 protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
  // TODO Auto-generated method stub
  
 }

}