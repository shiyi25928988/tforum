package cc.shiyi.coleditor.common.ai.utils;

import java.util.Random;

public class ConversationIdGenerator {
    private static final Random random = new Random();
    private static final String PREFIX = "conv_";
    public static String generateNewId() {
         return PREFIX + System.currentTimeMillis() + "_" + generateEightDigitRandomNumber();
    }
    private static String generateEightDigitRandomNumber() {
        int number = random.nextInt(90000000) + 10000000;
        return String.valueOf(number);
    }
}
