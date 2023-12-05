package org.example;


import org.testcontainers.containers.TrinoContainer;

import java.sql.DriverManager;
import java.sql.SQLException;

public class TrinoStuff {

    public void doSomething() throws SQLException, InterruptedException {

        try (TrinoContainer trinoContainer = new TrinoContainer("trinodb/trino:352")) {

            trinoContainer.start();
            
            for (int i = 0; i < 30; i++) {
                System.out.println("Calling...");

                try (
                        var connection = DriverManager.getConnection(trinoContainer.getJdbcUrl(), trinoContainer.getUsername(), trinoContainer.getPassword());
                        var statement = connection.createStatement();
                ) {
                    var resultSet = statement.executeQuery("SELECT 1");
                    resultSet.next();
                    resultSet.getInt(1);
                }

                // Wait a bit
                System.out.println("Waiting...");
                Thread.sleep(1000);
            }
            
            trinoContainer.stop();
        }
    }

}