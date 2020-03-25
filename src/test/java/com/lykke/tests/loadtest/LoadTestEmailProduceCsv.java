package com.lykke.tests.loadtest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class LoadTestEmailProduceCsv {

    public static void main(String[] args) {
        List<String> emailList = new ArrayList<>();

        int i = 0;
        while (i < 3000) {
            emailList.add("loadtest.1" + (i + 1) + "@example.com");
            i++;
        }

        String objectsCommaSeparated = String.join("\n", emailList);

        try (PrintWriter writer = new PrintWriter(new File("test3.csv"))) {

            writer.write(objectsCommaSeparated);

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
