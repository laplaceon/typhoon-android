package com.airstream.typhoon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.airstream.typhoon.R;
import com.squareup.picasso.Picasso;
import com.uvnode.typhoon.extensions.model.Series;

import java.util.List;

/**
 * Created by Riyadh on 6/2/2016.
 */
public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.ViewHolder> {

    private List<Series> series;
    private LayoutInflater layoutInflater;
    RecyclerListener.OnBottomReachedListener onBottomReachedListener;

    public SeriesAdapter(Context context, List<Series> series)  {
        this.series = series;
        layoutInflater = LayoutInflater.from(context.getApplicationContext());
    }

    public void clear() {
        int size = getItemCount();
        this.series.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void add(Series series)   {
        this.series.add(series);
        notifyItemInserted(getItemCount());
    }

    public void addAll(List<Series> series)    {
        int size = getItemCount();
        this.series.addAll(series);
        notifyItemRangeInserted(size, series.size());
    }

    public Object getItem(int position) {
        return series.get(position);
    }

    @Override
    public int getItemCount() {
        return series.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder  {
        private ImageView image;
        private TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_card);
            title = itemView.findViewById(R.id.title_card);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.item_series, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Series s = series.get(position);

        if((null != s.getImage()) && !("".equals(s.getImage()))) {
            Picasso.get().load(s.getImage()).centerCrop().fit().placeholder(R.drawable.source_image_placeholder).into(holder.image);
        }
        holder.title.setText(s.getTitle());

        if((null!= onBottomReachedListener) && (position == series.size() - 1)) {
            onBottomReachedListener.onBottomReached(position);
        }
    }

    public void setOnBottomReachedListener(RecyclerListener.OnBottomReachedListener onBottomReachedListener) {
        this.onBottomReachedListener = onBottomReachedListener;
    }
}
