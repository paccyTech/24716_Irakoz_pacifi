import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// SLF4J imports
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/RegistrationServlet")
public class RegistrationServlet extends HttpServlet {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/register";
    private static final String JDBC_USER = "postgres";
    private static final String JDBC_PASSWORD = "paccy";

    // SLF4J Logger instance
    private static final Logger logger = LoggerFactory.getLogger(RegistrationServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String password = request.getParameter("password");

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Log the start of the database operation
            logger.info("Attempting to register user: {}", name);

            // Load PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");
            logger.debug("PostgreSQL JDBC Driver loaded successfully.");

            // Establish connection
            conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            logger.debug("Database connection established.");

            // Prepare SQL statement
            String sql = "INSERT INTO users (name, password) VALUES (?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, password);

            // Execute the statement
            pstmt.executeUpdate();
            logger.info("User {} registered successfully.", name);

            // Redirect to a success page
            response.sendRedirect("success.jsp");

        } catch (ClassNotFoundException e) {
            logger.error("JDBC Driver not found", e);
        } catch (SQLException e) {
            logger.error("SQL Exception", e);
        } finally {
            // Clean up resources
            if (pstmt != null) {
                try {
                    pstmt.close();
                    logger.debug("PreparedStatement closed.");
                } catch (SQLException e) {
                    logger.warn("Failed to close PreparedStatement", e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                    logger.debug("Connection closed.");
                } catch (SQLException e) {
                    logger.warn("Failed to close Connection", e);
                }
            }
        }
    }
}
