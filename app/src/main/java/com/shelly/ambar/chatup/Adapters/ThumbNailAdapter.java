package com.shelly.ambar.chatup.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shelly.ambar.chatup.Interfaces.FiltersListFragmentListener;
import com.shelly.ambar.chatup.R;
import com.zomato.photofilters.utils.ThumbnailItem;


import java.util.List;

public class ThumbNailAdapter extends RecyclerView.Adapter<ThumbNailAdapter.MyViewHolder> {

    private List<ThumbnailItem> thumbnailItems;
    private FiltersListFragmentListener Listener;
    private Context context;
    private int SelectIndex=0;

    public ThumbNailAdapter(List<ThumbnailItem> thumbnailItems, FiltersListFragmentListener listener, Context context) {
        this.thumbnailItems = thumbnailItems;
        Listener = listener;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView=LayoutInflater.from(context).inflate(R.layout.thumbnail_item,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {

        final ThumbnailItem thumbnailItem=thumbnailItems.get(i);
        myViewHolder.Thumbnail.setImageBitmap(thumbnailItem.image);
        myViewHolder.Thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Listener.onFilterSelected(thumbnailItem.filter);
                SelectIndex=i;
                notifyDataSetChanged();
            }
        });
        myViewHolder.FilterName.setText(thumbnailItem.filterName);
        if(SelectIndex==i){
            myViewHolder.FilterName.setTextColor(ContextCompat.getColor(context,R.color.selected_filter));
        }else{
            myViewHolder.FilterName.setTextColor(ContextCompat.getColor(context,R.color.normal_filter));

        }


    }

    @Override
    public int getItemCount() {
        return thumbnailItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView Thumbnail;
        TextView FilterName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Thumbnail=itemView.findViewById(R.id.ThumbNail);
            FilterName=itemView.findViewById(R.id.Filter_Name);

        }
    }
}

