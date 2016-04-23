package demo.music.baidumusic.common;

/**
 * 歌曲信息类
 */
public class SongInfo {
    private String songName;
    private String artistName;
    private String songID;

    public SongInfo(String songName, String artistName, String songLink) {
        this.songName = songName;
        this.artistName = artistName;
        this.songID = songLink;
    }

    @Override
    public String toString() {
        return "SongInfo{" +
                "songName='" + songName + '\'' +
                ", artistName='" + artistName + '\'' +
                ", songLink='" + songID + '\'' +
                '}';
    }
}
