package client.model.commandPattern;


import java.util.ArrayList;
import java.util.List;

public class CommandUtil {
    public static String[] tokenizerArgs(String args) {
        List<String> tokens = new ArrayList<String>();
        char[] charArray = args.toCharArray();
        StringBuilder contact = new StringBuilder();
        boolean inText = false;

        for (char c : charArray) {
            if (c == ' ' && !inText) {
                if (contact.length() != 0) {
                    tokens.add(contact.toString());
                    contact = new StringBuilder();
                }
            } else if (c == '"') {
                if (inText) {
                    tokens.add(contact.toString());
                    contact = new StringBuilder();
                    inText = false;
                } else {
                    inText = true;
                }
            } else {
                contact.append(c);
            }
        }
        if (contact.toString().trim().length() != 0) {
            tokens.add(contact.toString().trim());
        }

        String[] argsArray = new String[tokens.size()];
        argsArray = tokens.toArray(argsArray);
        return argsArray;
    }

//    public static void main(String[] args) {
//        String commanda = "file -an c:/dummy/dummy.txt \"Hola mundo tres veces\"";
//        String[] tokens = CommandUtil.tokenizerArgs(commanda);
//        System.out.println(Arrays.toString(tokens));
//    }
}
