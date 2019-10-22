package service;

public class CRCUtil {
    public static final String POLYNOMIAL = "10001001";

    public static String xorWithPolynomial(String a) {
        if (a.length() != 8)
            throw new IllegalArgumentException("String to xor length should be 8.");
        String result = "";
        for (int i = 0; i < 8; i++) {
            if (a.charAt(i) == POLYNOMIAL.charAt(i)) {
                result += "0";
            } else {
                result += "1";
            }
        }
        return result;
    }

    public static String divideCRC(String binaryString) {
        binaryString = removeLeftZero(binaryString);
        if (binaryString.length() == 7) {
            return binaryString;
        }

        String result = "";
        while (true) {
            String buf = binaryString.substring(0, 8);
            binaryString = binaryString.substring(8);
            binaryString = xorWithPolynomial(buf) + binaryString;
            binaryString = removeLeftZero(binaryString);
            if (binaryString.length() == 7) {
                result = binaryString;
                break;
            } else if (binaryString.length() == 8) {
                result = xorWithPolynomial(binaryString).substring(1);
                break;
            }
        }
        return result;
    }

    public static String removeLeftZero(String binary) {
        if (binary.length() < 8)
            throw new IllegalArgumentException("String length should be > 7.");
        char a = binary.charAt(0);
        while (a == '0') {
            binary = binary.substring(1);
            if (binary.length() == 7) {
                break;
            }
            a = binary.charAt(0);
        }
        return binary;
    }

    public static String fixSingleError(String binary) {
        /*int shiftCounter = 0;
        String currentCRC = "";
        int currentWeight;
        while (true) {
            currentCRC = divideCRC(binary);
            currentWeight = calculateWeight(currentCRC);
            if (currentWeight <= 1) {
                String buf = binary.substring(80);
                binary = binary.substring(0, 80);
                binary = binary + xor7bit(buf, currentCRC);
                for (int i = 0; i < shiftCounter; i++) {
                    binary = shiftRight(binary);
                }
                return binary + "0";
            }

            binary = shiftLeft(binary);
            shiftCounter++;
        }*/
        for (int i = 0; i < binary.length(); i++) {
            binary = swapChar(binary, i);
            if(divideCRC(binary).equals("0000000")) {
                return binary;
            }
            else {
                binary = swapChar(binary, i);
            }
        }
        return null;
    }

    public static String swapChar(String bin, int i) {
        String buf = bin.substring(0, i);
        if (bin.charAt(i) == '0') {
            bin = bin.substring(i + 1);
            bin = buf + "1" + bin;
        } else {
            bin = bin.substring(i + 1);
            bin = buf + "0" + bin;
        }
        return bin;
    }

    private static int calculateWeight(String bin) {
        int res = 0;
        for (int i = 0; i < bin.length(); i++) {
            if (bin.charAt(i) == '1') {
                res++;
            }
        }
        return res;
    }

    public static String shiftLeft(String bin) {
        String buf = bin.substring(1);
        String symbol = bin.substring(0, 1);
        return buf + symbol;
    }

    public static String shiftRight(String bin) {
        String buf = bin.substring(0, bin.length() - 1);
        String symbol = bin.substring(bin.length() - 1);
        return symbol + buf;
    }

    private static String xor7bit(String bin1, String bin2) {
        String result = "";
        for (int i = 0; i < 7; i++) {
            if (bin1.charAt(i) == bin2.charAt(i)) {
                result += "0";
            } else {
                result += "1";
            }
        }
        return result;
    }
}
