package edu.ucla.cs.sourcecodes;

import java.util.List;

/**
 * Created by Jimmy on 5/12/2016.
 */
public class SessionData {
    //get and set
    private String sessionName;
    private List<String> wordList;

    public void setSessionName(String name)
    {
        sessionName = name;
    }

    public String getSessionName()
    {
        return sessionName;
    }

    public void setWordList(List<String> list)
    {
        wordList = list;
    }

    public List<String> getWordList()
    {
        return wordList;
    }
}
