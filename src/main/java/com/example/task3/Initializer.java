package com.example.task3;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.List;

@WebListener
public class Initializer  implements ServletContextListener {
    public List<String> loginsData;
    public List<String> postingsData;
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        List<String> loginsData = UnauthorizedDeliveries.readLogins();
        List<String> postingsData = UnauthorizedDeliveries.readPostings();

        UnauthorizedDeliveries.authorizedDelivery(postingsData, loginsData);

        try {
            UnauthorizedDeliveries.loginsToBD(loginsData);
            UnauthorizedDeliveries.postingsToBD(postingsData);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

