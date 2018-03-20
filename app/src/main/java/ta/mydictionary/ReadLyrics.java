package ta.mydictionary;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import db.DbHandler;
import model.Lyric;
import ta.contactmanager.R;

/**
 * Created by lubobul on 9/22/2015.
 */
public class ReadLyrics extends Activity {

    private DbHandler dbHandler = null;

    private Lyric lyric = null;

    private TextView songNameTextView, lyricsTextView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_read_lyrics);

        songNameTextView = (TextView) findViewById(R.id.song_name);
        lyricsTextView = (TextView) findViewById(R.id.song_lyrics);

        try {
            dbHandler = DbHandler.getInstance(this);
        } catch (Exception ex) {
            this.finish();
            ex.printStackTrace();
        }

        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(50);

        lyric = dbHandler.getLyricById(getIntent().getExtras().getInt("lyricId"));

        setTitle(lyric.getAuthor().getName());

        songNameTextView.setText(lyric.getSongName());
        lyricsTextView.setText(lyric.getText());

    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
