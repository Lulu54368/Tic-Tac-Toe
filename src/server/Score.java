package server;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lulu
 */
public class Score {
    private static final Map<String, Integer> scoreMap = new LinkedHashMap<>();

    public static void createNew(String username) {
        scoreMap.putIfAbsent(username, 0);
    }

    public static void win(String username) {
        scoreMap.put(username, scoreMap.getOrDefault(username, 0) + 5);
    }

    public static void lose(String username) {
        scoreMap.put(username, Math.max(scoreMap.get(username) - 5, 0));
    }

    public static void draw(String username) {
        scoreMap.put(username, scoreMap.get(username) + 2);
    }

    public static int getRank(String username) {
        System.out.println("The score for the players");
        scoreMap.entrySet().stream().forEach(e -> System.out.print(e.getKey() + " " + e.getValue()));
        System.out.println();
        return scoreMap
                .entrySet()
                .stream()
                .sorted((Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) ->
                        o2.getValue() - o1.getValue()
                )
                .map(e -> e.getKey())
                .collect(Collectors.toList())
                .indexOf(username) + 1;
    }
}
