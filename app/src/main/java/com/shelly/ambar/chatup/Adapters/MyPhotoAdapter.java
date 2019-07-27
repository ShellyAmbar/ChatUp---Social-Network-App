package com.shelly.ambar.chatup.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.shelly.ambar.chatup.Models.PostModel;
import com.shelly.ambar.chatup.PostDetailActivity;
import com.shelly.ambar.chatup.R;

import java.util.List;

public class MyPhotoAdapter extends RecyclerView.Adapter<MyPhotoAdapter.ViewHolder>{
    private Context context;
    private List<PostModel> postModelList;

    public MyPhotoAdapter(Context context, List<PostModel> postModelList) {
        this.context = context;
        this.postModelList = postModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(context).inflate(R.layout.photos_item,viewGroup,false);
        return new MyPhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final PostModel postModel=postModelList.get(i);
        Glide.with(context).load(postModel.getPostImage()).into(viewHolder.post_image);

        viewHolder.post_image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                SharedPreferences.Editor editor=context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("postId",postModel.getPostId());
                editor.apply();

                // ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                //  new PostDetailFragment()).commit();
                Intent intent=new Intent(context,PostDetailActivity.class);
                intent.putExtra("postId",postModel.getPostId());

                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);

                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return postModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView post_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_image=itemView.findViewById(R.id.post_image);

        }
    }
}
