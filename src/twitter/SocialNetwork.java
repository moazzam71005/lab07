package twitter;

import java.util.*;

public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> followsGraph = new HashMap<>();

        for (Tweet tweet : tweets) {
            String author = tweet.getAuthor().toLowerCase();
            Set<String> mentionedUsers = Extract.getMentionedUsers(List.of(tweet));
            Set<String> validMentions = new HashSet<>();

            for (String user : mentionedUsers) {
                String lowerUser = user.toLowerCase();
                if (!lowerUser.equals(author)) {
                    validMentions.add(lowerUser);
                }
            }

            if (!validMentions.isEmpty()) {
                followsGraph.putIfAbsent(author, new HashSet<>());
                followsGraph.get(author).addAll(validMentions);
            }
        }

        return followsGraph;
    }

    /**
     * Find the people in a social network who have the greatest influence.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        Map<String, Integer> followerCount = new HashMap<>();

        for (String user : followsGraph.keySet()) {
            followerCount.putIfAbsent(user, 0);
            for (String followed : followsGraph.get(user)) {
                followerCount.put(followed, followerCount.getOrDefault(followed, 0) + 1);
            }
        }

        List<String> influencers = new ArrayList<>(followerCount.keySet());
        influencers.sort((a, b) -> followerCount.get(b).compareTo(followerCount.get(a)));

        return influencers;
    }
}
