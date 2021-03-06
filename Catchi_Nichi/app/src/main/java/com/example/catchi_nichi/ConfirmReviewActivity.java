package com.example.catchi_nichi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfirmReviewActivity extends AppCompatActivity {
    //Retrofit
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(RetrofitAPI.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    public static final RetrofitAPI apiService = retrofit.create(RetrofitAPI.class);

    String nick;
    String img;
    String kr_name;
    String en_name;
    String brand;
    String kr_brand;
    String likes;
    String countingReview;
    String avgStars;
    String review_writer;
    String review_mood;
    String review_comment;
    Float review_star;
    Float review_longevity;
    Integer review_id;

    ImageView perfumePic;
    TextView perfumeInfo;
    TextView userWrite;
    Bitmap bitmap;
    TextView writerName;
    TextView moodText;

    RatingBar star;
    RatingBar longevity;

    String activity;
    String enterSearch;
    int getCount;
    String[] items;
    ArrayList<HashMap<String, String>> searchList;
    String category1;
    String category2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_review);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //???????????????
        Intent intent = getIntent();
        nick = intent.getStringExtra("nick");

        //perfume ??????
        img = intent.getStringExtra("img");
        kr_name = intent.getStringExtra("kr_name");
        en_name = intent.getStringExtra("en_name");
        brand = intent.getStringExtra("brand");
        kr_brand = intent.getStringExtra("kr_brand");
        likes = intent.getStringExtra("likes");
        countingReview = intent.getStringExtra("countingReview");
        avgStars = intent.getStringExtra("avgStars");

        //review ??????
        review_writer = intent.getStringExtra("review_writer");
        review_star = intent.getFloatExtra("review_star",0);
        review_longevity = intent.getFloatExtra("review_longevity",0);
        review_mood = intent.getStringExtra("review_mood");
        review_comment = intent.getStringExtra("review_comment");
        review_id = intent.getIntExtra("review_id",0);

        //????????????
        activity = intent.getStringExtra("Activity");
        searchList = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("searchList");
        try{
            enterSearch = intent.getStringExtra("enterSearch");
            getCount = intent.getIntExtra("getCount", 0);
            items = intent.getStringArrayExtra("autoSearchItem");
            category1 =intent.getStringExtra("category1");
            category2 =intent.getStringExtra("category2");}
        catch (Exception e){
            e.printStackTrace();
        }

        perfumePic = findViewById(R.id.imageView);
        perfumeInfo = findViewById(R.id.imageText);
        userWrite = findViewById(R.id.userWrite);
        userWrite.setText(review_comment);
        //textView??? ?????????
        userWrite.setMovementMethod(new ScrollingMovementMethod());
        writerName = findViewById(R.id.writerName);
        moodText = findViewById(R.id.moodText);
        star = findViewById(R.id.starRating);
        longevity = findViewById(R.id.longevityRating);

        star.setRating(review_star);
        star.setIsIndicator(true);

        longevity.setRating(review_longevity);
        longevity.setIsIndicator(true);

        Thread mThread = new Thread(){
            public void run(){
                try{
                    URL url = new URL(img);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();

        try{
            mThread.join();
            perfumePic.setImageBitmap(bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        perfumeInfo.setText("\n  " + kr_name + "\n  " + brand);
        writerName.setText(review_writer);
        moodText.setText("??? ????????? "+review_mood+ " ????????? ?????????.");

    }

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ok_btn:

                if(activity.equals("myPage")){
                    Intent intent8 = new Intent(getApplicationContext(), MyPageActivity.class);
                    intent8.putExtra("nick", nick);
                    startActivity(intent8);
                    finish();
                }
                else{
                    Intent intent2 = new Intent(getApplicationContext(), PerfumeDataActivity.class);
                    intent2.putExtra("nick", nick);
                    intent2.putExtra("img", img);
                    intent2.putExtra("kr_name", kr_name);
                    intent2.putExtra("en_name", en_name);
                    intent2.putExtra("brand", brand);
                    intent2.putExtra("kr_brand", kr_brand);
                    intent2.putExtra("likes", likes);
                    intent2.putExtra("countingReview", countingReview);
                    intent2.putExtra("avgStars", avgStars);

                    //????????????
                    //activity ??????
                    intent2.putExtra("Activity",activity);
                    intent2.putExtra("searchList",searchList);

                    try{
                        intent2.putExtra("autoSearchItem",items);
                        intent2.putExtra("getCount",getCount);
                        intent2.putExtra("enterSearch",enterSearch);
                        intent2.putExtra("category1",category1);
                        intent2.putExtra("category2",category2);}
                    catch (Exception e){
                        e.printStackTrace();
                    }

                    startActivity(intent2);
                    finish();
                }
                break;

            case R.id.modify_Btn:
                if(nick.equals(review_writer)){
                    Log.i("modifyReview","????????? ??????");

                    Intent intent = new Intent(getApplicationContext(), ModifyReviewActivity.class);
                    intent.putExtra("nick",nick);

                    //perfume ??????
                    intent.putExtra("img",img);
                    intent.putExtra("kr_name",kr_name);
                    intent.putExtra("en_name",en_name);
                    intent.putExtra("brand",brand);
                    intent.putExtra("kr_brand",kr_brand);
                    intent.putExtra("likes",likes);
                    intent.putExtra("countingReview",countingReview);
                    intent.putExtra("avgStars",avgStars);

                    //review ??????
                    intent.putExtra("review_writer",review_writer);
                    intent.putExtra("review_longevity", review_longevity);
                    intent.putExtra("review_star", review_star);
                    intent.putExtra("review_mood",review_mood);
                    intent.putExtra("review_comment",review_comment);
                    intent.putExtra("review_id",review_id);

                    //????????????
                    //activity ??????
                    intent.putExtra("Activity",activity);
                    intent.putExtra("searchList",searchList);

                    try{
                        intent.putExtra("autoSearchItem",items);
                        intent.putExtra("getCount",getCount);
                        intent.putExtra("enterSearch",enterSearch);
                        intent.putExtra("category1",category1);
                        intent.putExtra("category2",category2);}
                    catch (Exception e){
                        e.printStackTrace();
                    }

                    startActivity(intent);
                    finish();


                    startActivity(intent);
                    finish();
                }
                else{
                    Log.i("modifyReview","???????????? ???????????? ????????????");
                    Toast.makeText(getApplicationContext(), "?????? ????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

}
