package demo.music.baidumusic.common;

/**
 * 歌曲信息类
 */
public class SongInfo {
    private String songName;
    private String artistName;
    private String songID;

    public SongInfo(String songName, String artistName, String songID) {
        this.songName = songName;
        this.artistName = artistName;
        this.songID = songID;
    }

    @Override
    public String toString() {
        return "SongInfo{" +
                "songName='" + songName + '\'' +
                ", artistName='" + artistName + '\'' +
                ", songID='" + songID + '\'' +
                '}';
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getSongID() {
        return songID;
    }

    public void setSongID(String songID) {
        this.songID = songID;
    }
}
