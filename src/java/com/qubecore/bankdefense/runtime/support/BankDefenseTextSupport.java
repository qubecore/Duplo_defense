package com.qubecore.bankdefense.runtime.support;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class BankDefenseTextSupport {
    private static final Charset WINDOWS_1251 = Charset.forName("windows-1251");
    private static final Charset IBM866 = Charset.forName("IBM866");
    private static final String SAFE_PUNCTUATION = ",.!?:;+-/%()[]{}<>`\"'=#_|";
    private static final int CLEAN_CACHE_LIMIT = 4096;
    private static final Map<String, String> CLEAN_CACHE = Collections.synchronizedMap(
        new LinkedHashMap<String, String>(512, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return this.size() > CLEAN_CACHE_LIMIT;
            }
        }
    );

    private BankDefenseTextSupport() {
    }

    public static String clean(String value) {
        if (value == null) {
            return "";
        }
        String cached = CLEAN_CACHE.get(value);
        if (cached != null) {
            return cached;
        }
        if (value.isBlank()) {
            CLEAN_CACHE.put(value, "");
            return "";
        }
        if (canUseFastPath(value)) {
            String trimmed = value.trim();
            CLEAN_CACHE.put(value, trimmed);
            return trimmed;
        }
        StringBuilder cleaned = new StringBuilder();
        value.codePoints()
            .filter(codePoint -> codePoint != 0 && codePoint != 0xFEFF)
            .filter(codePoint -> Character.getType(codePoint) != Character.FORMAT)
            .forEach(cleaned::appendCodePoint);

        String current = normalizeHumanText(cleaned.toString().replace('\u0000', ' '));
        for (int i = 0; i < 6; i++) {
            if (!looksBrokenCyrillic(current)) {
                break;
            }
            String repaired = normalizeHumanText(repairBrokenCyrillic(current));
            if (repaired.equals(current)) {
                break;
            }
            current = repaired;
        }
        String normalized = normalizeHumanText(current);
        CLEAN_CACHE.put(value, normalized);
        return normalized;
    }

    private static boolean canUseFastPath(String value) {
        boolean previousSpace = false;
        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);
            int code = current;
            if (code == 0 || code == 0xFEFF || current == '\u00A0') {
                return false;
            }
            if (Character.getType(current) == Character.FORMAT) {
                return false;
            }
            if (current == '\t' || current == '\n' || current == '\r' || current == '\f' || current == 0x000B) {
                return false;
            }
            if (((code >= 0x0400 && code <= 0x040F) || (code >= 0x0450 && code <= 0x045F))
                && code != 0x0401
                && code != 0x0451) {
                return false;
            }
            if ((code >= 0x0080 && code <= 0x009F)
                || code == 0x00D0
                || code == 0x00D1
                || code == 0x00C2
                || code == 0xFFFD
                || current == '?') {
                return false;
            }
            if (current == ' ') {
                if (previousSpace) {
                    return false;
                }
                previousSpace = true;
            } else {
                previousSpace = false;
            }
            if (index + 1 >= value.length()) {
                continue;
            }
            char next = value.charAt(index + 1);
            if ((Character.isLetter(current) && Character.isDigit(next))
                || (Character.isDigit(current) && Character.isLetter(next))) {
                return false;
            }
            if (current == ' ' && ",.;:!?%)\\]}".indexOf(next) >= 0) {
                return false;
            }
            if ("([{".indexOf(current) >= 0 && next == ' ') {
                return false;
            }
        }
        return true;
    }

    private static String repairBrokenCyrillic(String value) {
        String best = value == null ? "" : value.trim();
        String cp1251 = tryDecode(value, WINDOWS_1251);
        String latin1 = tryDecode(value, StandardCharsets.ISO_8859_1);
        best = betterRepair(best, cp1251);
        best = betterRepair(best, tryDecode(cp1251, WINDOWS_1251));
        best = betterRepair(best, latin1);
        best = betterRepair(best, tryDecode(latin1, WINDOWS_1251));
        best = betterRepair(best, tryDecode(value, IBM866));
        return best;
    }

    private static String tryDecode(String value, Charset charset) {
        if (value == null || value.isBlank()) {
            return "";
        }
        try {
            return new String(value.getBytes(charset), StandardCharsets.UTF_8).trim();
        } catch (RuntimeException ignored) {
            return value.trim();
        }
    }

    private static String betterRepair(String currentBest, String candidate) {
        if (candidate == null || candidate.isBlank()) {
            return currentBest;
        }
        int candidateSuspicious = suspiciousScore(candidate);
        int currentSuspicious = suspiciousScore(currentBest);
        if (candidateSuspicious < currentSuspicious) {
            return candidate;
        }
        if (candidateSuspicious > currentSuspicious) {
            return currentBest;
        }
        return repairScore(candidate) > repairScore(currentBest) ? candidate : currentBest;
    }

    private static int repairScore(String value) {
        if (value == null || value.isBlank()) {
            return Integer.MIN_VALUE;
        }
        int score = 0;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            int code = c;
            if ((code >= 0x0410 && code <= 0x044F) || code == 0x0401 || code == 0x0451) {
                score += 4;
            } else if (Character.isLetterOrDigit(c)) {
                score += 1;
            } else if (Character.isWhitespace(c) || SAFE_PUNCTUATION.indexOf(c) >= 0) {
                score += 1;
            }
        }
        return score - suspiciousScore(value) * 5;
    }

    private static int suspiciousScore(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }

        int score = 0;
        int questionRun = 0;
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            int code = current;

            if (((code >= 0x0400 && code <= 0x040F) || (code >= 0x0450 && code <= 0x045F))
                && code != 0x0401
                && code != 0x0451) {
                score += 18;
            }
            if ((code >= 0x0080 && code <= 0x009F) || code == 0x00D0 || code == 0x00D1 || code == 0x00C2 || code == 0xFFFD) {
                score += 25;
            }

            if (current == '?') {
                questionRun++;
                score += 14;
                boolean touchesLetters =
                    (i > 0 && Character.isLetter(value.charAt(i - 1)))
                        || (i < value.length() - 1 && Character.isLetter(value.charAt(i + 1)));
                if (touchesLetters) {
                    score += 28;
                }
            } else {
                if (questionRun >= 2) {
                    score += 15;
                }
                questionRun = 0;
            }

            if (i < value.length() - 1) {
                int nextCode = value.charAt(i + 1);
                if ((code == 0x0420 || code == 0x0421)
                    && ((nextCode >= 0x0410 && nextCode <= 0x044F) || nextCode == 0x0401 || nextCode == 0x0451)) {
                    score += 8;
                }
                if (code == 0x00D0 || code == 0x00D1) {
                    score += 8;
                }
            }
        }

        if (questionRun >= 2) {
            score += 15;
        }
        return score;
    }

    private static String normalizeHumanText(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String normalized = value.replace('\u00A0', ' ').trim();
        normalized = normalized.replaceAll("[\\t\\x0B\\f\\r ]+", " ");
        normalized = normalized.replaceAll(" *\\n *", "\n");
        normalized = normalized.replaceAll("([\\p{L}])(\\d)", "$1 $2");
        normalized = normalized.replaceAll("(\\d)([\\p{L}])", "$1 $2");
        normalized = normalized.replaceAll(" +([,.;:!?%)\\]}])", "$1");
        normalized = normalized.replaceAll("([\\[\\(\\{]) +", "$1");
        normalized = normalized.replaceAll(" {2,}", " ");
        normalized = normalized.replaceAll("\\n{3,}", "\n\n");
        return normalized.trim();
    }

    public static boolean looksBrokenCyrillic(String value) {
        return suspiciousScore(value) >= 20;
    }
}
