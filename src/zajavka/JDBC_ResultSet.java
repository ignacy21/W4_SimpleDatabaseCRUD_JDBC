package zajavka;

import java.sql.*;
import java.util.List;

public class JDBC_ResultSet {

    public static void main(String[] args) {

        String query = "SELECT * FROM CUSTOMER WHERE NAME LIKE ?";
        String parameter = "%me%";

        String address = "jdbc:postgresql://127.0.0.1:5432/zajavka";

        try (
                Connection connection = DriverManager.getConnection(address);
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setString(1, parameter);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Customer> customerList = CustomerMapper.mapToCustomers(resultSet);
                customerList.forEach(customer -> System.out.println("Customer: " + customer));
            }

        } catch (SQLException e) {
            System.err.printf("Error, sqlState: [%s], errorCode [%s], message[%s]: ",
                    e.getSQLState(), e.getErrorCode(), e.getMessage());
        }
    }
}
