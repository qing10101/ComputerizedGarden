package com.garden.system.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Minimal JSON-like parser tailored for garden-config.json structure to avoid extra dependencies.
 */
public class ConfigParser {
    private static final Pattern OBJECT_PATTERN = Pattern.compile("\\{(.*?)\\}", Pattern.DOTALL);

    public static List<Map<String, Object>> parsePlants(String json) {
        if (json == null || json.isBlank()) return List.of();
        List<Map<String, Object>> plants = new ArrayList<>();
        Matcher matcher = OBJECT_PATTERN.matcher(json);
        while (matcher.find()) {
            String obj = matcher.group(1);
            Map<String, Object> map = new HashMap<>();
            map.put("name", extractString(obj, "name"));
            map.put("type", extractString(obj, "type"));
            map.put("waterRequirement", extractInt(obj, "waterRequirement", 10));
            map.put("pests", extractStringList(obj, "pests"));
            if (map.get("name") != null) {
                plants.add(map);
            }
        }
        return plants;
    }

    private static String extractString(String obj, String key) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"").matcher(obj);
        return m.find() ? m.group(1) : null;
    }

    private static int extractInt(String obj, String key, int fallback) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*([0-9]+)").matcher(obj);
        return m.find() ? Integer.parseInt(m.group(1)) : fallback;
    }

    private static List<String> extractStringList(String obj, String key) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL).matcher(obj);
        if (!m.find()) return List.of();
        String content = m.group(1);
        String[] parts = content.split(",");
        return java.util.Arrays.stream(parts)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.replaceAll("^\"|\"$", ""))
                .collect(Collectors.toList());
    }
}
