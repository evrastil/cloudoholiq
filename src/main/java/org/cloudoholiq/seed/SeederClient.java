package org.cloudoholiq.seed;

public class SeederClient {

    public static void main(String[] args) {
        RestDBSeeder restDbSeeder = new RestDBSeeder("http", "localhost", 8080);
        restDbSeeder.fillSomeTestData(30);
    }
}
