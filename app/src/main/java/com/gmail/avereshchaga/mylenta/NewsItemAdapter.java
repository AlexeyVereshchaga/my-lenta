package com.gmail.avereshchaga.mylenta;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.avereshchaga.mylenta.model.News;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Set;

/**
 * Created by Alex on 15.03.2015.
 */

public class NewsItemAdapter extends ArrayAdapter<News> {
    private List<News> newses;

    public NewsItemAdapter(Context context, int textViewResourceId,
                           List<News> objects) {
        super(context, textViewResourceId, objects);
        newses = objects;
    }

    static class ViewHolder {
        TextView titleView;
        TextView dateView;
        TextView descriptionView;
        TextView sourceView;
        ImageView thumbView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder viewHolder;

        if (v == null) {
            LayoutInflater vi = ((Activity) getContext()).getLayoutInflater();
            convertView = vi.inflate(R.layout.news_line_item, null);
            viewHolder = new ViewHolder();
            viewHolder.thumbView = (ImageView) convertView.findViewById(R.id.iv_thumb);
            viewHolder.titleView = (TextView) convertView.findViewById(R.id.tv_title_label);
            viewHolder.dateView = (TextView) convertView.findViewById(R.id.tv_date_label);
            viewHolder.descriptionView = (TextView) convertView.findViewById(R.id.tv_description_label);
            viewHolder.sourceView = (TextView) convertView.findViewById(R.id.tv_source);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
            viewHolder.thumbView.setVisibility(View.GONE);
            viewHolder.descriptionView.setVisibility(View.GONE);
        }
        if (newses.get(position).getEnclosure() != null) {
            viewHolder.thumbView.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(newses.get(position).getEnclosure(), viewHolder.thumbView);
        }
        viewHolder.titleView.setText(newses.get(position).getTitle());
        viewHolder.dateView.setText(newses.get(position).getPubDate());
        viewHolder.descriptionView.setText(newses.get(position).getDescription());
        if (newses.get(position).getAuthor() != null) {
            viewHolder.sourceView.setText(newses.get(position).getAuthor());
        } else {
            viewHolder.sourceView.setText("Lenta.ru");
        }

        return convertView;
    }
}
