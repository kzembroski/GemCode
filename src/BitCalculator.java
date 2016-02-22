
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Kevin Z on 2/20/2016.
 */
public class BitCalculator {

    private static final Map<Character, Integer> hexToInt = new HashMap<Character, Integer>();
    static {
        hexToInt.put('0', 0);
        hexToInt.put('1', 1);
        hexToInt.put('2', 2);
        hexToInt.put('3', 3);
        hexToInt.put('4', 4);
        hexToInt.put('5', 5);
        hexToInt.put('6', 6);
        hexToInt.put('7', 7);
        hexToInt.put('8', 8);
        hexToInt.put('9', 9);
        hexToInt.put('a', 10);
        hexToInt.put('b', 11);
        hexToInt.put('c', 12);
        hexToInt.put('d', 13);
        hexToInt.put('e', 14);
        hexToInt.put('f', 15);
    }

    private static final Map<Integer, Character> intToHex = new HashMap<Integer, Character>();
    static {
        intToHex.put(0, '0');
        intToHex.put(1, '1');
        intToHex.put(2, '2');
        intToHex .put(3, '3');
        intToHex.put(4, '4');
        intToHex.put(5, '5');
        intToHex.put(6, '6');
        intToHex.put(7, '7');
        intToHex.put(8, '8');
        intToHex.put(9, '9');
        intToHex.put(10, 'a');
        intToHex.put(11, 'b');
        intToHex.put(12, 'c');
        intToHex.put(13, 'd');
        intToHex.put(14, 'e');
        intToHex.put(15, 'f');
    }


    private static final char[] toBase64= {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
                                                'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V','W', 'X', 'Y', 'Z', 'a', 'b', 'c',
                                                'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
                                                's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
                                                '6', '7', '8', '9', '+', '/'};

    /**
     * Read a hex encoded string and output the string in Base64 encoding.
     * @param hexString
     * @return
     */
    public static String hexToBase64(String hexString) {
        if (hexString == null) throw new IllegalArgumentException();

        String base64 = "";
        for (int i = 0; i < hexString.length(); i+=3) {
            // 3 hex chars is equivalent to 2 base64 chars
            int b1 = hexToInt.get(hexString.charAt(i)) << 8;
            int b2 = -1;
            if(i+1 < hexString.length()) {
                b2 = hexToInt.get(hexString.charAt(i+1)) << 4;
            }

            //int b2 = hexToInt.get(hexString.charAt(i+1)) << 4;
            int b3 = -1;
            if (i+2 < hexString.length()) {
                b3 = hexToInt.get(hexString.charAt(i + 2));
            }

            if(b2 == -1) {
                // create 12 bit integer and get first 6 bits for first base64 char
                int c1 = b1 >> 6;
                base64 = base64 + toBase64[c1];

                base64 = base64 + "=";
            }
            else if (b3 == -1) {
                // create 12 bit integer and get first 6 bits for first base64 char
                int c1 = b1 + b2 >> 6;
                base64 = base64 + toBase64[c1];

                // Get last 6 bits and convert to base64 char
                int c2 = b1 + b2 & 0x003F;
                base64 = base64 + toBase64[c2];
                base64 = base64 + "==";
            }
            else {
                // create 12 bit integer and get first 6 bits for first base64 char
                int c1 = b1 + b2 + b3 >> 6;
                base64 = base64 + toBase64[c1];

                // Get last 6 bits and convert to base64 char
                int c2 = b1 + b2 + b3 & 0x003F;
                base64 = base64 + toBase64[c2];
            }
        }

        return base64;
    }

    /**
     *
     * @param s1
     * @param s2
     * @return
     */
    public static String fixedXOR(String s1, String s2) {
        if (s1 == null || s2 == null || s1.length() != s2.length()) throw new IllegalArgumentException();
        String output = "";
        for (int i = 0; i < s1.length(); i++) {
            int b1 = hexToInt.get(s1.charAt(i));
            int b2 = hexToInt.get(s2.charAt(i));

            output = output + intToHex.get(b1 ^ b2);
        }
        return output;
    }

    /**
     * Decode hex encoded string with xor cipher.
     * Determines decoding key by finding most frequent character encoding and xor with space char.
     * Assumes space occurs most frequently within a phrase.
     * @param hexEncoding
     * @return Outputs English text string.
     */
    public static String decode(String hexEncoding) {
        if (hexEncoding == null) throw new IllegalArgumentException();

        // Ascii 256 characters
        int[] count = new int[256];

        // Get count of encoded characters
        for(int i = 0; i < hexEncoding.length(); i+=2) {
            // get byte integer value
            int idx = (hexToInt.get(hexEncoding.charAt(i)).intValue() << 4) + hexToInt.get(hexEncoding.charAt(i+1)).intValue();
            count[idx]++;
        }

        char freq = 0x00; // most frequenct char
        int max = 0;

        // Find most frequent character from input string
        for(int i = 0; i < count.length; i++) {
            if (count[i] > max) {
                max = count[i];
                freq = (char)i;
            }
        }

        // create decode key by xor most frequent character with space char (0x20)
        char key = (char)(0x20 ^ freq);

        String output = "";
        for(int i = 0; i < hexEncoding.length(); i+=2) {
            // read each character and xor with key
            int a = (hexToInt.get(hexEncoding.charAt(i)).intValue() << 4) + hexToInt.get(hexEncoding.charAt(i+1)).intValue();
            char c = (char) (a ^ key);
            output = output + c;
        }
        return output;
    }


