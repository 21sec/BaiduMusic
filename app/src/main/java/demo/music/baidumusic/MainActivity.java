package demo.music.baidumusic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import demo.music.baidumusic.api.BaiduHttpApi;
import demo.music.baidumusic.api.JsonParse;
import demo.music.baidumusic.common.EventHandler;
import demo.music.baidumusic.common.SongInfo;
import demo.music.baidumusic.view.SearchDialog;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "BaiduMusic";
    //用于控制 双击2次back键的功能
    private long firstTime = 0;
    private Handler mHandler;
    public static RequestQueue mQueue;
    private ListView mListView;
    private static SimpleAdapter adapter;
    private static ArrayList<HashMap<String,Object>> mList;

    //直接用 Handler类初始化可能会引起内存泄露
    private static class WeakHandler extends Handler{
        private final WeakReference<MainActivity> mActivity;

        public WeakHandler(MainActivity activity){
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();

            if(activity != null){
                ArrayList<SongInfo> arrayList;
                switch (msg.what) {
                    //更新listView
                    case EventHandler.PARSE_SEARCH_SONG_LIST:
                    case EventHandler.PARSE_SEARCH_ARTIST_LIST:
                        arrayList = (ArrayList<SongInfo>)msg.obj;
                        if(arrayList == null){
                            Toast.makeText(activity,"获取音乐列表失败,更改关键字或检查网络",Toast.LENGTH_LONG).show();
                        }else{
                            mList.clear();
                            for(int i = 0 ; i< arrayList.size();i++){
                                HashMap<String,Object> map = new HashMap<>();
                                map.put("songName",arrayList.get(i).getSongName());
                                map.put("artistName",arrayList.get(i).getArtistName());
                                map.put("songID",arrayList.get(i).getSongID());
                                mList.add(map);
                            }
                            adapter.notifyDataSetChanged();
                        }
                        break;
                    case EventHandler.PARSE_SEARCH_SONG_LINK:
                        //启动浏览器下载
                        String link = (String)msg.obj;
                        if(link.equals("")){
                            Toast.makeText(activity,"获取下载地址失败,请稍候再试",Toast.LENGTH_LONG).show();
                        }else{
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse(link);
                            intent.setData(content_url);
                            activity.startActivity(intent);
                            Log.d("ruifeng",link);
                        }
                        break;
                }

            }
        }
    }

    private void initial(){
        //初始化Handler;
        mHandler = new WeakHandler(this);

        //初始化控件
        initView();

        //创建volley的工作队列
        mQueue = Volley.newRequestQueue(getApplicationContext());
    }

    private void initView(){
        //init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Title
        toolbar.setTitle("BaiduMusic");
        // Sub Title
        toolbar.setSubtitle("1.0v");
        setSupportActionBar(toolbar);
        //设置 navigation 需要在setSupportActionBar之后
        toolbar.setNavigationIcon(R.mipmap.ab_android);
        //toolbar 右侧的图标点击后，触发回调
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        mList = new ArrayList<>();
        //ListView
        mListView = (ListView)findViewById(R.id.listView);
        adapter=new SimpleAdapter(this,mList,R.layout.item_layout,
                new String[]{"songName","artistName"},
                new int[]{R.id.songName,R.id.artistName}){
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                Button button = (Button)v.findViewById(R.id.download);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message msg = Message.obtain();
                        msg.what = EventHandler.PARSE_SEARCH_SONG_LINK;
                        msg.obj = mList.get(position).get("songID");
                        String url = BaiduHttpApi.RequestUrl(msg);
                        JsonParse jsonParse = new JsonParse(url,mHandler);
                        jsonParse.startParse(msg);
                    }
                });
                return v;
            }
        };
        mListView.setAdapter(adapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //完成乱七八糟的初始化工作
        initial();
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
            switch (menuItem.getItemId()) {
                case R.id.action_edit:
                    break;
                case R.id.action_search:
                    SearchDialog.show(MainActivity.this,mHandler);
                    break;
            }
            return true;
        }

    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 3000) {//如果两次按键时间间隔大于3秒，则不退出
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;//更新firstTime
                    return true;
                } else {//两次按键小于3秒时，退出应用
                    finish();
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
}