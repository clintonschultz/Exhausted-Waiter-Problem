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

import java.util.concurrent.*;
import java.util.Random;

public class Waiter extends Thread { // or implements Runnable


    private static volatile Semaphore Nap;
    private static volatile Semaphore Servicing;
    public static int count = 0;

    // constructor which takes the two semaphores

    public Waiter(Semaphore N, Semaphore S) {
        Nap = N;
        Servicing = S;
    }

    public void run() {

        try {
            do { // waiter should be in infinite loop
                // tryacquire on the Nap semaphore
                try {
                    if (!Nap.tryAcquire() == true) {
                        System.out.println("Waiter is sleeping");
                        Nap.acquire();
                        System.out.println("The waiter is AWAKE");
                    }
                } catch (InterruptedException e) {
                    System.out.println("Waiter says GOODNIGHT FUCKERS. GO HOME.");
                    return;
                }
                count++;
                System.out.println("Waiter is servicing customer " + count);
                Thread.sleep((int) ((Math.random() * (500 - 50)) + 50));
                System.out.println("Customer " + count + " has been served");
                try {
                    Servicing.release();
                } catch (Exception e) {
                }
            } while (true);
        } catch (Exception e) {
        }
    } // end run()
} // end Waiter class
