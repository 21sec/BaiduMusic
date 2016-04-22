package demo.music.baidumusic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "BaiduMusic";
    //volley的用法可参考 http://www.kwstu.com/ArticleView/kwstu_20144118313429
    RequestQueue mQueue;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Title
        toolbar.setTitle("BaiduMusic");
        // Sub Title
        toolbar.setSubtitle("0.1Base");
        setSupportActionBar(toolbar);
        //设置 navigation 需要在setSupportActionBar之后
        toolbar.setNavigationIcon(R.mipmap.ab_android);
        //toolbar 右侧的图标点击后，触发回调
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        //创建volley的工作队列
        mQueue = Volley.newRequestQueue(getApplicationContext());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.action_edit:
                    msg += "Click edit";
                    break;
                case R.id.action_search:
                    msg += "Click search";

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Music Name:");
                    final EditText editText = new EditText(MainActivity.this);
                    //设置editText软键盘种类
                    editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    editText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                    editText.setSingleLine(true);
                    builder.setView(editText);
                    //dialog 空白处不退出
                    builder.setCancelable(false);
                    builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String mucis = editText.getText().toString();
                            String url;
                            try {
                                url = "http://tingapi.ting.baidu.com/v1/restserver/ting?format=json&calback=&from=webapp_music&method=baidu.ting.search.catalogSug&query=" + URLEncoder.encode(mucis, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                url = "";
                            }
                            jsonObjectRequest = new JsonObjectRequest(url, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                JSONArray j = response.getJSONArray("song");
                                                JSONObject x = j.getJSONObject(0);
                                                String songid = x.getString("songid");
                                                String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?format=json&calback=&from=webapp_music&method=baidu.ting.song.downWeb&songid=" + songid + "&bit=flac&_t=" + System.currentTimeMillis();
                                                mQueue.add(new JsonObjectRequest(url, null,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                Log.d(TAG, response.toString());
                                                                try {
                                                                    JSONArray ja = response.getJSONArray("bitrate");
                                                                    int maxbitrate = 0;
                                                                    int maxcount = 0;
                                                                    boolean filelink = true;
                                                                    for (int i = 0; i < ja.length(); i++) {
                                                                        JSONObject x = ja.getJSONObject(i);

                                                                        if (x.getInt("file_bitrate") > maxbitrate) {
                                                                            if (!x.getString("file_link").equals("")) {
                                                                                filelink = true;
                                                                                maxbitrate = x.getInt("file_bitrate");
                                                                                maxcount = i;
                                                                            } else if (!x.getString("show_link").equals("")) {
                                                                                filelink = false;
                                                                                maxbitrate = x.getInt("file_bitrate");
                                                                                maxcount = i;
                                                                            }
                                                                        }
                                                                    }
                                                                    String link = ja.getJSONObject(maxcount).getString(filelink ? "file_link" : "show_link");
                                                                    Log.d(TAG, "下载地址" + link);
                                                                    //将下载地址复制到剪贴板
                                                                    //ClipboardManager clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                                                                    //clip.setPrimaryClip(ClipData.newPlainText(null,link));
                                                                    //暂时以浏览器的方式打开下载链接
                                                                    if (!link.equals("")) {
                                                                        Intent intent = new Intent();
                                                                        intent.setAction("android.intent.action.VIEW");
                                                                        Uri content_url = Uri.parse(link);
                                                                        intent.setData(content_url);
                                                                        startActivity(intent);
                                                                    } else {
                                                                        Toast.makeText(MainActivity.this, "获取下载地址失败", Toast.LENGTH_SHORT).show();
                                                                    }

                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Log.e(TAG, error.getMessage(), error);
                                                    }
                                                }));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e(TAG, error.getMessage(), error);
                                }
                            });
                            mQueue.add(jsonObjectRequest);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                    //show出dialog后，直接显示软键盘
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                                    .toggleSoftInput(0,
                                            InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }, 100);
                    break;
            }
            if (!msg.equals("")) {
                Log.e(TAG,"Menu Item Click test : " + msg);
            }
            return true;
        }

    };
}