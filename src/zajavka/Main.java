package zajavka;

import java.sql.*;

public class Main {

    public static void main(String[] args) {
        // <protokol>:<baza_danych>://<adres_bazy_danych>/<nazwa_bazy_danych>
        // jdbc:postgersql:127.0.0.1:5432/zajavka

        String address = "jdbc:postgresql://127.0.0.1:5432/zajavka";
//        może być potrzebne do DriverManager.getConnection();
//        String username = "username";
//        String password = "postgresql";

//        String query1 = "INSERT INTO PRODUCER (ID, PRODUCER_NAME, ADDRESS)" +
//                " VALUES (1000, 'Zajavka Group', 'Zajavkowa 15, Warszawa')";
//        String query2 = "UPDATE PRODUCER SET ADDRESS = 'Nowy Adres siedziby' WHERE ID = 1;";
//        String query3 = "DELETE FROM PURCHASE WHERE ID IN (3, 4);";
//        String query4 = "SELECT * FROM PRODUCER;";

        String username = "ntinner27";

        String queryDeleteOpinion = "DELETE FROM OPINION WHERE CUSTOMER_ID IN " +
                "(SELECT ID FROM CUSTOMER WHERE USER_NAME = ?);";
        String queryDeletePurchase = "DELETE FROM PURCHASE WHERE CUSTOMER_ID IN " +
                "(SELECT ID FROM CUSTOMER WHERE USER_NAME = ?);";
        String queryDeleteCustomer = "DELETE FROM CUSTOMER WHERE USER_NAME = ?;";

        try (
                Connection connection = DriverManager.getConnection(address);
                PreparedStatement statement1 = connection.prepareStatement(queryDeleteOpinion);
                PreparedStatement statement2 = connection.prepareStatement(queryDeletePurchase);
                PreparedStatement statement3 = connection.prepareStatement(queryDeleteCustomer);
        ) {
            statement1.setString(1, username);
            int count1 = statement1.executeUpdate();
            System.out.println("queryDeleteOpinion, Changed  : " + count1 + " rows");

            statement2.setString(1, username);
            int count2 = statement2.executeUpdate();
            System.out.println("queryDeletePurchase, Changed : " + count2 + " rows");

            statement3.setString(1, username);
            int count3 = statement3.executeUpdate();
            System.out.println("queryDeleteCustomer, Changed : " + count3 + " rows");


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
