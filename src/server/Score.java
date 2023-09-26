package server;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Score {
    private static Map<String, Integer> scoreMap = new HashMap<>();
    public static void createNew(String username){
        scoreMap.putIfAbsent(username, 0);
    }
    public static void win(String username){
        scoreMap.put(username, scoreMap.get(username)+5);
    }
    public static void lose(String username){
        scoreMap.put(username, Math.max(scoreMap.get(username)-5, 0));
    }
    public static void draw(String username){
        scoreMap.put(username, scoreMap.get(username)+2);
    }
    public static int getRank(String username){
        return scoreMap
                .entrySet()
                .stream()
                .sorted((Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) ->
                        o2.getValue()-o1.getValue()

                )
                .map(e->e.getKey())
                .collect(Collectors.toList())
                .indexOf(username)+1;
    }
}
