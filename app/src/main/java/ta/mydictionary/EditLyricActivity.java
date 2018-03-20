package ta.mydictionary;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import db.DbHandler;
import model.Author;
import model.Lyric;
import ta.contactmanager.R;

public class EditLyricActivity extends Activity {

    private DbHandler dbHandler = null;

    private Lyric lyricToEdit = null;

    private EditText editTextAuthor = null;
    private EditText editTextSongName = null;
    private EditText editTextSongLyrics = null;
    private EditText editTextChords = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_word);

        try {
            dbHandler = DbHandler.getInstance(this);
        } catch (Exception ex) {
            Log.getStackTraceString(ex);
            this.finish();
        }

        lyricToEdit = dbHandler.getLyricById(getIntent().getExtras().getInt("lyricId"));

        editTextAuthor = (EditText) this.findViewById(R.id.edit_text_author);
        editTextSongName = (EditText) this.findViewById(R.id.edit_text_song_name);
        editTextSongLyrics = (EditText) this.findViewById(R.id.edit_song_lyrics);
        editTextChords = (EditText) this.findViewById(R.id.edit_chords);

        editTextAuthor.setText(lyricToEdit.getAuthor().getName());
        editTextSongName.setText(lyricToEdit.getSongName());
        editTextSongLyrics.setText(lyricToEdit.getText());
        editTextChords.setText(lyricToEdit.getChords());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_word, menu);
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

                Author newAuthor = new Author(String.valueOf(editTextAuthor.getText()));
                String newSongName = String.valueOf(editTextSongName.getText());
                String newSongLyrics = String.valueOf(editTextSongLyrics.getText());
                String newChords = String.valueOf(editTextChords.getText());

                if ((lyricToEdit.getAuthor().getName().equals(newAuthor.getName()) && lyricToEdit.getSongName().equals(newSongName)
                        && lyricToEdit.getText().equals(newSongLyrics) && lyricToEdit.getChords().equals(newChords))
                        || newSongName.trim().equals("") || newAuthor.getName().trim().equals("") ||
                        newSongLyrics.trim().equals("") || newChords.trim().equals("")) {

                    Toast.makeText(EditLyricActivity.this, "You must edit at least one field and not leave any of them blank.", Toast.LENGTH_SHORT).show();

                    break;
                }

                if (lyricToEdit == null) break;

                lyricToEdit.setAuthor(newAuthor);
                lyricToEdit.setSongName(newSongName);
                lyricToEdit.setText(newSongLyrics);
                lyricToEdit.setChords(newChords);

                dbHandler.updateSingleLyric(lyricToEdit);

                Toast.makeText(this.getApplicationContext(), lyricToEdit.getSongName() + " by " + lyricToEdit.getAuthor().getName() + " has been edited.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();

                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
