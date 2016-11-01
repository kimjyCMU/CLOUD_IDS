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
	getData data;
	
    static String name="";
    public ActionServlet(){  
	}

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

  response.setContentType("text/plain");  
  response.setCharacterEncoding("UTF-8"); 
  response.setHeader("Pragma","No-cache");
  response.setDateHeader("Expires",-1);
  response.setHeader("Cache-Control","no-Cache"); 
  
   // Response utilization history of a clicked machine 
  if(!request.getParameter("data").toString().equals("")){
	name = data.getResult(request.getParameter("data").toString()); 
	response.getWriter().write(name); 
  }
 }

 protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
  // TODO Auto-generated method stub
  
 }

}