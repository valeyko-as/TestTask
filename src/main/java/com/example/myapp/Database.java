package com.example.myapp;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Database {
    private static final String URL = "jdbc:postgresql://localhost:5432/Persons";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    public static void createTable() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS person ("
                    + "id SERIAL PRIMARY KEY,"
                    + "full_name VARCHAR(100) NOT NULL,"
                    + "birth_date DATE NOT NULL,"
                    + "gender VARCHAR(10) NOT NULL)";

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(createTableSQL);
                System.out.println("Table 'person' created successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createRecord(String fullName, String birthDate, String gender) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String insertSQL = "INSERT INTO person (full_name, birth_date, gender) VALUES (?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                preparedStatement.setString(1, fullName);
                preparedStatement.setDate(2, Date.valueOf(birthDate));
                preparedStatement.setString(3, gender);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Record created successfully.");
                } else {
                    System.out.println("Failed to create record.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void printUniqueRecords() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String selectSQL = "SELECT DISTINCT ON (full_name, birth_date) "
                    + "full_name, birth_date, gender FROM person ORDER BY full_name, birth_date";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                System.out.println("Full Name\tBirth Date\tGender\tAge");

                while (resultSet.next()) {
                    String fullName = resultSet.getString("full_name");
                    Date birthDate = resultSet.getDate("birth_date");
                    String gender = resultSet.getString("gender");
                    int age = calculateAge(birthDate.toLocalDate());

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String formattedBirthDate = dateFormat.format(birthDate);

                    System.out.println(fullName + "\t" + formattedBirthDate + "\t" + gender + "\t" + age);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertRandomData() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String insertSQL = "INSERT INTO person (full_name, birth_date, gender) VALUES (?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                Random random = new Random();
                char firstChar = 'A';
                for (int i = 0; i < 1000000; i++) {
                    String fullName = generateRandomName(random, 5, 10, firstChar);
                    Date birthDate = Date.valueOf("2001-01-01");
                    String gender = random.nextBoolean() ? "male" : "female";

                    preparedStatement.setString(1, fullName);
                    preparedStatement.setDate(2, birthDate);
                    preparedStatement.setString(3, gender);

                    preparedStatement.addBatch();

                    if (firstChar == 'Z') firstChar = 'A';
                    else firstChar++;
                    if (i % 1000 == 0) {
                        preparedStatement.executeBatch();
                    }

                }
                preparedStatement.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertDataWithLetter() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String insertSQL = "INSERT INTO person (full_name, birth_date, gender) VALUES (?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                Random random = new Random();
                char firstChar = 'F';
                for (int i = 0; i < 100; i++) {
                    String fullName = generateRandomName(random, 5, 10, firstChar);
                    Date birthDate = Date.valueOf("2001-01-01");
                    String gender = "male";

                    preparedStatement.setString(1, fullName);
                    preparedStatement.setDate(2, birthDate);
                    preparedStatement.setString(3, gender);

                    preparedStatement.addBatch();

                    if (i % 1000 == 0) {
                        preparedStatement.executeBatch();
                    }

                }
                preparedStatement.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void countSelectTime() {
        long startTime = System.currentTimeMillis();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String selectSQL = "SELECT * FROM person WHERE full_name LIKE 'F%' AND gender = 'male'";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                System.out.println("Full Name\tBirth Date\tGender");

                while (resultSet.next()) {
                    String fullName = resultSet.getString("full_name");
                    Date birthDate = resultSet.getDate("birth_date");
                    String gender = resultSet.getString("gender");
                    String formattedBirthDate = birthDate.toString();

                    System.out.println(fullName + "\t" + formattedBirthDate + "\t" + gender + "\t");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis() - startTime);
    }

    public static void createIndexes() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE INDEX idx_full_name ON person (full_name)");
                statement.executeUpdate("CREATE INDEX idx_gender ON person (gender)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String generateRandomName(Random random, int minLength, int maxLength, char firstChar) {
        int nameLength = random.nextInt(maxLength - minLength + 1) + minLength;
        StringBuilder nameBuilder = new StringBuilder();
        List<String> nameList = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            nameBuilder.delete(0, nameBuilder.length());
            nameBuilder.append(firstChar);
            for (int i = 0; i < nameLength; i++) {
                char randomChar = (char) (random.nextInt(26) + 'a');
                nameBuilder.append(randomChar);
            }
            nameList.add(nameBuilder.toString());
        }
        return String.join(" ", nameList);
    }

    public static int calculateAge(LocalDate birthDate) {
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }
}
