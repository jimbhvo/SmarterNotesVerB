package edu.ucla.cs.sourcecodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtil {
    public static String toJson(SessionDataMap sessionData)
    {
        try {
            JSONArray sDataArray = new JSONArray();
            ArrayList<SessionData> sessionArray = sessionData.getSessionList();
            for(SessionData sd: sessionArray)
            {
                JSONObject jsonObj = new JSONObject();

                jsonObj.put("sessionName", sd.getSessionName());
                JSONArray jsonArr = new JSONArray();

                for (String wd : sd.getWordList()) {
                    JSONObject wdObj = new JSONObject();
                    wdObj.put("word", wd);
                    jsonArr.put(wdObj);
                }

                jsonObj.put("wordList", jsonArr);
                sDataArray.put(jsonObj);
            }
            return sDataArray.toString();
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public static SessionDataMap toSdata(String string)
    {
        SessionDataMap sData = new SessionDataMap();
        try {
            JSONArray jSessionArray = new JSONArray(string);
            for (int iArray = 0; iArray < jSessionArray.length(); iArray++)
            {
                SessionData sDat = new SessionData();
                JSONObject jObj = jSessionArray.getJSONObject(iArray);
                sDat.setSessionName(jObj.getString("sessionName"));

                JSONArray jArr = jObj.getJSONArray("wordList");
                ArrayList<String> wordList = new ArrayList<>();

                for (int i = 0; i < jArr.length(); i++) {
                    JSONObject obj = jArr.getJSONObject(i);
                    wordList.add(obj.getString("word"));
                }
                sDat.setWordList(wordList);
                sData.setSession(sDat.getSessionName(),sDat);
            }
            return sData;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return sData;
    }


}
