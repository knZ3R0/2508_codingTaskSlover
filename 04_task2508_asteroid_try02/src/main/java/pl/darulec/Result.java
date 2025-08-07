package pl.darulec;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Result {

    // Reusable HTTP client (Java 11)
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void main(String[] args) {
        // Use args if provided: args[0] = year, args[1] = pha ("Y" or "N")
        int year = 2011;
        String pha = "Y";
        if (args != null && args.length >= 2) {
            try {
                year = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
                // leave default
            }
            pha = args[1];
        }

        List<String> result = asteroidMonitor(year, pha);
        for (String d : result) {
            System.out.println(d);
        }
    }

    /*
     * Complete the 'asteroidMonitor' function below.
     *
     * The function is expected to return a STRING_ARRAY.
     *
     * The function accepts following parameters:
     * 1. INTEGER year
     * 2. STRING pha
     *
     * API URL: https://jsonmock.hackerrank.com/api/asteroids/search?parameter={keyword}&page={page_no}
     * Note: We enumerate full dataset using base endpoint pagination:
     *       https://jsonmock.hackerrank.com/api/asteroids?page={page_no}
     */
    public static List<String> asteroidMonitor(int year, String pha) {
        List<String> out = new ArrayList<>();

        // Validate pha input early (strict Y/N, case-insensitive)
        if (pha == null) return out;
        String phaNorm = pha.trim().toUpperCase();
        if (!"Y".equals(phaNorm) && !"N".equals(phaNorm)) return out;

        try {
            // Fetch page 1 to read total_pages
            String firstUrl = "https://jsonmock.hackerrank.com/api/asteroids?page=1";
            String body1 = httpGet(firstUrl);
            if (body1 == null || body1.isEmpty()) {
                return out; // fail-safe: empty
            }

            int totalPages = parseTotalPages(body1);
            if (totalPages <= 0) {
                return out; // nothing to do
            }

            List<Map.Entry<Double, String>> acc = new ArrayList<>();

            for (int p = 1; p <= totalPages; p++) {
                String json = (p == 1) ? body1 : httpGet("https://jsonmock.hackerrank.com/api/asteroids?page=" + p);
                if (json == null || json.isEmpty()) {
                    continue; // skip this page
                }

                String dataSlice = extractDataArraySlice(json);
                if (dataSlice == null || dataSlice.isEmpty()) {
                    continue;
                }

                List<String> objects = extractObjectsFromArraySlice(dataSlice);
                for (String obj : objects) {
                    String designation = extractStringField(obj, "designation");
                    String discoveryDate = extractStringField(obj, "discovery_date");
                    String phaVal = extractStringField(obj, "pha");

                    if (designation == null || discoveryDate == null || phaVal == null) continue;

                    // Filter PHA (case-insensitive strict Y/N)
                    if (!phaNorm.equals(phaVal.trim().toUpperCase())) continue;

                    // Filter by discovery year
                    Integer discYear = extractYear(discoveryDate);
                    if (discYear == null || discYear != year) continue;

                    // period_yr default to 0 on missing/non-numeric
                    String periodStr = extractStringField(obj, "period_yr");
                    double period = parseDoubleSafe(periodStr);

                    acc.add(new AbstractMap.SimpleEntry<>(period, designation));
                }
            }

            // Sort by numeric period_yr asc, then designation asc
            acc.sort(Comparator.comparing(Map.Entry<Double, String>::getKey)
                    .thenComparing(Map.Entry<Double, String>::getValue));

            for (Map.Entry<Double, String> e : acc) {
                out.add(e.getValue());
            }
            return out;

        } catch (Exception e) {
            // Fail-safe: per task constraints, return empty list on errors
            return out;
        }
    }

    // HTTP GET utility: returns body or null on non-200/exception
    private static String httpGet(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(20))
                    .GET()
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (IOException | InterruptedException ignored) {
            // fall through to null
        }
        return null;
    }

    // Parse total_pages from top-level JSON using regex
    private static int parseTotalPages(String json) {
        if (json == null) return 0;
        Matcher m = Pattern.compile("\"total_pages\"\\s*:\\s*(\\d+)").matcher(json);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }

    // Extract the substring containing the data array: content between first '[' after "data"
    private static String extractDataArraySlice(String json) {
        if (json == null) return null;
        int dataIdx = json.indexOf("\"data\"");
        if (dataIdx < 0) return null;
        int bracketStart = json.indexOf('[', dataIdx);
        if (bracketStart < 0) return null;

        boolean inString = false;
        char prev = 0;
        int depth = 0;
        for (int i = bracketStart; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '"' && prev != '\\') {
                inString = !inString;
            }
            if (!inString) {
                if (c == '[') depth++;
                else if (c == ']') {
                    depth--;
                    if (depth == 0) {
                        // include both brackets
                        return json.substring(bracketStart, i + 1);
                    }
                }
            }
            prev = c;
        }
        return null;
        }
    // From the array slice "[ { ... }, { ... }, ... ]", extract each object as a string
    private static List<String> extractObjectsFromArraySlice(String arraySlice) {
        List<String> objs = new ArrayList<>();
        if (arraySlice == null || arraySlice.length() < 2) return objs;

        // Skip initial '[' and trailing ']'
        int i = 1;
        int end = arraySlice.length() - 1;
        boolean inString = false;
        char prev = 0;
        int braceDepth = 0;
        int objStart = -1;

        while (i < end) {
            char c = arraySlice.charAt(i);
            if (c == '"' && prev != '\\') {
                inString = !inString;
            }
            if (!inString) {
                if (c == '{') {
                    if (braceDepth == 0) objStart = i;
                    braceDepth++;
                } else if (c == '}') {
                    braceDepth--;
                    if (braceDepth == 0 && objStart >= 0) {
                        objs.add(arraySlice.substring(objStart, i + 1));
                        objStart = -1;
                    }
                }
            }
            prev = c;
            i++;
        }
        return objs;
    }

    // Extract string field value from a flat JSON object string using regex
    private static String extractStringField(String obj, String field) {
        if (obj == null) return null;
        Pattern p = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*\"(.*?)\"");
        Matcher m = p.matcher(obj);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    // Extract first 4-digit year from a date string like "YYYY-MM-DD"
    private static Integer extractYear(String discoveryDate) {
        if (discoveryDate == null || discoveryDate.length() < 4) return null;
        // Prefer first 4 chars if structured as ISO date
        String prefix = discoveryDate.substring(0, Math.min(4, discoveryDate.length()));
        try {
            return Integer.parseInt(prefix);
        } catch (NumberFormatException ignored) {
            // fallback: find first 4 consecutive digits
            Matcher m = Pattern.compile("(\\d{4})").matcher(discoveryDate);
            if (m.find()) {
                try {
                    return Integer.parseInt(m.group(1));
                } catch (NumberFormatException ignored2) {
                    return null;
                }
            }
            return null;
        }
    }

    // Parse double safely; returns 0 on missing/non-numeric as per task
    private static double parseDoubleSafe(String s) {
        if (s == null) return 0.0;
        String t = s.trim();
        if (t.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(t);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
