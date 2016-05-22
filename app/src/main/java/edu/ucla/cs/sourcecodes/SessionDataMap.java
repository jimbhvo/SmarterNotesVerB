package edu.ucla.cs.sourcecodes;

import java.util.ArrayList;


public class SessionDataMap {
    private ArrayList<String> sNames;
    private ArrayList<SessionData> sDataMap;
    private String curSessionName;

    public SessionDataMap()
    {
        sNames = new ArrayList<>();
        sDataMap = new ArrayList<>();
        curSessionName = "";
    }

    public void setSession(String s, SessionData sData)
    {
        if (!sNames.contains(s)) {
            sNames.add(s);
            sDataMap.add(sData);
        }
        else
        {
            int sessionNum = sNames.indexOf(s);
            sDataMap.set(sessionNum,sData);
        }
    }

    public int getLength()
    {
        return sNames.size();
    }

    public SessionData getFirst()
    {
        return sDataMap.get(0);
    }

    public SessionData getSession(String s)
    {
        int dataNum = sNames.indexOf(s);
        return sDataMap.get(dataNum);
    }

    public ArrayList<SessionData> getSessionList()
    {
        return sDataMap;
    }

    public ArrayList<String> getsNames(){ return sNames; }

    public void removeSession(String S)
    {
        SessionData removeMe = getSession(S);
        sNames.remove(S);
        sDataMap.remove(removeMe);
    }

    public void setCurSessionName(String s){curSessionName = s;}

    public String getCurSessionName(){return curSessionName;}
}
