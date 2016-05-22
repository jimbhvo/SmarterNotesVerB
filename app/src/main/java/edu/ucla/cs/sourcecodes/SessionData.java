package edu.ucla.cs.sourcecodes;

import java.util.ArrayList;
import java.util.List;

public class SessionData {
    //get and set
    private String sessionName;
    private ArrayList<String> wordList;

    public void setSessionName(String name)
    {
        sessionName = name;
    }

    public String getSessionName()
    {
        return sessionName;
    }

    public void setWordList(ArrayList<String> list)
    {
        wordList = new ArrayList<>(list);
    }

    public List<String> getWordList()
    {
        return wordList;
    }
}
