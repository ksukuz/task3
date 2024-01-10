package com.example.task3;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UnauthorizedDeliveries{
    private static final String loginPath = "D:/A1/logins.csv";
    private static final String postingsPath = "D:/A1/postings.csv";
    private static final String sqlLogins = "INSERT INTO logins (Application, AppAccountName, IsActive, JobTitle, Department) VALUES (?, ?, ?, ?, ?)";
    private static final String sqlPostings = "INSERT INTO postings (MatDoc, Item, DocDate, PstngDate, MaterialDescription, Quantity, BUn, AmountLC, Crcy, UserName, AuthorizedDelivery) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static List<String> readLogins(){
        try( BufferedReader loginsReader = new BufferedReader(new FileReader(loginPath))){
            List<String> loginsData = new ArrayList<>();
            String line;
            while ((line = loginsReader.readLine()) != null) {
                loginsData.add(line);
            }
            return loginsData;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static List<String> readPostings(){
        try(BufferedReader postingsReader = new BufferedReader(new FileReader(postingsPath))){
            List<String> postingsData = new ArrayList<>();
            String line;
            while ((line = postingsReader.readLine()) != null) {
                String[] postings = line.split(";");

                if(!postings[0].isEmpty()){
                    postingsData.add(line);
                }
            }
            return postingsData;
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void authorizedDelivery(List<String> postings, List<String> logins){
        String authPole = postings.get(0) + "; AuthorizedDelivery";
        postings.set(0, authPole);
        Map<String, Boolean> isActive = isActive(logins);
        for (int i = 1; i < postings.size(); i++) {
            String[] line = postings.get(i).split(";");
            if(isActive.containsKey(line[9])){
                if(isActive.get(line[9])) {
                    postings.set(i, postings.get(i) + "; true");
                    System.out.println(postings.get(i));
                }
                else
                    postings.set(i, postings.get(i) + "; false");
            }else  postings.set(i, postings.get(i) + "; false");

        }
    }

    private static Map<String, Boolean> isActive(List<String> logins){
        Map<String, Boolean> accountStatus = new HashMap<>();
        for (String login : logins) {
            String[] fields = login.split(",");
            if(fields[2].contains("True"))
                accountStatus.put(fields[1], true);
            else accountStatus.put(fields[1], false);
        }
        return accountStatus;
    }
    public static void loginsToBD(List<String> logins) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try(Connection connection = DriverManager.getConnection(DBConnector.getURL(), DBConnector.getUSERNAME(), DBConnector.getPASSWORD());
            PreparedStatement statement = connection.prepareStatement(sqlLogins)){

            for(String login : logins){
                if(login.equals(logins.get(0)))
                    continue;
                String[] data = login.split(",");
                statement.setString(1, data[0]);
                statement.setString(2, data[1]);
                statement.setBoolean(3, Boolean.parseBoolean(data[2].trim().toLowerCase()));
                statement.setString(4, data[3]);
                statement.setString(5, data[4]);
                statement.addBatch();
                statement.executeBatch();

            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public static void postingsToBD(List<String> postings) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try(Connection connection = DriverManager.getConnection(DBConnector.getURL(), DBConnector.getUSERNAME(), DBConnector.getPASSWORD());
            PreparedStatement statement = connection.prepareStatement(sqlPostings)){

            System.out.println(postings.get(1));
            for(String posting : postings){
                if(posting.equals(postings.get(0)))
                    continue;
                String[] data = posting.split(";");
                statement.setLong(1, Long.parseLong(data[0].trim()));
                statement.setInt(2, Integer.parseInt(data[1].trim()));
                statement.setDate(3, Date.valueOf(stringDateToSql(data[2].trim())));
                statement.setDate(4, Date.valueOf(stringDateToSql(data[3].trim())));
                statement.setString(5, data[4].trim());
                statement.setInt(6, Integer.parseInt(data[5].trim()));
                statement.setString(7, data[6]);
                statement.setFloat(8, Float.parseFloat(data[7].trim().replace(",",".")));
                statement.setString(9, data[8]);
                statement.setString(10, data[9]);
                statement.setBoolean(11, Boolean.parseBoolean(data[10].trim()));
                statement.addBatch();
                statement.executeBatch();

            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }
    private static String stringDateToSql(String date){
        StringBuilder sqlDate = new StringBuilder();
        sqlDate.append(date.replace(".", "-").substring(6, 10));
        sqlDate.append("-");
        sqlDate.append(date.replace(".", "-").substring(3, 5));
        sqlDate.append("-");
        sqlDate.append(date.replace(".", "-").substring(0, 2));
        return sqlDate.toString();
    }
}

