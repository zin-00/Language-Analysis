package main;

import java.io.*;
import java.util.*;

public class ExpertSystem {
	 // Maps to store various language components and their relationships
    private static final Map<String, String> OBJECT_KEYWORDS = new HashMap<>();  // Stores object names and their classifications
    static final Map<String, Set<String>> WORD_ALIASES = new HashMap<>();       // Stores words and their synonyms/aliases
    private static final Set<String> VERBS = new HashSet<>();                   // Stores basic action words
    private static final Set<String> ARTICLES = new HashSet<>();                // Stores words like "a", "an", "the"
    private static final Set<String> PREPOSITIONS = new HashSet<>();           // Stores words that show relationships
    private static final Set<String> AUXILIARY_VERBS = new HashSet<>();        // Stores helping verbs
    private static final Set<String> SENTENCE_TERMINATORS = new HashSet<>();   // Stores sentence ending punctuation
    private static final Map<String, String> PRONOUN_REPLACEMENTS = new HashMap<>();  // Maps pronouns to their object forms
    private static final Map<String, String> INFORMAL_TO_FORMAL = new HashMap<>();    // Maps casual words to formal ones

    // Static initializer block - runs when the class is first loaded
    static {
        initializeAliases();
        initializeInformalToFormal();
        
        // Initialize auxiliary verbs (helping verbs)
        AUXILIARY_VERBS.addAll(Arrays.asList(
            "am", "is", "are", "was", "were", "be", "being", "been",
            "have", "has", "had", "do", "does", "did",
            "will", "would", "shall", "should", "may", "might",
            "must", "can", "could"
        ));
        // Initialize sentence ending punctuation
        SENTENCE_TERMINATORS.addAll(Arrays.asList(".", "!", "?"));
        
        // Initialize basic action verbs
        VERBS.addAll(Arrays.asList(
            "see", "go", "come", "give", "make", "read", "write", 
            "listen", "think", "want", "need", "know", "feel", "try", "run", "jump", "talk", "bark",
            "eat", "sleep", "play", "walk", "sit", "stand", "like", "love", "hear"
        ));
        
		 // Initialize articles (a, an, the)
        ARTICLES.addAll(Arrays.asList("a", "an", "the"));
        
		// Initialize prepositions (relationship words)
        PREPOSITIONS.addAll(Arrays.asList(
            "in", "on", "at", "to", "for", "with", "by", "from", "of", 
            "under", "over", "between", "among", "through", "behind", "beyond",
            "near", "before", "after", "during", "within", "without", "about",
            "across", "along", "around", "down", "into", "onto", "out", "up", "upon"
        ));

        PRONOUN_REPLACEMENTS.put("i", "me");
        PRONOUN_REPLACEMENTS.put("you", "you");
        PRONOUN_REPLACEMENTS.put("he", "him");
        PRONOUN_REPLACEMENTS.put("she", "her");
        PRONOUN_REPLACEMENTS.put("it", "it");
        PRONOUN_REPLACEMENTS.put("we", "us");
        PRONOUN_REPLACEMENTS.put("they", "them");

        loadKeywords("keywords.txt");
    }
    

    private static void initializeAliases() {
        addAlias("cat", Arrays.asList("kitty", "kitten", "feline"));
        addAlias("dog", Arrays.asList("pup", "puppy", "hound", "canine"));
        addAlias("bird", Arrays.asList("birdie", "fowl", "avian"));
    }


    private static void initializeInformalToFormal() {
        INFORMAL_TO_FORMAL.put("kitty", "cat");
        INFORMAL_TO_FORMAL.put("doggy", "dog");
        INFORMAL_TO_FORMAL.put("birdie", "bird");
    }
    
    // Adds aliases for a base word (e.g., "cat" -> ["kitty", "kitten", "feline"])
    private static void addAlias(String baseWord, List<String> aliases) {
        WORD_ALIASES.putIfAbsent(baseWord, new HashSet<>(aliases));
        for (String alias : aliases) {
            WORD_ALIASES.computeIfAbsent(alias, k -> new HashSet<>()).add(baseWord);
        }
    }
    
