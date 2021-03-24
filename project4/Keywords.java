import java.lang.IllegalArgumentException;
import java.lang.Math;
import java.lang.System;

interface Interface {
    public long f();
}

public class Keywords implements Interface {
    public long f() {
        return (long) 42;
    }

    public static void main(String[] args) {
        int x = 2;
        switch (x) {
            case 1:
                System.out.println("One");
                break;
            case 2:
                System.out.println("Two");
                break;
            default:
                System.out.println("Error!");
        }
        for (int i = -5; i <= 3; i++) {
            if (i <= 0) {
                continue;
            }
            System.out.println("Hello, World");
        }
        int i = 1;
        do {
            System.out.println("Hello, World");
        } while (i++ <= 3);
        try {
            System.out.println(sqrt((double) 2));
            System.out.println(sqrt((double) -2));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("Done!");
        }
        Keywords o = new Keywords();
        System.out.println(o.f());
    }

    private static double sqrt(double x) throws IllegalArgumentException {
        if (x < (double) 0) {
            throw new IllegalArgumentException("x must be positve");
        }
        return Math.sqrt(x);
    }
}