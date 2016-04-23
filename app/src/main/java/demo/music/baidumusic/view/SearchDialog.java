package demo.music.baidumusic.view;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.avast.android.dialogs.core.BaseDialogFragment;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;

import demo.music.baidumusic.R;
import demo.music.baidumusic.api.BaiduHttpApi;
import demo.music.baidumusic.api.JsonParse;
import demo.music.baidumusic.common.EventHandler;

/**
 * Search 对话窗
 */
public class SearchDialog extends SimpleDialogFragment{
    public static String TAG = "BaiduMusic";
    private static Handler mHandler;
    private static FragmentActivity mActivity;
    private JsonParse jsonParse;

    public static void show(FragmentActivity activity,Handler handler) {
        mHandler = handler;
        mActivity = activity;
        SearchDialog searchDialog = new SearchDialog();
        searchDialog.setCancelable(false);
        searchDialog.show(activity.getSupportFragmentManager(), TAG);
    }

    @Override
    public int getTheme(){
        return R.style.SearchDialog;
    }

    @Override
    public BaseDialogFragment.Builder build(BaseDialogFragment.Builder builder){
        builder.setTitle("Mucis Name:");
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.search_dialog, null);
        final EditText editText = (EditText)v.findViewById(R.id.edit_id);

        builder.setView(v);

        builder.setNegativeButton("SONG", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = Message.obtain();
                msg.what = EventHandler.PARSE_SEARCH_SONG_LIST;
                msg.obj = editText.getText().toString();
                if ((msg.obj).equals("")) {
                    Toast.makeText(mActivity, "Please re-enter the information.", Toast.LENGTH_SHORT).show();
                } else{
                    String url = BaiduHttpApi.RequestUrl(msg);
                    jsonParse = new JsonParse(url,mHandler);
                    jsonParse.startParse(msg);
                }

                dismiss();
            }
        });
        builder.setPositiveButton("ARTIST", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = Message.obtain();
                msg.what = EventHandler.PARSE_SEARCH_ARTIST_ID;
                msg.obj = editText.getText().toString();
                if ((msg.obj).equals("")) {
                    Toast.makeText(mActivity, "Please re-enter the information.", Toast.LENGTH_SHORT).show();
                } else {
                    String url = BaiduHttpApi.RequestUrl(msg);
                    jsonParse = new JsonParse(url,mHandler);
                    jsonParse.startParse(msg);
                }

                dismiss();
            }
        });
        builder.setNeutralButton("CANCEL", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return builder;
    }

}
