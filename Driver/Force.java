package Driver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Force{
    private static boolean hadError=false;

    public static void main(String[] args) throws IOException {
        if(args.length>1){
            System.out.println("Usage force [script]");
        }else if(args.length==1){
            //run from file stuff
        }else{
            //run from prompt stuff
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException{
        byte[] bytes= Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if(hadError) System.exit(65);
    }

    private static void run(String program){
        //make new scanner
        System.out.println("run is running");
        scanner.Scanner scanner=new scanner.Scanner(program);
        List<scanner.Token> tokens=scanner.scanTokens();
        for (scanner.Token t :
                tokens) {
            System.out.println(t.toString());
        }
    }

    private static void runPrompt() throws IOException{
        InputStreamReader inputStreamReader=new InputStreamReader(System.in);
        BufferedReader reader=new BufferedReader(inputStreamReader);
        for(;;){
            System.out.print("> ");
            String line=reader.readLine();
            if(line==null){
                break;
            }
            run(line);
            hadError=false;
        }
    }

    public static void error(int line,String message){
        reportError(line,"[where part]",message);
        hadError=true;
    }


    private static void reportError(int line,String where, String message){
        System.out.println("Boy you have error :"+message+" at line : "+line+ " "+where);
    }
}