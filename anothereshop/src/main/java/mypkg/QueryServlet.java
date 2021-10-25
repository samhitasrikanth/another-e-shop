package mypkg;
 
import java.io.*;
import java.sql.*;
import java.util.logging.*;
import javax.naming.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;
 
public class QueryServlet extends HttpServlet {
 
   private String databaseURL, username, password;
 
   @Override
   public void init(ServletConfig config) throws ServletException {
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
         // Retrieve and process request parameters: "author" and "search"
         String type = request.getParameter("type");
         boolean hasTypeParam = type != null && !type.equals("Select...");
         String searchWord = request.getParameter("search");
         boolean hasSearchParam = searchWord != null && ((searchWord = searchWord.trim()).length() > 0);
 
         out.println("<html><head><title>Query Results</title></head><center> "
                 + "<body align = center style=\"background-color:powderblue;\">"
            + "<h1 style=\"color:white; background-color:grey; font-family:verdana;"
                 + "font-size: 300%\">Stationery World</h1>");
         out.println("<h2>THE STATIONERY SHOP -  Results</h2>");
         
        StringBuilder sqlStr = new StringBuilder();  // more efficient than String
        
        conn = DriverManager.getConnection(databaseURL, username, password);
        stmt = conn.createStatement();
 
         if (!hasTypeParam && !hasSearchParam) {  // No params present
            sqlStr.append("SELECT * FROM products");
            //out.println("<h3>Please select a type or enter a search term!</h3>");
            //out.println("<p><a href='start'>Back to Select Menu</a></p>");
            
            ResultSet rset = stmt.executeQuery(sqlStr.toString());
 
            if (!rset.next()) {  // Check for empty ResultSet (no book found)
               out.println("<h3><br><br><br>No product found. Please try again!</h3>");
               out.println("<p><br><a href='start'>Back to Select Menu</a></p>");
            } else {
               // Print the result in an HTML form inside a table
               out.println("<form method='get' action='cart'>");
               out.println("<input type='hidden' name='todo' value='add' />");
               out.println("<table border='1' cellpadding='6'>");
               out.println("<tr>");
               out.println("<th>&nbsp;</th>");
               out.println("<th>BRAND</th>");
               out.println("<th>NAME</th>");
               out.println("<th>COLOUR</th>");
               out.println("<th>SIZE</th>");
               out.println("<th>PRICE</th>");
               out.println("<th>QTY</th>");
               out.println("</tr>");
               // ResultSet's cursor now pointing at first row
               do {
                  // Print each row with a checkbox identified by book's id
                  String id = rset.getString("id");
                  out.println("<tr>");
                  out.println("<td><input type='checkbox' name='id' value='" + id + "' /></td>");
                  out.println("<td>" + rset.getString("brand") + "</td>");
                  out.println("<td>" + rset.getString("name") + "</td>");
                  out.println("<td>" + rset.getString("colour") + "</td>");
                  out.println("<td>" + rset.getString("size") + "</td>");
                  out.println("<td>$" + rset.getString("price") + "</td>");
                  out.println("<td><input type='text' size='3' value='1' name='qty" + id + "' /></td>");
                  out.println("</tr>");
               } while (rset.next());
               out.println("</table><br />");
               out.println("<input type='submit' value='Add to My Shopping Cart' />");
               out.println("<input type='reset' value='CLEAR' /></form>");
 
               // Hyperlink to go back to search menu
               out.println("<p><br><a href='start'>Back to Select Menu</a></p>");
            }
         } else {
            // Form a SQL command based on the param(s) present
            //StringBuilder sqlStr = new StringBuilder();  // more efficient than String
            sqlStr.append("SELECT * FROM products WHERE qty > 0 AND (");
            if (hasTypeParam) {
               sqlStr.append("type = '").append(type).append("'");
            }
            if (hasSearchParam) {
               if (hasTypeParam) {
                  sqlStr.append(" OR ");
               }
               sqlStr.append("name LIKE '%").append(searchWord)
                     .append("%' OR brand LIKE '%").append(searchWord).append("%'");
            }
            sqlStr.append(") ORDER BY brand, name");
            //System.out.println(sqlStr);  // for debugging
            ResultSet rset = stmt.executeQuery(sqlStr.toString());
 
            if (!rset.next()) {  // Check for empty ResultSet (no book found)
               out.println("<h3><br><br><br>No product found. Please try again!</h3>");
               out.println("<p><br><a href='start'>Back to Select Menu</a></p>");
            } else {
               // Print the result in an HTML form inside a table
               out.println("<form method='get' action='cart'>");
               out.println("<input type='hidden' name='todo' value='add' />");
               out.println("<table border='1' cellpadding='6'>");
               out.println("<tr>");
               out.println("<th>&nbsp;</th>");
               out.println("<th>BRAND</th>");
               out.println("<th>NAME</th>");
               out.println("<th>COLOUR</th>");
               out.println("<th>SIZE</th>");
               out.println("<th>PRICE</th>");
               out.println("<th>QTY</th>");
               out.println("</tr>");
 
               // ResultSet's cursor now pointing at first row
               do {
                  // Print each row with a checkbox identified by book's id
                  String id = rset.getString("id");
                  out.println("<tr>");
                  out.println("<td><input type='checkbox' name='id' value='" + id + "' /></td>");
                  out.println("<td>" + rset.getString("brand") + "</td>");
                  out.println("<td>" + rset.getString("name") + "</td>");
                  out.println("<td>" + rset.getString("colour") + "</td>");
                  out.println("<td>" + rset.getString("size") + "</td>");
                  out.println("<td>$" + rset.getString("price") + "</td>");
                  out.println("<td><input type='text' size='3' value='1' name='qty" + id + "' /></td>");
                  out.println("</tr>");
               } while (rset.next());
               out.println("</table><br />");
 
               // Ask for name, email and phone using text fields (arranged in a table)
               //out.println("<table>");
               //out.println("<tr><td>Enter your Name:</td>");
               //out.println("<td><input type='text' name='cust_name' /></td></tr>");
               //out.println("<tr><td>Enter your Email (user@host):</td>");
               //out.println("<td><input type='text' name='cust_email' /></td></tr>");
               //out.println("<tr><td>Enter your Phone Number (8-digit):</td>");
               //out.println("<td><input type='text' name='cust_phone' /></td></tr></table><br />");
 
               // Submit and reset buttons
               out.println("<input type='submit' value='Add to My Shopping Cart' />");
               out.println("<input type='reset' value='CLEAR' /></form>");
 
               // Hyperlink to go back to search menu
               out.println("<p><br><a href='start'>Back to Select Menu</a></p>");
            }
         }
       
         HttpSession session = request.getSession(false); // check if session exists
         if (session != null) {
             Cart cart;
             synchronized (session) {
             // Retrieve the shopping cart for this session, if any. Otherwise, create one.
             cart = (Cart) session.getAttribute("cart");
                 if (cart != null && !cart.isEmpty()) {
                  out.println("<p><br><a href='cart?todo=view'>View Shopping Cart</a></p>");
                }
             }
         }
               
         out.println("</body></center></html>");
      } catch (SQLException ex) {
         out.println("<h3>Service not available. Please try again later!</h3></body></center></html>");
         Logger.getLogger(QueryServlet.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
         out.close();
         try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
         } catch (SQLException ex) {
            Logger.getLogger(QueryServlet.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
   }
 
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
      doGet(request, response);
   }
}