    /**
     * L2 loss function using dictionary of english letter probability distribution.
     * @param count
     * @return
     */
    private static double l2Loss(double[] count) {
        double[] dictionary = new double[256];
        dictionary[101] = 0.1202; // e
        dictionary[116] = 0.0910; // t
        dictionary[97] = 0.0812; // a
        dictionary[111] = 0.0768; // o
        dictionary[105] = 0.0731; // i
        dictionary[110] = 0.0695; // n
        dictionary[115] = 0.0628; // s
        dictionary[114] = 0.0602; // r
        dictionary[104] = 0.0592; // h
        dictionary[100] = 0.0432; // d
        dictionary[108] = 0.0398; // l
        dictionary[117] = 0.0288; // u
        dictionary[99] = 0.0271; // c
        dictionary[109] = 0.0261; // m
        dictionary[102] = 0.0230; // f
        dictionary[121] = 0.0211; // y
        dictionary[119] = 0.0209; // w
        dictionary[103] = 0.0203; // g
        dictionary[112] = 0.0182; // p
        dictionary[98] = 0.0149; // b
        dictionary[118] = 0.0111; // v
        dictionary[107] = 0.0069; // k
        dictionary[120] = 0.0017; // x
        dictionary[113] = 0.0011; // q
        dictionary[106] = 0.0010; // j
        dictionary[122] = 0.0007; // z

        double loss = 0;

        for(int j = 0; j < 256; j++) {
            loss = loss + Math.pow(dictionary[j] - count[j], 2);
        }

        return loss;
    }

    // attempt at l2 loss decoding
    private static String decode2(String hexEncoding) {
        if (hexEncoding == null) throw new IllegalArgumentException();
        int key = 0x20;
        double loss = Double.MAX_VALUE;

        for(int k = 0; k < 256; k++) {
            // Ascii 256 characters
            double[] count = new double[256];

            int c1 = (k & 0x00F0) >> 4;
            int c2 = k & 0x000F;

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < hexEncoding.length()/2; i++) {
                builder.append("" + intToHex.get(c1) + intToHex.get(c2));
            }

           String key_string = builder.toString();

            String posEncoding = fixedXOR(hexEncoding, key_string);
            // Get count of encoded characters
            for (int i = 0; i < hexEncoding.length(); i += 2) {
                // get byte integer value
                int idx = (hexToInt.get(posEncoding.charAt(i)).intValue() << 4) + hexToInt.get(posEncoding.charAt(i + 1)).intValue();
                count[idx]++;
            }

            for (int i = 0; i < count.length; i++) {
                count[i] = count[i] / hexEncoding.length();
            }
            double l2Loss = l2Loss(count);

            // find key with lowest l2 loss
            if(l2Loss < loss) {
                key = k;
                loss = l2Loss;
            }
        }

        String output = "";
        for(int i = 0; i < hexEncoding.length(); i+=2) {
            // read each character and xor with key
            int a = (hexToInt.get(hexEncoding.charAt(i)).intValue() << 4) + hexToInt.get(hexEncoding.charAt(i+1)).intValue();
            char c = (char) (a ^ key);
            output = output + c;
        }
        return output;
    }


    public static void main(String[] args) {

        // hex to base64 tests
        String s = "49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736"
        + "f6e6f7573206d757368726f6f6d";
        String p = "SSdtIGtpbGxpbmcgeW91ciBicmFpbiBsaWtlIGEgcG9pc29ub3VzIG11c2hyb29t";
        System.out.println("Encoding hex string: " + s + " to base64");
        System.out.println("Result... \n" + hexToBase64(s));
        System.out.println(hexToBase64(s).compareTo(p) == 0);
        System.out.println();

        String s2 = "ff666f";
        String out2 = "/2Zv";
        System.out.println("Encoding hex string: " + s2 + " to base64");
        System.out.println("Result... \n" + hexToBase64(s2));
        System.out.println(hexToBase64(s2).compareTo(out2) == 0);
        System.out.println();

        String s3 = "8f234fea10";
        String out3 = "jyNP6hA=";
        System.out.println("Encoding hex string: " + s3 + " to base64");
        System.out.println("Result... \n" + hexToBase64(s3));
        System.out.println(hexToBase64(s3).compareTo(out3) == 0);
        System.out.println();

        String s4 = "bc156f9a";
        String out4 = "vBVvmg==";
        System.out.println("Encoding hex string: " + s4 + " to base64");
        System.out.println("Result... \n" + hexToBase64(s4));
        System.out.println(hexToBase64(s4).compareTo(out4) == 0);
        System.out.println();

        // fixed xor test
        String xor_in1 = "1c0111001f010100061a024b53535009181c";
        String xor_in2 = "686974207468652062756c6c277320657965";
        String xor_out = "746865206b696420646f6e277420706c6179";

        System.out.println("XOR hex encoded strings ... \n" + xor_in1 + "\n" + " and \n" + xor_in2);
        System.out.println("Result... \n" + fixedXOR(xor_in1, xor_in2));
        System.out.println(fixedXOR(xor_in1, xor_in2).compareTo(xor_out) == 0);
        System.out.println();

        // Decode single-byte XOR cipher
        String hexEncoding = "1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736";
        System.out.println("Decoding ... ");
        System.out.println(hexEncoding);
        System.out.println("Result ... ");
        System.out.println(decode(hexEncoding));
        
    }
}
