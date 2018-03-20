package adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import model.Author;
import ta.contactmanager.R;
import ta.mydictionary.AddLyricsActivity;

/**
 * Created by lubobul on 10/6/2015.
 */
public class DropDownAuthorAdapter extends ArrayAdapter<Author> {

    AddLyricsActivity ctx = null;
    List<Author> authors = null;

    public DropDownAuthorAdapter(AddLyricsActivity ctx, List<Author> authors) {

        super(ctx, R.layout.dropdown_author_item, authors);

        this.ctx = ctx;
        this.authors = authors;
    }

    public View getView(int position, View view, ViewGroup parent) {

        if (view == null)
            view = ctx.getLayoutInflater().inflate(R.layout.dropdown_author_item, parent, false);

        Author author = authors.get(position);

        TextView author_item = (TextView) view.findViewById(R.id.drop_down_author_item_text);
        author_item.setText(author.getName());

        return view;
    }
}
