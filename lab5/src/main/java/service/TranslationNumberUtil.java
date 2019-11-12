package service;

import java.util.ArrayList;
import java.util.List;

public class TranslationNumberUtil {
    public static String bytesToBinary(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for (int i = 0; i < Byte.SIZE * bytes.length; i++)
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }

    public static String binaryToHex(String bin) {
        List<String> numbers = new ArrayList<String>();
        while (bin.length() > 8) {
            String sub = bin.substring(8);
            String temp = "";
            for (int i = 0; i<8; i++) {
                temp = temp + bin.charAt(i);
            }
            numbers.add(temp);
            bin = sub;
        }
        numbers.add(bin);

        String result = "";
        for (String i : numbers) {
            i =  String.format("%21X", Long.parseLong(i, 2)).trim();
            result = result + " " + i + " ";
        }
        return result;
    }

    public static String intToBinary(Integer i) {
        String result = Integer.toBinaryString(i);
        int size = result.length();
        if (size < 8) {
            for (int k = 0; k < 8 - size; k++) {
                result = "0" + result;
            }
        }
        return result;
    }

    public static byte[] binaryToBytes(String s) {
        int sLen = s.length();
        byte[] toReturn = new byte[(sLen + Byte.SIZE - 1) / Byte.SIZE];
        char c;
        for (int i = 0; i < sLen; i++)
            if ((c = s.charAt(i)) == '1')
                toReturn[i / Byte.SIZE] = (byte) (toReturn[i / Byte.SIZE] | (0x80 >>> (i % Byte.SIZE)));
            else if (c != '0')
                throw new IllegalArgumentException();
        return toReturn;
    }
}