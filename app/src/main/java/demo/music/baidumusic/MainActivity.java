package demo.music.baidumusic;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.lang.ref.WeakReference;

import demo.music.baidumusic.common.EventHandler;
import demo.music.baidumusic.view.SearchDialog;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "BaiduMusic";
    private RequestQueue mQueue;
    private JsonObjectRequest jsonObjectRequest;
    private Handler mHandler;

    //直接用 Handler类初始化可能会引起内存泄露
    private static class WeakHandler extends Handler{
        private final WeakReference<MainActivity> mActivity;

        public WeakHandler(MainActivity activity){
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if(activity != null){
                switch (msg.what){
                    case EventHandler.PARSE_SEARCH_SONG_ID:
                        Log.d(TAG,(String)msg.obj);
                        break;
                    case EventHandler.PARSE_SEARCH_SONG_LINK:
                        break;
                    default:
                        Log.e(TAG,"The msg is undefined");
                }
            }
        }
    }

    private void initial(){
        //init toolbar
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
        mHandler = new WeakHandler(this);

        //初始化dialog
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
}