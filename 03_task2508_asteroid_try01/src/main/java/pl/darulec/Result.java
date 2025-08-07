package pl.darulec;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Result {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    /*
     * Complete the 'asteroidMonitor' function below.
     *
     * The function is expected to return a STRING_ARRAY.
     *
     * The function accepts following parameters:
     * 1. INTEGER year
     * 2. STRING pha
     *
     * API URL (per task): https://jsonmock.hackerrank.com/api/asteroids/search?parameter={keyword}&page={page_no}
     *
     * Assumptions (per docs/task.md and conservative approach per workflow P8):
     * - Use only standard Java 11 libraries (no extra JSON deps). We manually parse the minimal JSON fields we need.
     * - To avoid relying on ambiguous /search semantics, we fetch all pages from /api/asteroids and filter locally by year and pha.
     * - If period_yr is missing or non-numeric, treat as 0.0 for sorting.
     */
    public static List<String> asteroidMonitor(int year, String pha) {
        if (pha == null) pha = "";
        final String phaQuery = pha.trim();

        // Collect pairs [designation, period] without defining extra classes (single-class constraint).
        List<Object[]> matches = new ArrayList<>();

        int page = 1;
        int totalPages = 1;

        try {
            do {
                String url = "https://jsonmock.hackerrank.com/api/asteroids?page=" + page;
                HttpRequest req = HttpRequest.newBuilder()
                        .GET()
                        .uri(URI.create(url))
                        .build();

                HttpResponse<String> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
                if (resp.statusCode() != 200 || resp.body() == null) {
                    // Fail-fast-ish: stop further paging, return what we have.
                    break;
                }
                String body = resp.body();

                Integer tp = getIntField(body, "total_pages");
                if (tp != null) {
                    totalPages = tp;
                }

                for (String obj : iterateDataObjects(body)) {
                    String designation = getStringField(obj, "designation");
                    String discoveryDate = getStringField(obj, "discovery_date");
                    String phaVal = getStringField(obj, "pha");
                    String periodStr = getFieldAsStringFlexible(obj, "period_yr");

                    if (designation == null || discoveryDate == null || phaVal == null) {
                        continue;
                    }
                    if (!phaVal.equalsIgnoreCase(phaQuery)) {
                        continue;
                    }

                    Integer y = extractYear(discoveryDate);
                    if (y == null || y != year) {
                        continue;
                    }

                    double period = parseDoubleSafe(periodStr, 0.0);
                    matches.add(new Object[]{designation, period});
                }

                page++;
            } while (page <= totalPages);
        } catch (Exception e) {
            // Conservative behavior per workflow P8: stop and return whatever we gathered so far.
        }

        // Sort ascending by period_yr, then designation.
        matches.sort((a, b) -> {
            double pa = (double) a[1];
            double pb = (double) b[1];
            if (pa < pb) return -1;
            if (pa > pb) return 1;
            String da = (String) a[0];
            String db = (String) b[0];
            return da.compareTo(db);
        });

        List<String> result = new ArrayList<>(matches.size());
        for (Object[] m : matches) {
            result.add((String) m[0]);
        }
        return result;
    }

    // ========== Minimal JSON helpers (no external deps) ==========

    private static Integer getIntField(String json, String field) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*(\\d+)");
        Matcher m = p.matcher(json);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            } catch (NumberFormatException ignore) {
            }
        }
        return null;
    }

    private static String getStringField(String obj, String field) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*\"(.*?)\"");
        Matcher m = p.matcher(obj);
        if (m.find()) {
            return unescape(m.group(1));
        }
        return null;
    }

    private static String getFieldAsStringFlexible(String obj, String field) {
        // Try quoted string value
        Pattern p1 = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*\"(.*?)\"");
        Matcher m1 = p1.matcher(obj);
        if (m1.find()) {
            return unescape(m1.group(1));
        }
        // Try numeric (int or decimal) unquoted
        Pattern p2 = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)");
        Matcher m2 = p2.matcher(obj);
        if (m2.find()) {
            return m2.group(1);
        }
        // Try explicit null
        Pattern p3 = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*null");
        if (p3.matcher(obj).find()) {
            return null;
        }
        return null;
    }

    private static String unescape(String s) {
        // Minimal unescape for quotes and backslashes often used in simple payloads
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private static Iterable<String> iterateDataObjects(String json) {
        int dataIdx = json.indexOf("\"data\":");
        if (dataIdx < 0) return Collections.emptyList();
        int arrStart = json.indexOf('[', dataIdx);
        if (arrStart < 0) return Collections.emptyList();

        int i = arrStart + 1;
        int n = json.length();
        int depth = 0;
        int objStart = -1;
        List<String> out = new ArrayList<>();

        while (i < n) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0) objStart = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && objStart >= 0) {
                    out.add(json.substring(objStart, i + 1));
                    objStart = -1;
                }
            } else if (c == ']' && depth == 0) {
                break;
            }
            i++;
        }
        return out;
    }

    private static Integer extractYear(String discoveryDate) {
        // Supports "YYYY-MM-DD" or "YYYY"
        if (discoveryDate == null) return null;
        String s = discoveryDate.trim();
        if (s.length() >= 4) {
            String yearPart = s.substring(0, 4);
            try {
                return Integer.parseInt(yearPart);
            } catch (NumberFormatException ignore) {
            }
        }
        return null;
    }

    private static double parseDoubleSafe(String s, double def) {
        if (s == null || s.isEmpty()) return def;
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    // Simple main for ad-hoc runs; not required by the task.
    public static void main(String[] args) {
        List<String> asteroids =  asteroidMonitor(2011, "Y");
        for(String a: asteroids ) {
            System.out.println(a);
        }
        
    }
}
