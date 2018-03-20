package adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import model.Lyric;
import ta.contactmanager.R;
import ta.mydictionary.MainActivity;

public class LyricListAdapter extends ArrayAdapter<Lyric> {

    MainActivity ctx;
    List<Lyric> lyrics = null;
    List<Lyric> localCopyLyrics = null;

    public LyricListAdapter(MainActivity ctx, List<Lyric> lyrics) {
        super(ctx, R.layout.listview_lyric_item, lyrics);
        this.ctx = ctx;
        this.lyrics = lyrics;
        this.localCopyLyrics = new ArrayList<>(lyrics);
    }

    public void updateLocalCopy(List<Lyric> lyrics){

        this.localCopyLyrics = new ArrayList<>(lyrics);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null)
            view = ctx.getLayoutInflater().inflate(R.layout.listview_lyric_item, parent, false);

        Lyric lyric = lyrics.get(position);

        TextView wordText = (TextView) view.findViewById(R.id.author);
        wordText.setText(lyric.getAuthor().getName());

        TextView wordTranslation = (TextView) view.findViewById(R.id.song_name);
        wordTranslation.setText(lyric.getSongName());

        TextView quickLyric = (TextView) view.findViewById(R.id.quick_chords_text);

        quickLyric.setText(lyric.getChords());

        return view;
    }

    @Override
    public android.widget.Filter getFilter(){
        return new android.widget.Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();

                if (constraint != null && constraint.toString().length() > 0) {
                    List<Lyric> lyricsFound = new ArrayList<>();
                    for(Lyric lyric: localCopyLyrics){


                        if((lyric.getAuthor().getName()+lyric.getSongName()).toLowerCase().contains(constraint.toString().toLowerCase())){

                            lyricsFound.add(lyric);
                        }
                    }

                    result.values = lyricsFound;
                    result.count = lyricsFound.size();

                }else {
                    result.values = localCopyLyrics;
                    result.count = localCopyLyrics.size();
                }
                return result;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                clear();
                for (Lyric lyric : (List<Lyric>) results.values) {
                    add(lyric);
                }
                notifyDataSetChanged();
            }
        };
    }

}