package com.example.task3.Servlets;

import java.io.*;
import java.sql.*;
import com.example.task3.DBConnector;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "PostingYear", urlPatterns = "/postings/year")
public class PostingYear extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Connection connection;
        PreparedStatement statement;

        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver ());
            connection = DriverManager.getConnection(DBConnector.getURL(), DBConnector.getUSERNAME(), DBConnector.getPASSWORD());



            String sql = "SELECT MatDoc, MaterialDescription, UserName, AuthorizedDelivery FROM postings WHERE YEAR(PstngDate) = ? AND AuthorizedDelivery = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(request.getParameter("period")));
            statement.setInt(2, Integer.parseInt(request.getParameter("authDel")));
            ResultSet resultSet = statement.executeQuery();

            String json = "[";
            while (resultSet.next()) {
                json += "{" +
                        "\n   \"MatDoc\": " + resultSet.getLong(1) + "," +
                        "\n   \"MaterialDescription\": \"" + resultSet.getString(2) + "\"," +
                        "\n   \"UserName\": \"" + resultSet.getString(3) + "\"," +
                        "\n   \"AuthorizedDelivery\": " + resultSet.getBoolean(4) +
                        "\n}";
            }
            json += "]";

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);

            connection.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
