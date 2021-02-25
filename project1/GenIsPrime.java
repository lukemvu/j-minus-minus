import java.util.ArrayList;

import jminusminus.CLEmitter;

import static jminusminus.CLConstants.*;

/**
 * This class programmatically generates the class file for the following Java application:
 * 
 * <pre>
 * public class IsPrime {
 *     // Entry point.
 *     public static void main(String[] args) {
 *         int n = Integer.parseInt(args[0]);
 *         boolean result = isPrime(n);
 *         if (result) {
 *             System.out.println(n + " is a prime number");
 *         } else {
 *             System.out.println(n + " is not a prime number");
 *         }
 *     }
 *
 *     // Returns true if n is prime, and false otherwise.
 *     private static boolean isPrime(int n) {
 *         if (n < 2) {
 *             return false;
 *         }
 *         for (int i = 2; i <= n / i; i++) {
 *             if (n % i == 0) {
 *                 return false;
 *             }
 *         }
 *         return true;
 *     }
 * }
 * </pre>
 */

public class GenIsPrime {
    public static void main(String[] args) {
        // Create a CLEmitter instance
        CLEmitter e = new CLEmitter(true);

        // Create an ArrayList instance to store modifiers
        ArrayList<String> modifiers = new ArrayList<String>();

        // public class GenIsPrime {
        modifiers.add("public");
        e.addClass(modifiers, "IsPrime", "java/lang/Object", null, true);

        // public static void main(String[] args) {
        modifiers.clear();
        modifiers.add("public");
        modifiers.add("static");
        e.addMethod(modifiers, "main", "([Ljava/lang/String;)V", null, true);

        // int n = Integer.parseInt(args[0]);
        e.addNoArgInstruction(ALOAD_0);
        e.addNoArgInstruction(ICONST_0);
        e.addNoArgInstruction(AALOAD);
        e.addMemberAccessInstruction(INVOKESTATIC, "java/lang/Integer", "parseInt",
                "(Ljava/lang/String;)I");
        e.addNoArgInstruction(ISTORE_1);

        //boolean result = isPrime(n);
        e.addNoArgInstruction(ILOAD_1); // pop n onto stack
        // pop n and call isPrime() on n, push int result onto stack
        e.addMemberAccessInstruction(INVOKESTATIC, "IsPrime", "isPrime", "(I)I");
        e.addNoArgInstruction(ISTORE_2); // store result into local var

        //if (result) {
        e.addNoArgInstruction(ILOAD_2); // push result onto stack
        e.addNoArgInstruction(ICONST_1); // push 1 onto stack
        e.addBranchInstruction(IF_ICMPNE, "NotPrimeBranch"); // if not true, branch to 'FalsePrime'

        //System.out.println(n + " is a prime number");

        // Get System.out on stack
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

        // Create an instance (say sb) of StringBuffer on stack for string concatenations
        //    sb = new StringBuffer();
        e.addReferenceInstruction(NEW, "java/lang/StringBuffer");
        e.addNoArgInstruction(DUP);
        e.addMemberAccessInstruction(INVOKESPECIAL, "java/lang/StringBuffer", "<init>", "()V");

        // sb.append(n);
        e.addNoArgInstruction(ILOAD_1);
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(I)Ljava/lang/StringBuffer;");

        // sb.append(" is a prime number");
        e.addLDCInstruction(" is a prime number");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuffer;");

        // System.out.println(sb.toString());
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer",
                "toString", "()Ljava/lang/String;");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");

        // return;
        e.addNoArgInstruction(RETURN);

        //} else {System.out.println(n + " is not a prime number");}
        e.addLabel("NotPrimeBranch");

        // Get System.out on stack
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

        // Create an instance (say sb) of StringBuffer on stack for string concatenations
        //    sb = new StringBuffer();
        e.addReferenceInstruction(NEW, "java/lang/StringBuffer");
        e.addNoArgInstruction(DUP);
        e.addMemberAccessInstruction(INVOKESPECIAL, "java/lang/StringBuffer", "<init>", "()V");

        // sb.append(n);
        e.addNoArgInstruction(ILOAD_1);
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(I)Ljava/lang/StringBuffer;");

        // sb.append(" is not a prime number");
        e.addLDCInstruction(" is not a prime number");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuffer;");

        // System.out.println(sb.toString());
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer",
                "toString", "()Ljava/lang/String;");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");

        // return;
        e.addNoArgInstruction(RETURN);

        // Returns true if n is prime, and false otherwise.
        //private static boolean isPrime(int n) {
        modifiers.clear();
        modifiers.add("private");
        modifiers.add("static");
        e.addMethod(modifiers, "isPrime", "(I)I", null, true);

        /**
         *      if n>= 2 go to A:
         *      return false
         *  A:  i = 2
         *  D:  if i > n / i go to B:
         *      if n % i != 0 goto C:
         *      retun false
         *  C:  increment i by 1
         *      goto D:
         *  B:  return true
         */

        e.addNoArgInstruction(ILOAD_0); // push n onto stack
        e.addNoArgInstruction(ICONST_2); // push 2 onto stack
        // if n>=2 jump to A
        e.addBranchInstruction(IF_ICMPGE, "A");
        // return false
        e.addNoArgInstruction(ICONST_0); // push 0 onto stack
        e.addNoArgInstruction(IRETURN); // return 0 (false)

        // A: i = 2
        e.addLabel("A");
        e.addNoArgInstruction(ICONST_2);
        e.addNoArgInstruction(ISTORE_1); // store 2 into local var i

        // D:  if i > n / i go to B:
        e.addLabel("D");
        e.addNoArgInstruction(ILOAD_0); // push n onto stack
        e.addNoArgInstruction(ILOAD_1); // push i onto stack
        e.addNoArgInstruction(IDIV); // n / i
        e.addNoArgInstruction(ILOAD_1); // push i onto stack
        e.addBranchInstruction(IF_ICMPLT, "B");

        // if n % i != 0 goto C:
        e.addNoArgInstruction(ILOAD_0); // push n onto stack
        e.addNoArgInstruction(ILOAD_1); // push i onto stack
        e.addNoArgInstruction(IREM); // n % i
        e.addNoArgInstruction(ICONST_0); // push 0 onto stack
        e.addBranchInstruction(IF_ICMPNE, "C");
        // return false
        e.addNoArgInstruction(ICONST_0); // push 0 onto stack
        e.addNoArgInstruction(IRETURN); // return 0 (false)

        // C:  increment i by 1
        e.addLabel("C");
        e.addNoArgInstruction(ILOAD_1); // push i onto stack
        e.addNoArgInstruction(ICONST_1); // push 1 onto stack
        e.addNoArgInstruction(IADD);
        e.addNoArgInstruction(ISTORE_1); // store into local var i
        // goto D:
        e.addBranchInstruction(GOTO, "D");

        e.addLabel("B");
        // return true
        e.addNoArgInstruction(ICONST_1); // push 0 onto stack
        e.addNoArgInstruction(IRETURN); // return 1 (true)

        // Write GenIsPrime.class to file system
        e.write();
    }
}