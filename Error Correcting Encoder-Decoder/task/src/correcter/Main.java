package correcter;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class Main {
    final static String WORKING_DIR = "/Users/Dead/IdeaProjects/Error Correcting Encoder-Decoder/Error Correcting Encoder-Decoder/Task/";

    @Deprecated
    private static void encode() {

        byte[] inputData = null;
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(WORKING_DIR + "send.txt"))) {
            inputData = bis.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (inputData == null) {
            return;
        }

        StringBuilder binaryTripleString = new StringBuilder();
        int sum = 0;
        int n = 0;
        for (byte b : inputData) {
            //make triple parity bits from byte
            StringBuilder binaryStringBuilder = new StringBuilder(Integer.toBinaryString(b));
            if (binaryStringBuilder.length() < 8) {
                binaryStringBuilder.insert(0, "0".repeat(8 - binaryStringBuilder.length()));
            }
            for (char c : binaryStringBuilder.toString().toCharArray()) {
                binaryTripleString.append(c);
                binaryTripleString.append(c);
                if (c == '1') {
                    sum ^= 1;
                }

                if (++n % 3 == 0) {
                    binaryTripleString.append(sum);
                    binaryTripleString.append(sum);
                    sum = 0;
                }
            }

            if (n % 3 == 0 && n % 8 > 0) {
                binaryTripleString.append(sum);
                binaryTripleString.append(sum);
                sum = 0;
            }
        }

        if (n % 3 != 0) {
            binaryTripleString.append("00".repeat(3 - n % 3));
            binaryTripleString.append(sum);
            binaryTripleString.append(sum);
        }
        if (binaryTripleString.length() % 8 > 0) {
            binaryTripleString.append("0".repeat(8 - binaryTripleString.length() % 8));
        }

        int bytesCount = binaryTripleString.length() / 8;
        byte[] encodedData = new byte[bytesCount];
        for (int i = 0; i < bytesCount; i++) {
            encodedData[i] = (byte)Integer.parseInt(binaryTripleString.substring(i * 8, (i + 1) * 8), 2);
        }

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(WORKING_DIR + "encoded.txt"))) {
            bos.write(encodedData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    private static void send() {
        byte[] data = null;
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(WORKING_DIR + "encoded.txt"))) {
            data = bis.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (data == null) {
            return;
        }

        Random random = new Random();
        for (int i = 0; i < data.length; i++) {
            data[i] ^= 1 << random.nextInt(8);
        }

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(WORKING_DIR + "received.txt"))) {
            bos.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    private static void decode() {
        byte[] data = null;
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(WORKING_DIR + "received.txt"))) {
            data = bis.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (data == null) {
            return;
        }

        StringBuilder resultingBinString = new StringBuilder();
        for (byte b : data) {
            int[] bits = new int[4];
            int badBit = 0;

            for (int i = 3; i >= 0; i--) {
                int b1 = b & 1;
                b >>= 1;
                int b2 = b & 1;
                b >>= 1;

                if (b1 != b2) {
                    badBit = i;
                }
                bits[i] = b1;
            }

            //Error if bad bit = 0, 1, 2
            if (badBit < 3) {
                int sum = 0;
                for (int i = 0; i < 3; i++) {
                    if (i != badBit) {
                        sum ^= bits[i];
                    }
                }

                bits[badBit] = sum ^ bits[3];
            }

            resultingBinString.append(bits[0]);
            resultingBinString.append(bits[1]);
            resultingBinString.append(bits[2]);
        }

        if (resultingBinString.length() % 8 != 0) {
            resultingBinString.append("0".repeat(8 - resultingBinString.length() % 8));
        }
        int bytesCount = resultingBinString.length() / 8;
        if ("00000000".equals(resultingBinString.substring(resultingBinString.length() - 8, resultingBinString.length()))) {
            bytesCount--;
        }
        byte[] encodedData = new byte[bytesCount];
        for (int i = 0; i < bytesCount; i++) {
            encodedData[i] = (byte)Integer.parseInt(resultingBinString.substring(i * 8, (i + 1) * 8), 2);
        }

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(WORKING_DIR + "decoded.txt"))) {
            bos.write(encodedData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getBitFromByte(byte data, int numOfBit) {
        return data >> 8 - numOfBit & 1;
    }

    private static byte setBitToByte(byte data, int numOfBit, int bitValue) {
        return bitValue != getBitFromByte(data, numOfBit) ? (byte) (data ^ 1 << 8 - numOfBit) : data;
    }

    private static byte encodeHammingByte(byte data) {
        //1, 2, 4 bit is PARITY
        //3, 5, 6, 7 bit is data

        byte result = (byte) (data << 1);
        result = setBitToByte(result, 3, getBitFromByte(result, 4));
        result = setBitToByte(result, 1, getBitFromByte(result, 3) ^ getBitFromByte(result, 5) ^ getBitFromByte(result, 7));
        result = setBitToByte(result, 2, getBitFromByte(result, 3) ^ getBitFromByte(result, 6) ^ getBitFromByte(result, 7));
        result = setBitToByte(result, 4, getBitFromByte(result, 5) ^ getBitFromByte(result, 6) ^ getBitFromByte(result, 7));

        return result;
    }

    private static byte decodeHammingByte(byte data) {
        //1, 2, 4 bit is PARITY
        //3, 5, 6, 7 bit is data

        int e1 = getBitFromByte(data, 1) ^ getBitFromByte(data, 3) ^ getBitFromByte(data, 5) ^ getBitFromByte(data, 7);
        int e2 = getBitFromByte(data, 2) ^ getBitFromByte(data, 3) ^ getBitFromByte(data, 6) ^ getBitFromByte(data, 7);
        int e4 = getBitFromByte(data, 4) ^ getBitFromByte(data, 5) ^ getBitFromByte(data, 6) ^ getBitFromByte(data, 7);

        int errorBit = (e4 << 2) | (e2 << 1) | e1;

        byte result;
        if (errorBit > 0 && errorBit < 8) {
            result = setBitToByte(data, errorBit, 1 - getBitFromByte(data, errorBit));
        } else {
            result = data;
        }

        return (byte) ((setBitToByte(result, 4, getBitFromByte(result, 3)) >> 1) & 0b1111);
    }

    private static void encodeHamming() {
        byte[] inputData = null;
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(WORKING_DIR + "send.txt"))) {
            inputData = bis.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (inputData == null) {
            return;
        }

        byte[] encodedData = new byte[inputData.length * 2];

        int i = 0;
        for (byte b: inputData) {
            encodedData[i] = encodeHammingByte((byte) (b >> 4));
            encodedData[i + 1] = encodeHammingByte((byte) (b & 0b1111));
            i += 2;
        }

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(WORKING_DIR + "encoded.txt"))) {
            bos.write(encodedData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendHamming() {
        byte[] data = null;
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(WORKING_DIR + "encoded.txt"))) {
            data = bis.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (data == null) {
            return;
        }

        Random random = new Random();
        for (int i = 0; i < data.length; i++) {
            data[i] ^= 0b10 << random.nextInt(7);
        }

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(WORKING_DIR + "received.txt"))) {
            bos.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void decodeHamming() {
        byte[] data = null;
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(WORKING_DIR + "received.txt"))) {
            data = bis.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (data == null) {
            return;
        }

        byte[] decodedData = new byte[data.length / 2];
        for (int i = 0; i < data.length; i += 2) {
            decodedData[i / 2] = (byte) ((decodeHammingByte(data[i]) << 4) | decodeHammingByte(data[i + 1]));
        }

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(WORKING_DIR + "decoded.txt"))) {
            bos.write(decodedData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Write a mode: ");
        String mode = scanner.nextLine();
        switch (mode.toUpperCase()) {
            case "ENCODE":
                encodeHamming();
                break;
            case "SEND":
                sendHamming();
                break;
            case "DECODE":
                decodeHamming();
                break;
            default:
                System.out.println("Incorrect mode!");
        }
    }
}
