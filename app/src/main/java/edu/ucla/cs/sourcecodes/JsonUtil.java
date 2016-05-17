package edu.ucla.cs.sourcecodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jimmy on 5/12/2016.
 */
public class JsonUtil {
    public static String toJson(SessionData sessionData)
    {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("sessionName", sessionData.getSessionName());
            JSONArray jsonArr = new JSONArray();

            for (String wd : sessionData.getWordList())
            {
                JSONObject wdObj = new JSONObject();
                wdObj.put("word", wd);
                jsonArr.put(wdObj);
            }

            jsonObj.put("wordList",jsonArr);

            return jsonObj.toString();
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public static SessionData toSdata(String string)
    {
        SessionData sData = new SessionData();
        try {
            JSONObject jObj = new JSONObject(string);
            sData.setSessionName(jObj.getString("sessionName"));

            JSONArray jArr = jObj.getJSONArray("wordList");
            List<String> wordList = new ArrayList<>();

            for (int i=0; i < jArr.length(); i++) {
                JSONObject obj = jArr.getJSONObject(i);
                wordList.add(obj.getString("word"));
            }
            sData.setWordList(wordList);

            return sData;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return sData;
    }


}
