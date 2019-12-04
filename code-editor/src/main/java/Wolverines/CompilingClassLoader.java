package Wolverines;

import java.io.*;

/*
    A CompilingClassLoader compiles your Java source on-the-fly. It checks for nonexistent .class files, or .class files
    that are older than their corresponding source code.
*/

public class CompilingClassLoader extends ClassLoader{

    //  Given a filename, read the entirety of that file from disk and return it as a byte array
    private byte[] getBytes(String filename) throws IOException {
        //  Find out the length of the file
        File file = new File (filename);
        long len = file.length();

        //  Create an array that's right size for the file's contents
        byte raw[] = new byte[(int) len];

        // Open the file
        FileInputStream fin = new FileInputStream(file);

        //  Read all into array, if don't get all call an error
        int r = fin.read(raw);
        if(r != len)
            throw new IOException("Can't read all, " + r + " != " + len);

        // CLOSE the file
        fin.close();

        //  Finally return the file contents as an array
        return raw;
    }

    //  Spawn a process to compile the java source code file specified in the 'javaFile' parameter.
    //  Return true if the compilation worked, false otherwise
    private boolean compile(String javaFile) throws IOException{
        //  Let the user know what's going on
        System.out.println("CCL: Compiling " + javaFile + "...");

        //  Start up the compiler
        Process p = Runtime.getRuntime().exec("javac " + javaFile);

        //  Wait to finish run
        try{
            p.waitFor();
        } catch(InterruptedException ie){
            System.out.println(ie);
        }

        //  Check the return code, in case of compilation error
        int ret = p.exitValue();

        //  Tell whether the compilation worked
        return ret == 0;
    }

    //  Heart of ClassLoader -- automatically compile source as necessary when looking for class files
    public Class loadClass(String name, boolean resolve) throws ClassNotFoundException {

        //  Goal is to get a Class Object
        Class clas = null;

        //  First, see if class has been previously loaded
        clas = findLoadedClass(name);

        //System.out.println("findLoadedClass: " + clas);

        //  Create a pathname from the class name
        //  E.g. java.lang.Object => java/lang/Object
        String fileStub = name.replace('.', '/');

        //  Build objects pointing to the source code (.java) and object code (.class)
        String javaFileName = fileStub + ".java";
        String classFileName = fileStub + ".class";

        File javaFile = new File(javaFileName);
        File classFile = new File(classFileName);

        //  System.out.println("j " + javaFile.lastModified() + " c " + classFile.lastModified());

        //  First, see if we want to try compiling. We do if
        //  (a) There is source code and,
        //  (b.0) There is no object code
        //  (b.1) There is object code but it's older than the source
        if(javaFile.exists() &&
                (!classFile.exists() || javaFile.lastModified() > classFile.lastModified())){
            try{
                //  Try to compile, if error then declare failure. (It's not good enough to use an already existing
                //  but out-of-date, classFile)
                if(!compile(javaFileName) || !classFile.exists()){
                    throw new ClassNotFoundException("Compile failed: " + javaFileName);
                }
            }catch(IOException ie){
                //  Another place where we might come to if we fail to compile
                throw new ClassNotFoundException(ie.toString());
            }
        }
        //  Try loading up the raw bytes, assuming they were properly compiled, or didn't need to be compiled
        try{
            //  Read the bytes
            byte raw[] = getBytes(classFileName);

            //  Try converting into class
            clas = defineClass(name, raw, 0, raw.length);
        } catch(IOException ie){
            //  Not a failure! If reached here, it might mean that we are dealing with a class in a library, like
            //  java.lang.Object
        }

        //  System.out.println("defineClass: " + clas);

        //  Maybe clas is in a library -- try loading normal way
        if(clas == null) {
            clas = findSystemClass(name);
        }

        //  System.out.println("findSystemClass: " + clas);

        //  Resolve the class, if any, but only if the "resolve"
        //  Flag set to true
        if(resolve && clas != null)
            resolveClass(clas);

        //  If still no class, it's an error
        if(clas == null)
            throw new ClassNotFoundException(name);

        //  Otherwise, return class
        return clas;
    }
}
