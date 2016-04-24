package demo.music.baidumusic.api;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import demo.music.baidumusic.MainActivity;
import demo.music.baidumusic.common.EventHandler;
import demo.music.baidumusic.common.SongInfo;

/**
 * Json 解析类
 */
public class JsonParse {
    private JsonObjectRequest jsonObjectRequest;
    private Message msg;
    private Handler mHandler;
    public JsonParse(String url , Handler handler) {
        mHandler = handler;
        jsonObjectRequest = new JsonObjectRequest(url,null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("BaiduMusic", response.toString());
                switch (msg.what){

                    case EventHandler.PARSE_SEARCH_SONG_LIST:
                        msg.obj = getSongList(response);
                        mHandler.sendMessage(msg);
                        break;

                    //解析ID,然后返回给主线程LIST
                    case EventHandler.PARSE_SEARCH_ARTIST_ID:
                        String artistId = parseArtistId(response);
                        msg.what = EventHandler.PARSE_SEARCH_ARTIST_LIST;
                        msg.obj = artistId;
                        String url = BaiduHttpApi.RequestUrl(msg);
                        JsonParse jsonParse = new JsonParse(url,mHandler);
                        jsonParse.startParse(msg);
                        break;

                    case EventHandler.PARSE_SEARCH_ARTIST_LIST:
                        msg.obj = getArtistList(response);
                        mHandler.sendMessage(msg);
                        break;

                    case EventHandler.PARSE_SEARCH_SONG_LINK:
                        msg.obj = getSongLink(response);
                        mHandler.sendMessage(msg);
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("BaiduMusic", error.getMessage(), error);
            }
        });
    }


    public void startParse(Message msg){
        this.msg = msg;
        MainActivity.mQueue.add(jsonObjectRequest);
    }

    private ArrayList<SongInfo> getSongList(JSONObject response){
        ArrayList<SongInfo> arrayList = new ArrayList<>();
        try{
            JSONArray jsonArray = response.getJSONArray("song");
            for(int i =0;  i < jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(!jsonObject.getString("songname").equals("") &&
                        !jsonObject.getString("artistname").equals("")&&
                        !jsonObject.getString("songid").equals("")){
                    SongInfo songInfo = new SongInfo(jsonObject.getString("songname"),
                            jsonObject.getString("artistname"),jsonObject.getString("songid"));
                    arrayList.add(songInfo);
                }
            }
        }catch (JSONException e){
            arrayList = null;
            e.printStackTrace();
        }


        return arrayList;
    }

    private String parseArtistId(JSONObject response){
        String tinguid = "";
        try {
            JSONArray jsonArray = response.getJSONArray("artist");
            for(int i = 0 ; i < jsonArray.length() ; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(!jsonObject.getString("artistid").equals("")){
                    tinguid = jsonObject.getString("artistid");
                    break;
                }
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
        return tinguid;
    }
    private ArrayList<SongInfo> getArtistList(JSONObject response){
        ArrayList<SongInfo> arrayList = new ArrayList<>();
        try{
            JSONArray jsonArray = response.getJSONArray("songlist");
            for(int i =0;  i < jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(!jsonObject.getString("title").equals("") &&
                        !jsonObject.getString("author").equals("")&&
                        !jsonObject.getString("song_id").equals("")){
                    SongInfo songInfo = new SongInfo(jsonObject.getString("title"),
                            jsonObject.getString("author"),jsonObject.getString("song_id"));
                    arrayList.add(songInfo);
                }
            }
        }catch (JSONException e){
            arrayList = null;
            e.printStackTrace();
        }


        return arrayList;
    }

    private String getSongLink(JSONObject response){
        String link = "";
        try{
            JSONArray ja = response.getJSONArray("bitrate");
            int maxbitrate = 0;
            int maxcount = 0;
            boolean file_link = true;
            for(int i = 0 ; i < ja.length();i++){
                JSONObject jo = ja.getJSONObject(i);
                if(jo.getInt("file_bitrate") > maxbitrate){
                    Log.d("ruifeng",""+jo.getInt("file_bitrate"));
                    if(!jo.getString("file_link").equals("")){
                        maxbitrate = jo.getInt("file_bitrate");
                        maxcount = i;
                        file_link = true;
                    }
                    else if(!jo.getString("show_link").equals("")){
                        maxbitrate = jo.getInt("file_bitrate");
                        maxcount = i;
                        file_link = false;
                    }
                }
            }
            link = ja.getJSONObject(maxcount).getString(file_link?"file_link":"show_link");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return link;
    }
}
