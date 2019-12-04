package Wolverines;

import java.lang.reflect.*;

public class CCLRun {

    public static void main(String[] args) throws Exception{
        //  First argument is Java program (class) the user wants to run
        String progClass = args[0];

        //  The arguments to the program are arguments 1...n, so separate them out into own array
        String progArgs[] = new String[args.length-1];
        System.arraycopy(args, 1, progArgs, 0, progArgs.length);

        //  Create CompilingClassLoader
        CompilingClassLoader ccl = new CompilingClassLoader();

        //  Load the main class through CCL
        Class clas = ccl.loadClass(progClass);

        //  Use reflection to call main() method, and to pass the arguments in.

        //  Get a class representing the type of the main method's argument
        Class mainArgType[] = {(new String[0]).getClass()};

        //  Find the standard main method in the class
        Method main = clas.getMethod("main", mainArgType);

        //  Create a list containing the arguments -- in this case, an array of strings
        Object argsArray[] = {progArgs};

        //  Call method
        main.invoke(null, argsArray);

    }

}
