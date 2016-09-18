package io.github.cgew85.picops.Anwendungsklassen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import io.github.cgew85.picops.R;

/**
 * The Class MenuListAdapter.
 */
public class MenuListAdapter extends BaseAdapter {

    /**
     * The context.
     */
    Context context;

    /**
     * The m title.
     */
    String[] mTitle;

    /**
     * The m subtitle.
     */
    String[] mSubtitle;

    /**
     * The m icon.
     */
    int[] mIcon;

    /**
     * The inflater.
     */
    LayoutInflater inflater;

    /**
     * Instantiates a new menu list adapter.
     *
     * @param context  the context
     * @param title    the title
     * @param subtitle the subtitle
     * @param icon     the icon
     */
    public MenuListAdapter(Context context, String[] title, String[] subtitle, int[] icon) {
        this.context = context;
        this.mTitle = title;
        this.mSubtitle = subtitle;
        this.mIcon = icon;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return mTitle.length;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return mTitle[position];
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView txtTitle;
        TextView txtSubtitle;
        ImageView imgIcon;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.drawer_list_item, parent, false);

        txtTitle = (TextView) itemView.findViewById(R.id.title);
        txtSubtitle = (TextView) itemView.findViewById(R.id.subtitle);

        imgIcon = (ImageView) itemView.findViewById(R.id.icon);

        txtTitle.setText(mTitle[position]);
        txtSubtitle.setText(mSubtitle[position]);
        imgIcon.setImageResource(mIcon[position]);

        return itemView;
    }


}
