/* Name: Clinton J Schultz
Professor: Dr. Gary Newell
Assignment: Concurrency - The Sleepy Waiter Problem
Date: 10/30/2021

The object of this assignment is to implement a waiter class and a customer class
that will be used to simulate a waiter serving customers during a busy rush and a
slow period at a diner with a 15-person limit. Both classes are Threads: the Waiter
serves 100 total Customer Threads and each phase of the dining experience is output
to the console so that you can see how JVM is handling the Threads internally.
 */

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.Semaphore;
import java.util.Random;
import java.util.Scanner;

public class Driver {

    private static volatile Semaphore Door, Servicing, Nap;

    public static void main(String[] args) throws InterruptedException {

        Door = new Semaphore(15, true);
        Servicing = new Semaphore(0, true);
        Nap = new Semaphore(0, true);
        int count = 0;
        ThreadGroup customers = new ThreadGroup("customers");

        // create RNG and Scanner
        Random gen = new Random();
        Scanner scan = new Scanner(System.in);

        // create the two ThreadGroups for "rushhour" and "slowtime"
        ThreadGroup rushhour = new ThreadGroup("rushhour");
        ThreadGroup slowtime = new ThreadGroup("slowtime");

        // instantiate a new Waiter object passing it Nap and Servicing Semaphores
        Waiter waiter = new Waiter(Nap, Servicing);
        String name = "";
        // declare and instantiate array of 100 Customer objects/threads
        Customer[] customerArray = new Customer[100];

        // walk through first 50 cells of array and create 50 Customer objects/threads
        // passing them the first ThreadGroup "rushhour" the Door, Servicing, and Nap semaphores
        for (int i = 0; i < 50; i++) {
            Customer customer = new Customer(Door, Servicing, Nap, rushhour);
            //System.out.println(customer.getName() + " was created in rushhour");
            customerArray[i] = customer;
        }
        // Next, walk through remaining 50 doing the same but passing in ThreadGroup "slowtime"
        for (int i = 50; i < 100; i++) {
            Customer customer = new Customer(Door, Servicing, Nap, slowtime);
            //System.out.println(customer.getName() + " was created in slowtime");
            customerArray[i] = customer;
        }
        // prompt user to hit enter for "rushhour" simulation
        String enterKey = "word";
        do {
            System.out.println("Please press ENTER key to simulate rushhour");
            enterKey = scan.nextLine();
        } while (!enterKey.equals(""));

        // start up the Waiter object/thread
        waiter.start();

        // sleep for 1 sec, then walk through and start up the first 50 customers
        Thread.sleep(1000);

        for (int i = 0; i < 50; i++) { // also need a join loop
            customerArray[i].start();
        }

        // While the first ThreadGroup's activeCount() > 0
        // busy wait
        do {
            ;
        } while (rushhour.activeCount() > 0);

        System.out.println("\n");
        // Prompt user to hit enter for "slowtime" simulation
        enterKey = "word";
        do {
            System.out.println("Please press ENTER key to simulate slowtime");
            enterKey = scan.nextLine();
        } while (!enterKey.equals(""));

        // walk through second 50 customer objects/threads and start them, but this time
        // wait from a random 50-500 milliseconds between each start
        for (int i = 50; i < 100; i++) {
            customerArray[i].start();
            Thread.sleep((int) ((Math.random() * (500 - 50)) + 50));
        }

        // wait for the second ThreadGroup's activeCount() to reach zero
        // then, interrupt the Waiter object and the program will end.
        do {
            ;
        } while (slowtime.activeCount() != 0);
        waiter.interrupt();
        Thread.sleep(200);
    }
}
