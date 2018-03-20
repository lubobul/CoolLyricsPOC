package model;

/**
 * Created by lubobul on 9/1/2015.
 */
public class Lyric {

    private int id;
    private Author author;
    private String songName;
    private String text;
    private String chords;


    public Lyric( Author author, String chords ,String songName, String text) {
        this.id = 0;
        this.author = author;
        this.chords = chords;

        this.songName = songName;
        this.text = text;
    }

    public Lyric(int id, Author author, String chords ,String songName, String text) {
        this.id = id;
        this.author = author;
        this.chords = chords;

        this.songName = songName;
        this.text = text;
    }

    public String getChords() {
        return chords;
    }
    public Author getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }
    public String getSongName() {
        return songName;
    }


    public void setAuthor(Author author){ this.author = author; }
    public void setText(String text){ this.text = text; }
    public void setSongName(String songName) { this.songName = songName; }

    public void setChords(String chords) {
        this.chords = chords;
    }

    public int getId() {
        return id;
    }
}
