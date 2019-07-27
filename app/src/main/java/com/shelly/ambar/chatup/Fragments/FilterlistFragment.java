package com.shelly.ambar.chatup.Fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.shelly.ambar.chatup.Adapters.ThumbNailAdapter;
import com.shelly.ambar.chatup.FilterActivity;
import com.shelly.ambar.chatup.Interfaces.FiltersListFragmentListener;
import com.shelly.ambar.chatup.R;
import com.shelly.ambar.chatup.Utils.BitmapUtils;
import com.shelly.ambar.chatup.Utils.SpacesItemDecoration;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;


public class FilterlistFragment extends Fragment implements FiltersListFragmentListener {

    private RecyclerView recyclerView;
    private ThumbNailAdapter Adapter;
    private List<ThumbnailItem> thumbnailItems;
    private FiltersListFragmentListener listener;

    public void setListener(FiltersListFragmentListener listener) {
        this.listener = listener;
    }


    public FilterlistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance()==null){
            getFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemView= inflater.inflate(R.layout.fragment_filterlist, container, false);
        thumbnailItems=new ArrayList<>();
        Adapter=new ThumbNailAdapter(thumbnailItems,this,getActivity());
        recyclerView=itemView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()
                ,LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int space= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,
                getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new SpacesItemDecoration(space));
        recyclerView.setAdapter(Adapter);



        DisplayThumbNail(null);



        return itemView;
    }

    public void DisplayThumbNail(final Bitmap bitmap) {

        Runnable r= new Runnable() {
            @Override
            public void run() {
                Bitmap thumbImage;
                if(bitmap==null){
                    thumbImage=BitmapUtils.getBitmapFromAssets(getActivity(),FilterActivity.pictureName,100,100);
                }else{
                    thumbImage=Bitmap.createScaledBitmap(bitmap,100,100,false);
                }

                if(thumbImage==null) return;
                ThumbnailsManager.clearThumbs();
                thumbnailItems.clear();

                //add normal bitmap first:
                ThumbnailItem thumbnailItem= new ThumbnailItem();
                thumbnailItem.image=thumbImage;
                thumbnailItem.filterName="Normal";
                ThumbnailsManager.addThumb(thumbnailItem);

                List<Filter> Filters = FilterPack.getFilterPack(getActivity());

                for (Filter filter: Filters){
                    ThumbnailItem thumbnailItem1= new ThumbnailItem();
                    thumbnailItem1.image=thumbImage;
                    thumbnailItem1.filter=filter;
                    thumbnailItem1.filterName=filter.getName();
                    ThumbnailsManager.addThumb(thumbnailItem1);

                }

                thumbnailItems.addAll(ThumbnailsManager.processThumbs(getActivity()));



                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Adapter.notifyDataSetChanged();
                    }
                });

            }
        };
        new Thread(r).start();

    }

    @Override
    public void onFilterSelected(Filter filter) {
        if(listener!= null){
            listener.onFilterSelected(filter);
        }

    }
}
