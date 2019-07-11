package com.example.messaging.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParameterResolver {
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("(\\{[a-zA-Z]+\\})");
    private final List<String> parameterNames = new ArrayList<>();
    private final Pattern pattern;

    public ParameterResolver(final String parameterTemplate) {

        final Matcher matcher = PARAMETER_PATTERN.matcher(parameterTemplate);

        while (matcher.find()) {
            if(matcher.groupCount()==1) {
                final String group = matcher.group(1);
                if(group.length()>2) {
                    parameterNames.add(group.substring(1, group.length() - 1));
                } else {
                    parameterNames.add(group);
                }
            }
        }
        pattern = Pattern.compile(Pattern.quote(matcher.replaceAll("_____PARAM_____")).replace("_____PARAM_____", "\\E([^/]*)\\Q"));

    }

    public Map<String, String> parametersByName(final String uriString) {
        final Matcher matcher = pattern.matcher(uriString);
        if(!matcher.matches()) {
            return new HashMap<>();
        }
        final Map<String, String> map = new HashMap<>();
        for(int i = 1;i<=matcher.groupCount();i++) {
            map.put(parameterNames.get(i-1), matcher.group(i));
        }
        return map;

    }

}
