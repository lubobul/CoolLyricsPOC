package ta.mydictionary;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import db.DbHandler;
import model.Author;
import model.Lyric;
import ta.contactmanager.R;

public class AddLyricsActivity extends Activity {

    DbHandler dbHandler = null;
    AutoCompleteTextView newAuthorEditText = null;
    EditText newSongNameEditText = null;
    EditText songLyricsEditText = null;
    EditText songChordsEditText = null;

    List<Author> authors = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_word);

        newAuthorEditText = (AutoCompleteTextView) findViewById(R.id.add_author_name);
        newSongNameEditText = (EditText) findViewById(R.id.add_song_name);
        songLyricsEditText = (EditText) findViewById(R.id.add_song_lyrics);
        songChordsEditText = (EditText) findViewById(R.id.add_chords);

        try {
            dbHandler = DbHandler.getInstance(this);
        } catch (Exception ex) {
            Log.getStackTraceString(ex);
            this.finish();
        }

        authors = dbHandler.getAllAuthors();

        Log.d("Authors: ", authors.size() + "");

        ArrayList<String> authorNames = new ArrayList<>();
        for(Author auth : authors){

            authorNames.add(auth.getName());
        }

        newAuthorEditText.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, authorNames));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_word, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {

            case R.id.action_back:
                setResult(RESULT_CANCELED);
                finish();
                return true;

            case R.id.action_save:

                Author newAuthor = new Author(String.valueOf(newAuthorEditText.getText()));
                String songName = String.valueOf(newSongNameEditText.getText());
                String songLyrics = String.valueOf(songLyricsEditText.getText());
                String songChords = String.valueOf(songChordsEditText.getText());

                if (newAuthor.getName().equals("") || songName.equals("") || songLyrics.equals("") || songChords.equals("")) {
                    Toast.makeText(AddLyricsActivity.this, "You must not leave empty fields.", Toast.LENGTH_SHORT).show();
                    break;
                }

                    dbHandler.addLyric(new Lyric(newAuthor, songChords, songName, songLyrics));

                    Toast.makeText(this.getApplicationContext(), songName + " by " + newAuthor.getName() + " has been added.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();

                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
