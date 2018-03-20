package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import model.Author;
import model.Lyric;

/**
 * Created by lubobul on 9/2/2015.
 */
public class DbHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    private static DbHandler dbInstance = null;

    private static SQLiteDatabase writable_db;
    private static SQLiteDatabase readable_db;

    private static final String DATABASE_NAME = "CoolLyrics",
    TABLE_AUTHOR = "author",
    KEY_AUTHOR_ID = "author_id",
    KEY_AUTHOR_NAME = "name",

    TABLE_LYRICS = "lyrics",
    KEY_LYRICS_ID = "lyric_id",
    KEY_CHORDS = "chords",
    KEY_SONG_NAME = "song_name",
    KEY_LYRIC = "lyric",
    FOREIGN_KEY_AUTHOR_ID = "fk_author_id";

    private DbHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DbHandler getInstance(Context context){

        if(dbInstance == null)
            dbInstance = new DbHandler(context);

        writable_db = dbInstance.getWritableDatabase();
        readable_db = dbInstance.getReadableDatabase();

        while(writable_db.isDbLockedByCurrentThread() || writable_db.isDbLockedByOtherThreads()) {
            //db is locked, keep looping
        }

        while(readable_db.isDbLockedByCurrentThread() || readable_db.isDbLockedByOtherThreads()) {
            //db is locked, keep looping
        }

        return dbInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_AUTHOR + "(" + KEY_AUTHOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_AUTHOR_NAME + " TEXT);");

        db.execSQL("CREATE TABLE " + TABLE_LYRICS + "(" + KEY_LYRICS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_CHORDS + " TEXT, " + KEY_SONG_NAME + " TEXT, "  + KEY_LYRIC + " TEXT, " + FOREIGN_KEY_AUTHOR_ID +" INTEGER, " +
                "FOREIGN KEY (" + FOREIGN_KEY_AUTHOR_ID + ") REFERENCES " + TABLE_AUTHOR + " (" + KEY_AUTHOR_ID + "));");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTHOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LYRICS);
        onCreate(db);
    }

    public Lyric getLastInsertedLyric(){

        final String MY_QUERY = "SELECT a.*, b.* FROM " + TABLE_AUTHOR +" a INNER JOIN " + TABLE_LYRICS + " b ON a."+
                KEY_AUTHOR_ID + "=b." +  FOREIGN_KEY_AUTHOR_ID+ " WHERE b." + KEY_LYRICS_ID + "= (SELECT MAX(" + KEY_LYRICS_ID + ")  FROM " + TABLE_LYRICS + ");";


        Cursor cursor = readable_db.rawQuery(MY_QUERY, null);

        Lyric lyric = null;

        if(cursor.moveToFirst()){
            lyric = new Lyric( Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_LYRICS_ID))),
                    new Author( Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_AUTHOR_ID))),
                            cursor.getString(cursor.getColumnIndex(KEY_AUTHOR_NAME))), cursor.getString(cursor.getColumnIndex(KEY_CHORDS)),
                    cursor.getString(cursor.getColumnIndex(KEY_SONG_NAME)), cursor.getString(cursor.getColumnIndex(KEY_LYRIC)));
        }

        cursor.close();

        return lyric;
    }

    public Lyric getLyricById(int id){

        final String MY_QUERY = "SELECT a.*, b.* FROM " + TABLE_AUTHOR +" a INNER JOIN " + TABLE_LYRICS + " b ON a."+
                KEY_AUTHOR_ID + "=b." +  FOREIGN_KEY_AUTHOR_ID+ " WHERE b." + KEY_LYRICS_ID + "=?";

        Cursor cursor = readable_db.rawQuery(MY_QUERY, new String[]{String.valueOf(id)});

        Lyric lyric = null;
        if(cursor.moveToFirst()) {
            lyric = new Lyric( Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_LYRICS_ID))),
                    new Author( Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_AUTHOR_ID))),
                            cursor.getString(cursor.getColumnIndex(KEY_AUTHOR_NAME))), cursor.getString(cursor.getColumnIndex(KEY_CHORDS)),
                    cursor.getString(cursor.getColumnIndex(KEY_SONG_NAME)), cursor.getString(cursor.getColumnIndex(KEY_LYRIC)));
        }

        cursor.close();
        return lyric;
    }

    public Author checkAuthorExists(String authorName){

        final String rawSql = "SELECT * FROM " + TABLE_AUTHOR + " WHERE " + KEY_AUTHOR_NAME + "=?";
        Cursor cursor = readable_db.rawQuery(rawSql, new String[]{String.valueOf(authorName)});

        Author author = null;
        if(cursor.moveToFirst()) {
            author = new Author(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_AUTHOR_ID))),
                     cursor.getString(cursor.getColumnIndex(KEY_AUTHOR_NAME)));
        }

        cursor.close();

        return author;
    }

    public long addAuthor(Author author){

        ContentValues values = new ContentValues();

        values.put(KEY_AUTHOR_NAME, author.getName());

        long  insertedRowID = writable_db.insert(TABLE_AUTHOR, null, values);

        return insertedRowID;
    }

    public void addLyric(Lyric lyric){

        Author author = checkAuthorExists(lyric.getAuthor().getName());

        long authorId = 0;
        if(author == null)
            authorId = addAuthor(lyric.getAuthor());
        else
            authorId = author.getId();

        if(authorId != -1) {
            ContentValues values = new ContentValues();
            values.put(KEY_CHORDS, lyric.getChords());
            values.put(KEY_SONG_NAME, lyric.getSongName());
            values.put(KEY_LYRIC, lyric.getText());
            values.put(FOREIGN_KEY_AUTHOR_ID, authorId);
            writable_db.insert(TABLE_LYRICS, null, values);
        }

    }

    public List<Lyric> getAllLyrics(){

        List<Lyric> lyrics = new ArrayList<>();

        final String MY_QUERY = "SELECT a.*, b.* FROM " + TABLE_AUTHOR +" a INNER JOIN " + TABLE_LYRICS + " b ON a."+
                KEY_AUTHOR_ID + "=b." +  FOREIGN_KEY_AUTHOR_ID+ ";";

        Cursor cursor = readable_db.rawQuery(MY_QUERY, null);

        if(cursor.moveToFirst()) {

            do {
                lyrics.add(new Lyric( Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_LYRICS_ID))),
                        new Author( Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_AUTHOR_ID))),
                                cursor.getString(cursor.getColumnIndex(KEY_AUTHOR_NAME))), cursor.getString(cursor.getColumnIndex(KEY_CHORDS)),
                        cursor.getString(cursor.getColumnIndex(KEY_SONG_NAME)), cursor.getString(cursor.getColumnIndex(KEY_LYRIC))));

            } while (cursor.moveToNext());
        }

        cursor.close();

        return lyrics;
    }

    public List<Author> getAllAuthors(){

        List<Author> authors = new ArrayList<>();

        final String MY_QUERY = "SELECT * FROM " + TABLE_AUTHOR + ";";

        Cursor cursor = readable_db.rawQuery(MY_QUERY, null);

        if(cursor.moveToFirst()) {

            do {
                authors.add(new Author( Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_AUTHOR_ID))),
                    cursor.getString(cursor.getColumnIndex(KEY_AUTHOR_NAME))));

            } while (cursor.moveToNext());
        }

        cursor.close();

        return authors;
    }

    public void updateSingleLyric(Lyric lyric) {


        ContentValues values = new ContentValues();
        values.put(KEY_SONG_NAME, lyric.getSongName());
        values.put(KEY_LYRIC, lyric.getText());
        values.put(KEY_CHORDS, lyric.getChords());

        // updating row
        writable_db.update(TABLE_LYRICS, values, KEY_LYRICS_ID + " = ?",
                new String[]{String.valueOf(lyric.getId())});
    }

    public void removeLyric(Lyric lyric){

        writable_db.delete(TABLE_LYRICS, KEY_LYRICS_ID + " = ?",
                new String[]{String.valueOf(lyric.getId())});
    }

    public void freeDbResources(){

        readable_db.close();
        writable_db.close();
    }
}
