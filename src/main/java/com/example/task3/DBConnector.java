package com.example.task3;

public class DBConnector {
    private static final String URL="jdbc:mysql://localhost:3306/db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    public static String getURL(){
        return URL;
    }

    public static String getUSERNAME() {
        return USERNAME;
    }

    public static String getPASSWORD() {
        return PASSWORD;
    }
}
