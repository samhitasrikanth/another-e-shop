package mypkg;
 
import java.io.*;
import java.sql.*;
import java.util.logging.*;
import javax.servlet.*;
import javax.servlet.http.*;
 
public class EntryServlet extends HttpServlet {
 
   private String databaseURL, username, password;
 
   @Override
   public void init(ServletConfig config) throws ServletException {
      // Retrieve the database-URL, username, password from webapp init parameters
      super.init(config);
      ServletContext context = config.getServletContext();
      databaseURL = context.getInitParameter("databaseURL");
      username = context.getInitParameter("username");
      password = context.getInitParameter("password");
   }
 
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
      response.setContentType("text/html;charset=UTF-8");
      PrintWriter out = response.getWriter();
 
      Connection conn = null;
      Statement stmt = null;
      try {
         conn = DriverManager.getConnection(databaseURL, username, password);
         stmt = conn.createStatement();
         String sqlStr = "SELECT DISTINCT type FROM products WHERE qty > 0";
         // System.out.println(sqlStr);  // for debugging
         ResultSet rset = stmt.executeQuery(sqlStr);
 
         out.println("<html><center><head><title>The Stationery Shop</title></head>"
                 + "<body align = center style=\"background-color:powderblue;\">"
                 + "<h1 style=\"color:white; background-color:grey; font-family:verdana; "
                 + "font-size: 300%\">Stationery World</h1>");
         //out.println("<body align = center style=\"background-color:powderblue;>");
         //out.println("<h2>THE STATIONERY SHOP</h2>");
         // Begin an HTML form
         out.println("<form action=\"/anothereshop/cart\">" +
                 "<input type=\"image\" src=\"/images/shopping.png\" alt=\""
                 + "shopping cart\" style=\"width:80px; height:80px;\" align = right>" +
                 "</form>");
         out.println(" <br><br><br>");
         out.println(" <h2 style=\"font-size: 300%\">Browse by Type of Stationery</h2>");
         out.println("<form method='get' action='search'>");
         //out.println("<p1 style=\"font-size: 150%\">Browse by Type of Stationery:<br><br></p1>");
         
         out.println("<button type=\"submit\" name=\"type\" value=\"Pen\">"
                 + "<img src=\"/images/pen3.jpg\" alt=\"pen\" style=\"width:200px;height:200px;\"></button>");
         out.println("<button type=\"submit\" name=\"type\" value=\"Pencil\">"
                 + "<img src=\"/images/pencil1.jpg\" alt=\"pencil\" style=\"width:200px;height:200px;\"></button>");
         out.println("<button type=\"submit\" name=\"type\" value=\"Paper\">"
                 + "<img src=\"/images/paper3.jpg\" alt=\"paper\" style=\"width:200px;height:200px;\"></button>");
         out.println("<button type=\"submit\" name=\"type\" value=\"Eraser\">"
                 + "<img src=\"/images/eraser1.jpg\" alt=\"eraser\" style=\"width:200px;height:200px;\"></button>");

         out.println("<br><br>");
         
         //while (rset.next()){
            //String type = rset.getString("type");
            //out.println("<input type= 'radio' name='type' value='" + type + "' />" + type);
         //}
         
         //out.println("<input type=\"image\" src=\"/images/pen3.jpg\" name=\"type\" value=\"Pen\" />Pens");
         //out.println("<input type=\"checkbox\" name=\"type\" value=\"Pen\" />Pens");
         //out.println("<input type=\"checkbox\" name=\"type\" value=\"Pencil\" />Pencils");
         //out.println("<input type=\"checkbox\" name=\"type\" value=\"Paper\" />Paper");
         //out.println("<input type=\"checkbox\" name=\"type\" value=\"Eraser\" />Eraser");
         
         out.println("<p></p>");
 
         // A pull-down menu of all the authors with a no-selection option
         //out.println("Choose an Product: <select name='type' size='1'>");
         //out.println("<option value=''>Select...</option>");  // no-selection
         //while (rset.next()) {  // list all the authors
            //String type = rset.getString("type");
            //out.println("<option value='" + type + "'>" + type + "</option>");
         //}
         //out.println("</select><br />");
         //out.println("<p>OR</p>");
         
         out.println("<p1 style=\"font-size: 150%\">OR Manually Search:<br><br></p1>\n" + 
                 "<label for=\"brand\">Enter a Brand Name or Stationery Type:</label><br><br>");
 
         // A text field for entering search word for pattern matching
         out.println("<input type='text' name='search' />");
 
         // Submit and reset buttons
         
         out.println("<br /><br />");
         
         out.println("<input type=\"reset\" value=\"Clear\" "
                 + "style=\"width:50px; background-color: grey; color: white;\"/><br><br>");
         out.println("<input type=\"submit\" value=\"Browse\" style=\"width:200px;\"/>");
         
         //out.println("<input type='submit' value='BROWSE' />");
         //out.println("<input type='reset' value='CLEAR' />");
         out.println("</form>");
         
         // Show "View Shopping Cart" if the cart is not empty
         //HttpSession session = request.getSession(false); // check if session exists
         
         //if (session != null) {
            //Cart cart;
            //synchronized (session) {
               // Retrieve the shopping cart for this session, if any. Otherwise, create one.
               //cart = (Cart) session.getAttribute("cart");
               //if (cart != null && !cart.isEmpty()) {
                 //out.println("<form action=\"/anothereshop/cart\">" +
                 //"<input type=\"image\" src=\"/images/shopping.png\" alt=\""
                 //+ "shopping cart\" style=\"width:80px; height:80px;\" align = right>" +
                 //"</form>");               
               //}
            //}
         //}
         out.println("<br><br>");
         out.println("<br><br>");
         out.println("<br><br>");


         out.println("</center>");
 
         out.println("</body></html>");
      } catch (SQLException ex) {
         out.println("<h3>Service not available. Please try again later!</h3></body></html>");
         Logger.getLogger(EntryServlet.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
         out.close();
         try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
         } catch (SQLException ex) {
            Logger.getLogger(EntryServlet.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
   }
 
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
      doGet(request, response);
   }
}