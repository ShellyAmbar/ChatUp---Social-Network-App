package com.shelly.ambar.chatup.Utils;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int Space;

    public SpacesItemDecoration(int space) {
        Space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent
            , @NonNull RecyclerView.State state) {
        if(parent.getChildAdapterPosition(view)== state.getItemCount()-1){
            outRect.left=Space;
            outRect.right=0;
        }else{
            outRect.left=0;
            outRect.right=Space;
        }
    }
}
