package mypkg;
 
import java.io.*;
import java.sql.*;
import java.util.logging.*;
import javax.servlet.*;
import javax.servlet.http.*;
 
public class CheckoutServlet extends HttpServlet {
 
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
      ResultSet rset = null;
      String sqlStr = null;
      HttpSession session = null;
      Cart cart = null;
 
      try {
         conn = DriverManager.getConnection(databaseURL, username, password);
         stmt = conn.createStatement();
 
         out.println("<html><head><title>Checkout</title></head><center> "
                 + "<body align = center style=\"background-color:powderblue;\">"
            + "<h1 style=\"color:white; background-color:grey; font-family:verdana;"
                 + "font-size: 300%\">Stationery World - Checkout</h1>");
         //out.println("<h2>THE STATIONERY SHOP -  Results</h2>");
 
         // Retrieve the Cart
         session = request.getSession(false);
         if (session == null) {
            out.println("<h3>Your Shopping cart is empty!</h3></body></html>");
            return;
         }
         synchronized (session) {
            cart = (Cart) session.getAttribute("cart");
            if (cart == null) {
               out.println("<h3>Your Shopping cart is empty!</h3></body></html>");
               return;
            }
         }
 
         // Retrieve and process request parameters: id(s), cust_name, cust_email, cust_phone
         String custName = request.getParameter("cust_name");
         boolean hasCustName = custName != null && ((custName = custName.trim()).length() > 0);
         String custEmail = request.getParameter("cust_email").trim();
         boolean hasCustEmail = custEmail != null && ((custEmail = custEmail.trim()).length() > 0);
         String custPhone = request.getParameter("cust_phone").trim();
         boolean hasCustPhone = custPhone != null && ((custPhone = custPhone.trim()).length() > 0);
         //String custCredit = request.getParameter("cust_credit").trim();
         //boolean hasCustCredit = custCredit != null && ((custCredit = custCredit.trim()).length() > 0);
         //String custCVV = request.getParameter("cust_cvv").trim();
         //boolean hasCustVV = custCVV != null && ((custCVV = custCVV.trim()).length() > 0);
         //String custDate = request.getParameter("cust_date").trim();
         //boolean hasCustDate = custDate != null && ((custDate = custDate.trim()).length() > 0);
 
         // Validate inputs
         if (!hasCustName) {
            out.println("<h3>Please Enter Your Name!</h3></body></html>");
            return;
         } else if (!hasCustEmail || (custEmail.indexOf('@') == -1)) {
            out.println("<h3>Please Enter Your email (user@host)!</h3></body></html>");
            return;
         } else if (!hasCustPhone || custPhone.length() != 8) {
            out.println("<h3>Please Enter an 8-digit Phone Number!</h3></body></html>");
            return;
         } 
 
         // Display the name, email and phone (arranged in a table)
         out.println("<table>");
         out.println("<tr>");
         out.println("<td>Customer Name:</td>");
         out.println("<td>" + custName + "</td></tr>");
         out.println("<tr>");
         out.println("<td>Customer Email:</td>");
         out.println("<td>" + custEmail + "</td></tr>");
         out.println("<tr>");
         out.println("<td>Customer Phone Number:</td>");
         out.println("<td>" + custPhone + "</td></tr>");
         out.println("</table>");
 
         // Print the book(s) ordered in a table
         out.println("<br />");
         out.println("<table border='1' cellpadding='6'>");
         out.println("<tr>");
         out.println("<th>BRAND</th>");
         out.println("<th>NAME</th>");
         out.println("<th>COLOUR</th>");
         out.println("<th>SIZE</th>");
         out.println("<th>PRICE</th>");
         out.println("<th>QTY</th></tr>");
 
         float totalPrice = 0f;
         for (CartItem item : cart.getItems()) {
            int id = item.getId();
            String brand = item.getBrand();
            String name = item.getName();
            String colour = item.getColour();
            String size = item.getSize();
            int qtyOrdered = item.getQtyOrdered();
            float price = item.getPrice();
 
            // No check for price and qtyAvailable change
            // Update the books table and insert an order record
            sqlStr = "UPDATE products SET qty = qty - " + qtyOrdered + " WHERE id = " + id;
            //System.out.println(sqlStr);  // for debugging
            stmt.executeUpdate(sqlStr);
 
            sqlStr = "INSERT INTO order_records values ("
                    + id + ", " + qtyOrdered + ", '" + custName + "', '"
                    + custEmail + "', '" + custPhone + "')";
            //System.out.println(sqlStr);  // for debugging
            stmt.executeUpdate(sqlStr);
 
            // Show the book ordered
            out.println("<tr>");
            out.println("<td>" + brand + "</td>");
            out.println("<td>" + name + "</td>");
            out.println("<td>" + colour + "</td>");
            out.println("<td>" + size + "</td>");
            out.println("<td>" + price + "</td>");
            out.println("<td>" + qtyOrdered + "</td></tr>");
            totalPrice += price * qtyOrdered;
         }
         out.println("<tr><td colspan='4' align='right'>Total Price: $");
         out.printf("%.2f</td></tr>", totalPrice);
         out.println("</table>");
 
         out.println("<h3>Thank you.</h3>");
         out.println("<a href='start'>Back to Search Menu</a>");
         out.println("</body></html>");
 
         cart.clear();   // empty the cart
      } catch (SQLException ex) {
         cart.clear();   // empty the cart
         out.println("<h3>Service not available. Please try again later!</h3></body></html>");
         Logger.getLogger(CheckoutServlet.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
         out.close();
         try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();  // Return the connection to the pool
         } catch (SQLException ex) {
            Logger.getLogger(CheckoutServlet.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
   }
 
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
      doGet(request, response);
   }
}