package ta.mydictionary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import adapters.LyricListAdapter;
import customanimation.TransformAnimation;
import db.DbHandler;
import model.Lyric;
import ta.contactmanager.R;

public class MainActivity extends Activity implements SearchView.OnQueryTextListener {

    //STATICS
    public static final int UPDATE_MAIN_ACTIVITY_AFTER_ADD = 1;
    public static final int UPDATE_MAIN_ACTIVITY_AFTER_EDIT = 2;

    //Db
    private DbHandler dbHandler = null;

    //containers ( lists )
    private List<Lyric> lyrics = null;
    private ListView listView = null;

    //Misc
    private View selectedLyricView = null;
    private SearchView searchView = null;
    LinearLayout searchBarLayout = null;

    private int selectedLyricPosition = 0;

    private LyricListAdapter wordsAdapter = null;

    private Context ctx = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx = this;

        setContentView(R.layout.activity_main);

        try {
            dbHandler = DbHandler.getInstance(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        lyrics = dbHandler.getAllLyrics();
        Collections.sort(lyrics, new Comparator<Lyric>() {
            @Override
            public int compare(Lyric lhs, Lyric rhs) {
                return lhs.getAuthor().getName().compareToIgnoreCase(rhs.getAuthor().getName());
            }
        });

        this.initializeLyricsListView();

    }


    /**
     * Initializes UI objects and listeners
     */
    private void initializeLyricsListView() {

        wordsAdapter = new LyricListAdapter(this, lyrics);
        listView = (ListView) findViewById(R.id.listView);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(wordsAdapter);

        searchBarLayout = (LinearLayout) this.findViewById(R.id.search_bar_layout);
        searchView = (SearchView) this.findViewById(R.id.search_bar_view);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /***
             * OnItemClick Listener - Handles click events from the Lyrics list
             * @param parent
             * @param view
             * @param position
             * @param id
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (selectedLyricView != null) {
                    selectedLyricView.setBackgroundColor(Color.TRANSPARENT);
                    LinearLayout lr = (LinearLayout) selectedLyricView.findViewById(R.id.quick_chords_layout);
                    ViewGroup.LayoutParams params = lr.getLayoutParams();
                    params.height = 0;
                    lr.setLayoutParams(params);
                }

                LinearLayout quickLyricViewLayout = (LinearLayout) view.findViewById(R.id.quick_chords_layout);

                // Prepare the View for the animation
                quickLyricViewLayout.setVisibility(View.VISIBLE);

                TextView chords = (TextView) view.findViewById(R.id.quick_chords_text);
                chords.measure(0, 0);
                Animation ani = new TransformAnimation(quickLyricViewLayout, chords.getMeasuredHeight());

                ani.setDuration(200);
                quickLyricViewLayout.startAnimation(ani);

                selectedLyricView = view;
                selectedLyricPosition = position;

                view.setBackgroundColor(Color.rgb(164, 198, 57));


            }
        });


        /***
         * Handles long click from the Lyrics list. It opens the view lyric activity
         */
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                // Toast.makeText(MainActivity.this, "ID: " + id, Toast.LENGTH_SHORT).show();

                Intent readLyricsIntent = new Intent(MainActivity.this, ReadLyrics.class);
                readLyricsIntent.putExtra("lyricId", lyrics.get(position).getId());
                ctx.startActivity(readLyricsIntent);

                return false;
            }
        });

        searchView.setOnQueryTextListener(this);

    }

    /**
     * It passes search string to the lyrics adapter filter
     *
     * @param searchString
     * @return
     */
    @Override
    public boolean onQueryTextChange(String searchString) {
        wordsAdapter.getFilter().filter(searchString);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case UPDATE_MAIN_ACTIVITY_AFTER_ADD:
                if (resultCode == RESULT_OK) {

                    Lyric word = dbHandler.getLastInsertedLyric();
                    if (word != null)
                        lyrics.add(word);

                    Collections.sort(lyrics, new Comparator<Lyric>() {
                        @Override
                        public int compare(Lyric lhs, Lyric rhs) {
                            return lhs.getAuthor().getName().compareToIgnoreCase(rhs.getAuthor().getName());
                        }
                    });

                    wordsAdapter.updateLocalCopy(lyrics);
                    wordsAdapter.notifyDataSetChanged();
                }
                break;

            case UPDATE_MAIN_ACTIVITY_AFTER_EDIT:
                if (resultCode == RESULT_OK) {

                    Lyric selectedWord = lyrics.get(selectedLyricPosition);
                    Lyric word = dbHandler.getLyricById(selectedWord.getId());

                    if (word != null) {

                        lyrics.remove(selectedLyricPosition);
                        lyrics.add(selectedLyricPosition, word);

                        wordsAdapter.updateLocalCopy(lyrics);
                        wordsAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (id) {

            case R.id.action_add:

                Intent addWordIntent = new Intent(MainActivity.this, AddLyricsActivity.class);
                this.startActivityForResult(addWordIntent, MainActivity.UPDATE_MAIN_ACTIVITY_AFTER_ADD);

                return true;

            case R.id.action_edit:

                if (selectedLyricView == null && lyrics.isEmpty()) break;

                Intent editWordIntent = new Intent(MainActivity.this, EditLyricActivity.class);
                editWordIntent.putExtra("lyricId", lyrics.get(selectedLyricPosition).getId());
                this.startActivityForResult(editWordIntent, MainActivity.UPDATE_MAIN_ACTIVITY_AFTER_EDIT);

                return true;

            case R.id.action_remove:

                if (this.selectedLyricView == null || lyrics.isEmpty()) break;

                final TextView authorTextView = (TextView) selectedLyricView.findViewById(R.id.author);
                final TextView songNameTextView = (TextView) selectedLyricView.findViewById(R.id.song_name);

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:


                                dbHandler.removeLyric(lyrics.get(selectedLyricPosition));
                                lyrics.remove(selectedLyricPosition);

                                wordsAdapter.updateLocalCopy(lyrics);
                                wordsAdapter.notifyDataSetChanged();

                                Toast.makeText(ctx.getApplicationContext(), String.valueOf(songNameTextView.getText()) + " by " +
                                        String.valueOf(authorTextView.getText()) + " has been removed.", Toast.LENGTH_SHORT).show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setMessage("Do you really want to remove " + String.valueOf(songNameTextView.getText()) + " by " +
                        String.valueOf(authorTextView.getText()) + "?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                return true;

            case R.id.action_search:

                searchBarLayout.setVisibility(View.VISIBLE);
                searchBarLayout.measure(0, 0);

                ViewGroup.LayoutParams params = searchBarLayout.getLayoutParams();
                params.height = searchBarLayout.getMeasuredHeight();

                searchBarLayout.setLayoutParams(params);

                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {


        if (searchBarLayout.getVisibility() == View.VISIBLE) {
            searchBarLayout.setVisibility(View.INVISIBLE);

            ViewGroup.LayoutParams params = searchBarLayout.getLayoutParams();
            params.height = 1;

            searchBarLayout.setLayoutParams(params);

            //resets the filter after back button
            wordsAdapter.getFilter().filter("");

        } else {
            finish();
        }
    }
}


