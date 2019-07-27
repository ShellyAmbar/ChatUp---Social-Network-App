package com.shelly.ambar.chatup.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shelly.ambar.chatup.Models.CardModel;
import com.shelly.ambar.chatup.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpCardsAdapter extends PagerAdapter {

    private List<CardModel> cardModelList;
    private LayoutInflater layoutInflater;
    private Context context;

    public SignUpCardsAdapter(List<CardModel> cardModelList, Context context) {
        this.cardModelList = cardModelList;
        this.context = context;
    }


    @Override
    public int getCount() {
        return cardModelList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.card_item,container,false);

        TextView description=view.findViewById(R.id.description);
        TextView title=view.findViewById(R.id.title);
        CircleImageView imageView=view.findViewById(R.id.image);
        imageView.setImageResource(cardModelList.get(position).getImage());
        title.setText(cardModelList.get(position).getTitle());
        description.setText(cardModelList.get(position).getDescription());
        container.addView(view,0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}