    // Loads object keywords from a specified file and processes them (singular/plural)
    private static void loadKeywords(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue; // Skip empty line

                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String singular = parts[0].trim();
                    String value = parts[1].trim();

                    // First, add the base word and its value
                    OBJECT_KEYWORDS.put(singular, value);
                    
                    // Then add its plural form
                    String plural = generatePlural(singular);
                    if (plural != null) {
                        OBJECT_KEYWORDS.put(plural, value);
                    }

                    // Process aliases for this keyword
                    processAliasesForKeyword(singular, value);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading keywords: " + e.getMessage());
        }
    }
    // Processes aliases for a given keyword and adds them to the object keywords
    private static void processAliasesForKeyword(String baseWord, String value) {
        // Get aliases for the base word
        Set<String> aliases = WORD_ALIASES.get(baseWord);
        if (aliases != null) {
            for (String alias : aliases) {
                // Add the alias to OBJECT_KEYWORDS with the same value
                OBJECT_KEYWORDS.put(alias, value);
                
                // Also add plural form of the alias
                String aliasPlural = generatePlural(alias);
                if (aliasPlural != null) {
                    OBJECT_KEYWORDS.put(aliasPlural, value);
                }
            }
        }
        
        // Check if this word is an alias for something else
        for (Map.Entry<String, Set<String>> entry : WORD_ALIASES.entrySet()) {
            if (entry.getValue().contains(baseWord)) {
                // If this word is an alias, it should have the same value as its base word
                OBJECT_KEYWORDS.put(baseWord, value);
                String plural = generatePlural(baseWord);
                if (plural != null) {
                    OBJECT_KEYWORDS.put(plural, value);
                }
            }
        }
    }
    
    // Generates the plural form of a word, handling special cases like "child" -> "children"
    private static String generatePlural(String singular) {
        if (singular == null || singular.isEmpty()) return null;

        Map<String, String> specialPlurals = Map.of(
            "mouse", "mice", "child", "children", "person", "people",
            "foot", "feet", "tooth", "teeth", "goose", "geese"
        );
        if (specialPlurals.containsKey(singular)) return specialPlurals.get(singular);

        if (Arrays.asList("fish", "sheep", "deer", "species").contains(singular)) return singular;

        if (singular.endsWith("s") || singular.endsWith("x") || singular.endsWith("z") || 
            singular.endsWith("sh") || singular.endsWith("ch")) {
            return singular + "es";
        } else if (singular.endsWith("y") && !isVowel(singular.charAt(singular.length() - 2))) {
            return singular.substring(0, singular.length() - 1) + "ies";
        } else if (singular.endsWith("f")) {
            return singular.substring(0, singular.length() - 1) + "ves";
        } else if (singular.endsWith("fe")) {
            return singular.substring(0, singular.length() - 2) + "ves";
        } else {
            return singular + "s";
        }
    }
    // Checks if a character is a vowel (for pluralization purposes)
    private static boolean isVowel(char c) {
        return "aeiou".indexOf(Character.toLowerCase(c)) != -1;
    }
    
    // Sentence analysis class that stores various components of the sentence (subject, verb, object)
    public static class SentenceAnalysis {
        private List<String> subjects = new ArrayList<>();
        private List<String> verbs = new ArrayList<>();
        private List<String> objects = new ArrayList<>();
        private boolean isSentence;
        private String mainSubject;
        private String mainObject;
        private String sentenceType;

        private Map<String, String> resolvedAliases = new HashMap<>();
        private Map<String, String> formalReplacements = new HashMap<>();

        public List<String> getSubjects() { return subjects; }
        public List<String> getVerbs() { return verbs; }
        public List<String> getObjects() { return objects; }
        public boolean isSentence() { return isSentence; }
        public String getMainSubject() { return mainSubject; }
        public String getMainObject() { return mainObject; }
        public String getSentenceType() { return sentenceType; }
        public Map<String, String> getResolvedAliases() { return resolvedAliases; }
        public Map<String, String> getFormalReplacements() { return formalReplacements; }
        private String mood;

        public String getMood() { return mood; }

     // Returns a string representation of the sentence analysis
        @Override
        public String toString() {
            if (!isSentence) return "Not a complete sentence";
            StringBuilder sb = new StringBuilder();
            sb.append("Sentence Analysis:\n");
            sb.append("Type: ").append(sentenceType).append("\n");
            if (!mood.isEmpty()) {
                sb.append("Mood: ").append(mood).append("\n");
            }
            
            if (!subjects.isEmpty()) {
                sb.append("Subject(s): ");
                appendWithKeywords(sb, subjects);
                sb.append("\n");
            }
            
            if (!verbs.isEmpty()) {
                sb.append("Verb(s): ").append(String.join(", ", verbs)).append("\n");
            }
            
            if (!objects.isEmpty()) {
                sb.append("Object(s): ");
                appendWithKeywords(sb, objects);
                sb.append("\n");
            }
            
            if (!resolvedAliases.isEmpty()) {
                sb.append("Resolved Aliases:\n");
                for (Map.Entry<String, String> entry : resolvedAliases.entrySet()) {
                    sb.append("  ").append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
                }
            }
            
            return sb.toString();
        }
        
        // Helper method to append a list of words with their corresponding keyword values to the string builder
        private void appendWithKeywords(StringBuilder sb, List<String> words) {
            for (int i = 0; i < words.size(); i++) {
                String word = words.get(i);
                sb.append(word);
                if (OBJECT_KEYWORDS.containsKey(word)) {
                    sb.append(" (").append(OBJECT_KEYWORDS.get(word)).append(")");
                }
                if (i < words.size() - 1) {
                    sb.append(", ");
                }
            }
        }
    }


    public static SentenceAnalysis analyzeSentence(String input) {
        // Create a new SentenceAnalysis object to store results
        SentenceAnalysis analysis = new SentenceAnalysis();
        
        // Tokenize the input sentence by splitting on whitespace and converting to lowercase
        List<String> tokens = Arrays.asList(input.toLowerCase().trim().split("\\s+"));
        
        // Flags to track if a subject and verb have been found in the sentence
        boolean foundSubject = false, foundVerb = false;
        
        // Loop through the tokens to process each word
        for (String token : tokens) {
            // Check if the token is an auxiliary verb or main verb
            if (AUXILIARY_VERBS.contains(token) || VERBS.contains(token)) {
                analysis.getVerbs().add(token);  // Add the verb to the list of verbs
                foundVerb = true;  // Mark that a verb has been found
            } 
            // Skip articles, prepositions, and sentence terminators (punctuation)
            else if (ARTICLES.contains(token) || PREPOSITIONS.contains(token) || 
                     SENTENCE_TERMINATORS.contains(token)) {
                continue;  // Skip these words as they are not directly useful for subject/verb/object analysis
            }
            // Check if the token is a pronoun replacement (e.g., "he" -> "John")
            else if (PRONOUN_REPLACEMENTS.containsKey(token)) {
                String replacement = PRONOUN_REPLACEMENTS.get(token);
                // If the subject has not been found yet, add the replacement as the subject
                if (!foundSubject) {
                    analysis.getSubjects().add(replacement);  // Add to subjects list
                    analysis.mainSubject = replacement;  // Set main subject
                    foundSubject = true;  // Mark that a subject has been found
                }
            } 
            // Check if the token is an object keyword or word alias
            else if (OBJECT_KEYWORDS.containsKey(token) || WORD_ALIASES.containsKey(token)) {
                // If a verb has been found, it's likely the object; add it to the objects list
                if (foundVerb) {
                    analysis.getObjects().add(token);  // Add token to objects list
                    // Resolve object alias and store it as the main object
                    analysis.mainObject = OBJECT_KEYWORDS.getOrDefault(token, token);
                    analysis.resolvedAliases.put(token, OBJECT_KEYWORDS.getOrDefault(token, token));
                } 
                // If no verb has been found, treat this as the subject
                else {
                    analysis.getSubjects().add(token);  // Add to subjects list
                    analysis.mainSubject = token;  // Set this token as the main subject
                    foundSubject = true;  // Mark that a subject has been found
                }
            } 
            // For other tokens, assume they are the subject if no subject has been found
            else {
                // If no subject has been found, this token is treated as the subject
                if (!foundSubject) {
                    analysis.getSubjects().add(token);  // Add to subjects list
                    analysis.mainSubject = token;  // Set this token as the main subject
                    foundSubject = true;  // Mark that a subject has been found
                } 
                // If a subject is found and a verb is found, this token is the object
                else if (foundVerb) {
                    analysis.getObjects().add(token);  // Add token to objects list
                    analysis.mainObject = token;  // Set this token as the main object
                }
            }
        }

        // Determine if the sentence is complete based on the presence of a subject, verb, and object
        analysis.isSentence = foundSubject && foundVerb && !analysis.getObjects().isEmpty();

        // Enhanced sentence type identification based on sentence structure and punctuation
        if (analysis.isSentence) {
            // Get the first and last tokens of the sentence to analyze sentence type
            String firstToken = tokens.get(0).toLowerCase();
            String lastToken = tokens.get(tokens.size() - 1);
            
            // Define a set of question words (e.g., "what", "how", "why")
            Set<String> questionWords = new HashSet<>(Arrays.asList(
                "what", "where", "when", "who", "whom", "whose", "which", "why", "how"
            ));

            // Check if the sentence ends with a question mark or starts with a question word
            if (lastToken.endsWith("?") || questionWords.contains(firstToken)) {
                analysis.sentenceType = "Interrogative";  // This is a question
                analysis.mood = "Questioning";  // The mood is questioning
            } 
            // Check if the sentence ends with an exclamation mark
            else if (lastToken.endsWith("!")) {
                analysis.sentenceType = "Exclamatory";  // This is an exclamation
                analysis.mood = "Emphatic";  // The mood is emphatic
            } 
            // Check if the sentence starts with a polite request (imperative mood)
            else if (firstToken.equals("please") || 
                     Arrays.asList("do", "please", "let", "would").contains(firstToken) || 
                     analysis.getVerbs().get(0).equals("would")) {
                analysis.sentenceType = "Imperative";  // This is a command/request
                analysis.mood = "Requesting";  // The mood is requesting
            } 
            // If none of the above, classify as declarative
            else {
                analysis.sentenceType = "Declarative";  // This is a statement
                analysis.mood = "Neutral";  // The mood is neutral
            }
        } else {
            // If sentence is incomplete (missing subject/verb/object), mark as incomplete
            analysis.sentenceType = "Incomplete";  // Incomplete sentence
            analysis.mood = "Undefined";  // Undefined mood due to incomplete sentence
        }

        // Return the final sentence analysis object
        return analysis;
    }
}
