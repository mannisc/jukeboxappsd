package com.songbase.fm.androidapp.list;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.R;

public class ListAdapter extends ArrayAdapter<MainListElement> implements
        SectionIndexer {

    private List<MainListElement> list;
    private Context context;
    private static String sections = "abcdefghilmnopqrstuvz";
    private int oldPosition = 0;

    public static enum ListLayout {
        NAME, NAMEINFO
    }

    ;

    public ListAdapter(List<MainListElement> list, Context ctx) {
        super(ctx, R.layout.nameinforowlayout, list);
        this.list = list;
        this.context = ctx;
    }

    public List<MainListElement> getList() {
        return list;
    }

    public void setList(List<MainListElement> list) {

        this.list = list;

        this.notifyDataSetChanged();

    }

    public int getCount() {
        return list.size();
    }

    public MainListElement getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return list.get(position).hashCode();
    }

    // TODO adapt layout

    public View getView(int position, View convertView, ViewGroup parent) {

        MainListElement listElement = list.get(position);

        ListHolder holder = new ListHolder();

        boolean redraw = false;

        if (convertView == null
                || (convertView != null && (((ListHolder) convertView.getTag()).infoLayout != (listElement
                .getListLayout() == ListLayout.NAMEINFO)))) {

            redraw = true;

        }
        // First let's verify the convertView is not null
        if (redraw) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (listElement.getListLayout() == ListLayout.NAMEINFO) {

                convertView = inflater
                        .inflate(R.layout.nameinforowlayout, null);

                TextView infoView = (TextView) convertView
                        .findViewById(R.id.infolist);
                /* YOUR CHOICE OF COLOR */
                infoView.setTextColor(Color.WHITE);

                holder.infoView = infoView;

            } else {
                convertView = inflater.inflate(R.layout.namerowlayout, null);

            }

            ImageView imageView = (ImageView) convertView
                    .findViewById(R.id.imglist);

            holder.imageView = imageView;

            ImageView imageViewTop = (ImageView) convertView
                    .findViewById(R.id.imglisttop);

            holder.imageView = imageView;
            holder.imageViewTop = imageViewTop;


            TextView nameView = (TextView) convertView
                    .findViewById(R.id.namelist);

            holder.nameView = nameView;
            nameView.setTextColor(Color.WHITE);

            convertView.setTag(holder);
        } else {
            holder = (ListHolder) convertView.getTag();
        }

        holder.infoLayout = (listElement.getListLayout() == ListLayout.NAMEINFO);

        parent.setBackgroundColor(Color.TRANSPARENT);

        holder.imageView.setImageDrawable(listElement.getIcon());
        holder.imageView.setAlpha((float)(listElement.getIconAlpha())/255f);

        Drawable drawableTop = listElement.getIconTop();
        holder.imageViewTop.setImageDrawable(drawableTop);


        holder.nameView.setText(listElement.getName());

        if (holder.infoView != null)
            holder.infoView.setText(listElement.getInfo());

        return convertView;
    }

	/* *********************************
	 * We use the holder pattern It makes the view faster and avoid finding the
	 * component *********************************
	 */

    private static class ListHolder {
        public boolean infoLayout;
        public ImageView imageView;
        public ImageView imageViewTop;
        public TextView nameView;
        public TextView infoView;
    }

    @Override
    public int getPositionForSection(int section) {
        for (int i = 0; i < this.getCount(); i++) {
            String item = this.getItem(i).getName().toLowerCase();
            if (item.charAt(0) == sections.charAt(section)) {
                oldPosition = i;
                return i;
            }

        }
        return oldPosition;
    }

    @Override
    public int getSectionForPosition(int arg0) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        String[] sectionsArr = new String[sections.length()];
        for (int i = 0; i < sections.length(); i++)
            sectionsArr[i] = "" + sections.charAt(i);

        return sectionsArr;

    }

}