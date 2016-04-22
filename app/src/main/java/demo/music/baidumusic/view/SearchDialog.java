package demo.music.baidumusic.view;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.avast.android.dialogs.core.BaseDialogFragment;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;

import demo.music.baidumusic.R;
import demo.music.baidumusic.common.EventHandler;

/**
 * Created by tcl on 16/4/23.
 */
public class SearchDialog extends SimpleDialogFragment{
    public static String TAG = "BaiduMusic";
    private static Handler mhandler;
    public static void show(FragmentActivity activity,Handler handler) {
        mhandler = handler;
        new SearchDialog().show(activity.getSupportFragmentManager(), TAG);
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
        builder.setPositiveButton("Search", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = Message.obtain();
                msg.what = EventHandler.PARSE_SEARCH_SONG_ID;
                msg.obj = editText.getText().toString();
                mhandler.sendMessage(msg);
                dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return builder;
    }
}
