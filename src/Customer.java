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

public class Customer extends Thread {

    //declare private static volatile variables: thread group and 3 semaphores
    private static volatile Semaphore Door, Servicing, Nap;
    private static volatile ThreadGroup customers;
    private static int custCount = 0;
    private static volatile Semaphore countMutex = new Semaphore(1, true);

    public Customer(Semaphore D, Semaphore S, Semaphore N, ThreadGroup C) {
        super(C, "Customer");
        Door = D;
        Servicing = S;
        Nap = N;
    }

    public void run() {
        // method that executes when driver starts thread
        // the thread should release the Nap semaphore
        System.out.println("Customer attempting to enter the restaurant");
        try {
            Door.acquire();
        } catch (InterruptedException ex) {
        }

        try {
            Nap.release();
        } catch (Exception ex) {
        }

        int val;

        // set customer name
        try {
            countMutex.acquire();
            custCount++;
            countMutex.release();
            val = custCount;
            Thread.currentThread().setName("Customer " + val);
            System.out.println(Thread.currentThread().getName() + " has entered the restaurant and been seated");
        } catch (InterruptedException e) {
        }

        // acquire from Servicing semaphore to wait for service
        System.out.println(Thread.currentThread().getName() + " is waiting for the waiter");
        try {
            Servicing.acquire();
        } catch (InterruptedException e) {
        }
        try {
            // now ready to leave so execute a release on Door semaphore
            Door.release();
            System.out.println(Thread.currentThread().getName() + " is leaving");
        } catch (Exception ex) {
        }
    }
} // end Customer class
