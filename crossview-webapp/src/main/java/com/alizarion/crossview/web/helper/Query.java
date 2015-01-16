package com.alizarion.crossview.web.helper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author selim@openlinux.fr.
 */
public  class Query {

    private String sentence;

    private Set<String> hashTags = new HashSet<>();

    private Set<String> userTags = new HashSet<>();

    private Set<String> words = new HashSet<>();


    public Query(final String query) {

        this.words.addAll(Arrays.asList(query.split(" ")));
        this.sentence = query;
        for(String word : this.words){
            if (word.startsWith("#")){
                this.hashTags.add(word.replace("#",""));
                sentence.replace(word,"");
            } else if(word.startsWith("@")){
                this.userTags.add(word.replace("@",""));
                sentence.replace("@"+word,"");
            }
        }
        this.words.removeAll(this.hashTags);
        this.words.removeAll(this.userTags);
    }

    public String getSentence() {
        return sentence;
    }

    public Set<String> getHashTags() {
        return hashTags;
    }

    public Set<String> getUserTags() {
        return userTags;
    }

    public Set<String> getWords() {
        return words;
    }
}
