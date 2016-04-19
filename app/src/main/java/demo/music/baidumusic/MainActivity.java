package demo.music.baidumusic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "BaiduMusic";
    RequestQueue mQueue;

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

        //生成volley的工作队列
        mQueue = Volley.newRequestQueue(getApplicationContext());
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
                    break;
            }
            if (!msg.equals("")) {
                Log.e(TAG,"Menu Item Click test : " + msg);
            }
            return true;
        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}