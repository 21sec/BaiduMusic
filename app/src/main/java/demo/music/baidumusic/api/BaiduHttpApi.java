package demo.music.baidumusic.api;

import android.os.Message;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import demo.music.baidumusic.common.EventHandler;

/**
 * http 接口
 */
public class BaiduHttpApi {
    //http://mrasong.com/a/baidu-mp3-api-full
    public static final String HTTP_HEAD    = "http://tingapi.ting.baidu.com/v1/restserver/ting?" +
            "format=json&calback=&from=webapp_music";

    /*
    获取列表 {type:x,size:x, offset:x}
        type: 1、新歌榜，2、热歌榜，
        11、摇滚榜，12、爵士，16、流行
        21、欧美金曲榜，22、经典老歌榜，23、情歌对唱榜，24、影视金曲榜，25、网络歌曲榜
    size: 10 返回条目数量
    offset: 0 获取偏移
     */
    public static final String HTTP_BILLLIST = "&method=baidu.ting.billboard.billList";

    /*
    搜索 { query: keyword }
     */
    public static final String HTTP_CATALOGSUG = "&method=baidu.ting.search.catalogSug";

    /*
    LRC歌词 {songid: id}
     */
    public static final String HTTP_LRY = "&method=baidu.ting.song.lry";

    /*
    推荐列表 {song_id: id, num: 5 }
    num: 返回条目数量
     */
    public static final String HTTP_GETRECOMMANDSONGLIST = "&method=baidu.ting.song.getRecommandSongList";

    /*
    下载 {songid: id, bit:"24, 64, 128, 192, 256, 320, flac", _t: (new Date())}
    songid: 歌曲id
    bit: 码率
    _t: 时间戳
     */
    public static final String HTTP_DOWNWEB = "&method=baidu.ting.song.downWeb";

    /*
    获取歌手信息 { tinguid: id }
    tinguid:歌手ting id
     */
    public static final String HTTP_ARTIST_GETINFO = "&method=baidu.ting.artist.getInfo";

    /*
    获取歌手歌曲列表 { tinguid: id, limits:6, use_cluster:1, order:2}
    tinguid: 歌手ting id
    limits: 返回条目数量
     */
    public static final String HTTP_ARTIST_GETSONGLIST = "&method=baidu.ting.artist.getSongList";

    public static String RequestUrl(Message msg){
        String url;
        String msgURLEncode = null;
        try{
            msgURLEncode = URLEncoder.encode((String)msg.obj,"UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        switch (msg.what){
            case EventHandler.PARSE_SEARCH_SONG_LIST:
            case EventHandler.PARSE_SEARCH_ARTIST_ID:
                //获取歌曲id或者歌手id的Json url
                url = HTTP_HEAD+HTTP_CATALOGSUG+"&query="+ msgURLEncode;
                break;
            case EventHandler.PARSE_SEARCH_ARTIST_LIST:
                url = HTTP_HEAD+HTTP_ARTIST_GETSONGLIST+"&tinguid="+msgURLEncode+"&limits=20&use_cluster=1&order=2";
                break;
            case EventHandler.PARSE_SEARCH_SONG_LINK:
                //获取下载地址的Json url
                url = HTTP_HEAD+HTTP_DOWNWEB+"&songid="+msgURLEncode+"&bit=flac&_t="+System.currentTimeMillis();
                break;
            default:
                url = "";
                break;
        }
        Log.d("BaiduMusic",url);
        return url;
    }

}
