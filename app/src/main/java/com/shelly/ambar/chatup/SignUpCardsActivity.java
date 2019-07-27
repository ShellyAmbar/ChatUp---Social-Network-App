package com.shelly.ambar.chatup;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.shelly.ambar.chatup.Adapters.SignUpCardsAdapter;
import com.shelly.ambar.chatup.Models.CardModel;

import java.util.ArrayList;
import java.util.List;

public class SignUpCardsActivity extends AppCompatActivity {

    private List<CardModel> cardModelList;
    private SignUpCardsAdapter cardsAdapter;
    private ViewPager viewPager;
    private Integer[] colors=null;
    private ArgbEvaluator argbEvaluator;
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_cards);



        cardModelList=new ArrayList<>();

        cardModelList.add(new CardModel("welcome to ChatUP","",R.drawable.photo_one));
        cardModelList.add(new CardModel("Meet new people in a different way!","Select who you want to follow!",R.drawable.photo_two));
        cardModelList.add(new CardModel("Keep in touch socially!","Watch their recent STORIES and POSTS.. ",R.drawable.photo_tree));
        cardModelList.add(new CardModel("And..It's easy to use..","In ChatUp you get the most accessibility chats!",R.drawable.photo_four));

        cardsAdapter=new SignUpCardsAdapter(cardModelList,this);
        viewPager=findViewById(R.id.viewPager);
        viewPager.setAdapter(cardsAdapter);
        viewPager.setPadding(130,0,130,0);
        argbEvaluator=new ArgbEvaluator();
        button=findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SignUpCardsActivity.this, SignUpActivity.class));
                finish();
            }
        });


        Integer[]colors_temp={
                getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color4),
                getResources().getColor(R.color.color5)

        };
        colors=colors_temp;

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if(position<cardsAdapter.getCount()-1 && position< colors.length -1){
                    viewPager.setBackgroundColor((Integer)argbEvaluator
                            .evaluate(positionOffset,
                                    colors[position],
                                    colors[position+1])
                    );
                }else{
                    viewPager.setBackgroundColor(colors[colors.length-1]);
                }
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
