package com.qubecore.bankdefense.runtime.support;

import java.nio.charset.StandardCharsets;

public final class BankDefenseTextSupport {
    private BankDefenseTextSupport() {
    }

    public static String clean(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder cleaned = new StringBuilder();
        value.codePoints()
            .filter(codePoint -> codePoint != 0 && codePoint != 0xFEFF)
            .filter(codePoint -> Character.getType(codePoint) != Character.FORMAT)
            .forEach(cleaned::appendCodePoint);
        String current = cleaned.toString().replace('\u0000', ' ').trim();
        for (int i = 0; i < 2; i++) {
            if (!looksBrokenCyrillic(current)) {
                break;
            }
            String repaired = new String(current.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8).trim();
            if (repaired.equals(current)) {
                break;
            }
            current = repaired;
        }
        return current;
    }

    public static boolean looksBrokenCyrillic(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return value.contains("Р°")
            || value.contains("Рµ")
            || value.contains("Рё")
            || value.contains("Рѕ")
            || value.contains("Рџ")
            || value.contains("Рљ")
            || value.contains("СЃ")
            || value.contains("С‚")
            || value.contains("СЏ")
            || value.contains("СЋ")
            || value.contains("вЂ")
            || value.contains("Ð")
            || value.contains("Ñ");
    }
}
