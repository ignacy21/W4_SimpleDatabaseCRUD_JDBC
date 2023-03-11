package zajavka;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Customer {

    private final String id;
    private final String userName;
    private final String email;
    private final String name;
    private final String surname;
    private final LocalDate dateOfBirth;
    private final String telephoneNumber;

    public Customer(String id, String userName, String email, String name, String surname, LocalDate dateOfBirth, String telephoneNumber) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.telephoneNumber = telephoneNumber;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", telephoneNumber='" + telephoneNumber + '\'' +
                '}';
    }
}
