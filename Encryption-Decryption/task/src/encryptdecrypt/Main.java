package encryptdecrypt;

import java.nio.file.*;
import java.util.Scanner;

//Begin of Crypto classes

interface Crypto {
    enum Action {
        ACTION_ENCRYPT,
        ACTION_DECRYPT
    }

    String encrypt(String text, int key);
    String decrypt(String text, int key);
}

class ShiftCrypto implements Crypto {

    @Override
    public String encrypt(String text, int key) {
        char[] chs = text.toCharArray();
        for (int i = 0; i < chs.length; i++) {
            if (chs[i] >= 'a' && chs[i] <= 'z') {
                chs[i] = (char) ('a' + (chs[i]-'a' + key) % 26);
            } else if (chs[i] >= 'A' && chs[i] <= 'Z') {
                chs[i] = (char) ('A' + (chs[i] - 'A' + key) % 26);
            }
        }

        return String.valueOf(chs);
    }

    @Override
    public String decrypt(String text, int key) {
        char[] chs = text.toCharArray();
        for (int i = 0; i < chs.length; i++) {
            if (chs[i] >= 'a' && chs[i] <= 'z') {
                chs[i] = (char) ('z' - ('z' - chs[i] + key) % 26);
            } else if (chs[i] >= 'A' && chs[i] <= 'Z') {
                chs[i] = (char) ('Z' - ('Z' - chs[i] + key) % 26);
            }
        }

        return String.valueOf(chs);
    }
}

class UnicodeCrypto implements Crypto {

    @Override
    public String encrypt(String text, int key) {
        char[] chs = text.toCharArray();
        for (int i = 0; i < chs.length; i++) {
            chs[i] = (char) (chs[i] + key);
        }

        return String.valueOf(chs);
    }

    @Override
    public String decrypt(String text, int key) {
        char[] chs = text.toCharArray();
        for (int i = 0; i < chs.length; i++) {
            chs[i] = (char) (chs[i] - key);
        }

        return String.valueOf(chs);
    }


}

class CryptoContext {
    private Crypto method;

    public void setMethod(Crypto method) {
        this.method = method;
    }

    public String getActionResult(Crypto.Action action, String text, int key) {
        if (action == Crypto.Action.ACTION_ENCRYPT) {
            return method.encrypt(text, key);
        } else {
            return method.decrypt(text, key);
        }
    }

}

//End of Crypto classes

public class Main {

    public static void main(String[] args) {
        String mode = "enc";
        String alg = "shift";
        int key = 0;
        String text = null;
        String fileNameIn = null;
        String fileNameOut = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-") && i == args.length - 1) {
                System.out.println("Error: incorrect arguments");
                break;
            }

            switch (args[i]) {
                case "-alg":
                    alg = args[i++ + 1];
                    break;
                case "-mode":
                    mode = args[i++ + 1];
                    break;
                case "-key":
                    key = Integer.parseInt(args[i++ + 1]);
                    break;
                case "-data":
                    text = args[i++ + 1];
                    break;
                case "-in":
                    fileNameIn = args[i++ + 1];
                    break;
                case "-out":
                    fileNameOut = args[i++ + 1];
                    break;
                default:
            }
        }

        if (fileNameIn != null) {
            try {
                text = Files.readString(Path.of(fileNameIn));
            } catch (Exception e) {
                System.out.println("Error while reading file: " + e.getMessage());
            }
        }

        CryptoContext cryptoContext = new CryptoContext();
        cryptoContext.setMethod("shift".equals(alg) ? new ShiftCrypto() : new UnicodeCrypto());

        String textOut;

        if (text == null) {
            textOut = "";
            System.out.println();
        } else {
            textOut = cryptoContext.getActionResult("enc".equals(mode) ? Crypto.Action.ACTION_ENCRYPT : Crypto.Action.ACTION_DECRYPT, text, key);
        }

        if (fileNameOut != null) {
            try {
                Files.writeString(Path.of(fileNameOut), textOut);
            } catch (Exception e) {
                System.out.println("Error while writing file: " + e.getMessage());
            }
        }

        System.out.println(textOut);
    }
}
