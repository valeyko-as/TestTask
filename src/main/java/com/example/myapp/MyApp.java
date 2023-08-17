package com.example.myapp;

import java.util.List;

public class MyApp {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java MyApp <command>");
            return;
        }
        int command = Integer.parseInt(args[0]);
        switch (command) {
            case 1 -> Database.createTable();
            case 2 -> {
                if (args.length != 6) {
                    System.out.println("Usage: java MyApp 2 <fullName> <birthDate> <gender>");
                    return;
                }
                List<String> nameList = List.of(args[1], args[2], args[3]);
                String fullName = String.join(" ", nameList);
                String birthDate = args[4];
                String gender = args[5];
                Database.createRecord(fullName, birthDate, gender);
            }
            case 3 -> Database.printUniqueRecords();
            case 4 -> {
                Database.insertRandomData();
                Database.insertDataWithLetter();
            }
            case 5 -> Database.countSelectTime();
            case 6 -> {
                Database.createIndexes();
                Database.countSelectTime();
            }
        }
    }

}
