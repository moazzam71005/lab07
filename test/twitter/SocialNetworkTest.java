package twitter;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;
import java.time.Instant;

public class SocialNetworkTest {

    /**
     * Testing strategy:
     * 
     * guessFollowsGraph():
     *  - empty list of tweets
     *  - tweets with no mentions
     *  - single mention
     *  - multiple mentions in a tweet
     *  - multiple tweets from same user
     * 
     * influencers():
     *  - empty graph
     *  - single user with no followers
     *  - single influencer
     *  - multiple influencers (different counts)
     *  - tied influencers (same number of followers)
     */

    // 0. Ensure assertions are enabled
    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    // 1. Empty List of Tweets
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        assertTrue("Expected empty graph", followsGraph.isEmpty());
    }

    // 2. Tweets Without Mentions
    @Test
    public void testGuessFollowsGraphNoMentions() {
        Tweet tweet = new Tweet(1, "alice", "just a normal tweet", Instant.now());
        List<Tweet> tweets = Arrays.asList(tweet);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue("Expected empty graph for no mentions", followsGraph.isEmpty());
    }

    // 3. Single Mention
    @Test
    public void testSingleMention() {
        Tweet tweet = new Tweet(1, "alice", "hey @bob how are you", Instant.now());
        List<Tweet> tweets = Arrays.asList(tweet);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue(followsGraph.containsKey("alice"));
        assertTrue(followsGraph.get("alice").contains("bob"));
    }

    // 4. Multiple Mentions
    @Test
    public void testMultipleMentions() {
        Tweet tweet = new Tweet(1, "alice", "hello @bob and @charlie", Instant.now());
        List<Tweet> tweets = Arrays.asList(tweet);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertEquals(Set.of("bob", "charlie"), followsGraph.get("alice"));
    }

    // 5. Multiple Tweets from One User
    @Test
    public void testMultipleTweetsSameUser() {
        Tweet t1 = new Tweet(1, "alice", "hi @bob", Instant.now());
        Tweet t2 = new Tweet(2, "alice", "hi again @david", Instant.now());
        List<Tweet> tweets = Arrays.asList(t1, t2);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertEquals(Set.of("bob", "david"), followsGraph.get("alice"));
    }

    // 6. Empty Graph for influencers()
    @Test
    public void testInfluencersEmptyGraph() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertTrue(influencers.isEmpty());
    }

    // 7. Single User Without Followers
    @Test
    public void testSingleUserNoFollowers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", new HashSet<>());
        List<String> influencers = SocialNetwork.influencers(followsGraph);

        // No one follows anyone; so only 'alice' exists
        assertEquals(List.of("alice"), influencers);
    }

    // 8. Single Influencer
    @Test
    public void testSingleInfluencer() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", Set.of("bob"));
        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertEquals(List.of("bob", "alice"), influencers);
    }

    // 9. Multiple Influencers
    @Test
    public void testMultipleInfluencers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", Set.of("bob", "charlie"));
        followsGraph.put("bob", Set.of("charlie"));
        followsGraph.put("charlie", Set.of());
        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertEquals(List.of("charlie", "bob", "alice"), influencers);
    }

    // 10. Tied Influence
    @Test
    public void testTiedInfluencers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", Set.of("bob"));
        followsGraph.put("charlie", Set.of("david"));
        List<String> influencers = SocialNetwork.influencers(followsGraph);

        // Both bob and david should have equal influence
        assertTrue(influencers.containsAll(List.of("bob", "david", "alice", "charlie")));
    }
}
