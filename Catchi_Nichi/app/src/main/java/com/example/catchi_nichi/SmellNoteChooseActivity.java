package com.example.catchi_nichi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SmellNoteChooseActivity extends AppCompatActivity {
    //Retrofit
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(RetrofitAPI.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    public static final RetrofitAPI apiService = retrofit.create(RetrofitAPI.class);

    String nick;
    EditText searchText;
    String[] items;
    LinearLayout resultView;
    Bitmap bitmap;
    ArrayList<HashMap<String, String>> searchList ;
    int temp;
    int checkedImageId= 999;
    ImageButton perfumeImageBtn;
    LinearLayout group;
    TextView resultText;
    TextView selectedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smellnote_chooseperfume);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //???????????????
        Intent intent = getIntent();
        nick = intent.getStringExtra("nick");

        //item
        Call<Post> search = apiService.searchAPI("","likes", 999,0,1);
        search.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                items = new String[response.body().getCount()];
                for(int i=0; i<response.body().getCount();i++){
                    items[i]=(response.body().getSearchList().get(i).get("kr_name"));
                }
                Adapter();
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

            }
        });

        searchText = findViewById(R.id.search);
        resultView = findViewById(R.id.Info);

        resultText = findViewById(R.id.resultView);
        selectedText = findViewById(R.id.selectedView);
        selectedText.setTypeface(Typeface.DEFAULT_BOLD);

        //????????? ?????????
        ConstraintLayout layout = findViewById(R.id.layout);
        layout.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
        });
    }

    public void Adapter(){
        ArrayAdapter<String> adWord = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, items);
        AutoCompleteTextView edit = (AutoCompleteTextView) findViewById(R.id.search);
        edit.setAdapter(adWord);
    }


    public void onClick(View v) {

        switch (v.getId()){

            case R.id.layout:
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                break;

            case R.id.searchButton:

                Call<Post> search = apiService.searchAPI(searchText.getText().toString(),"likes", 999,0,1);
                search.enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {
                        Log.i("registerSmellNote","success");
                        resultText.setText(response.body().getCount()+" ?????? ????????? ?????????????????????.");
                        searchList = response.body().getSearchList();
                        draw();
                    }

                    @Override
                    public void onFailure(Call<Post> call, Throwable t) {
                        Log.i("registerSmellNote","fail");
                        t.printStackTrace();
                    }


                });
                break;

            case R.id.cancel_btn:
                Intent intent = new Intent(getApplicationContext(), SmellNoteMainActivity.class);
                intent.putExtra("nick", nick);
                startActivity(intent);
                finish();
                break;

            case R.id.choose_Btn:
                if(checkedImageId==999){
                    Toast.makeText(getApplicationContext(), "??????????????? ????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), searchList.get(checkedImageId).get("kr_name")+" \n ????????? ??????????????? ???????????????.", Toast.LENGTH_SHORT).show();
                    Intent intent2 = new Intent(getApplicationContext(), SmellNoteRegisterActivity.class);

                    //??????????????????
                    intent2.putExtra("pickPurfume",checkedImageId);
                    intent2.putExtra("searchList",searchList);
                    intent2.putExtra("nick", nick);
                    startActivity(intent2);
                    finish();
                }
                break;


        }

    }

    void draw(){

        //?????? ???????????? ??????
        resultView.removeAllViews();

        final int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
        final int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130, getResources().getDisplayMetrics());
        final int textWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 210, getResources().getDisplayMetrics());
        final int textHeight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130, getResources().getDisplayMetrics());

        for(temp=0;temp<searchList.size();temp++){

            perfumeImageBtn = new ImageButton(this);
            perfumeImageBtn.setLayoutParams(new LinearLayout.LayoutParams(width,height));
            perfumeImageBtn.setId(temp);
            perfumeImageBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            perfumeImageBtn.setBackgroundColor(Color.parseColor("#FFFFFF"));
            perfumeImageBtn.setOnClickListener(v -> {
                checkedImageId = v.getId();
                selectedText.setText(searchList.get(checkedImageId).get("brand") +"??? '"+searchList.get(checkedImageId).get("kr_name")+"' ??? \n?????????????????????.");
                Log.i("selected perfume: ",searchList.get(checkedImageId).get("kr_name"));
            });

            group = new LinearLayout(this);
            group.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(5,15,5,15);
            group.setLayoutParams(params);

            Thread mThread = new Thread(){
                public void run(){
                    try{
                        URL url = new URL(searchList.get(temp).get("img"));
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
                perfumeImageBtn.setImageBitmap(bitmap);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            TextView perfumeInfo = new TextView(this);
            perfumeInfo.setGravity(Gravity.CENTER);
            perfumeInfo.setLayoutParams(new LinearLayout.LayoutParams(textWidth,textHeight));
            perfumeInfo.setText("\n  "+ searchList.get(temp).get("kr_name") + "\n  " + searchList.get(temp).get("brand") + "\n  " + "Likes : " + searchList.get(temp).get("likes")+ "\n  "+ "????????? : " + searchList.get(temp).get("countingReview")+ "\n  "+ "???????????? : " + searchList.get(temp).get("avgStars")+ "\n");
            //perfumeInfo.setTypeface(Typeface.DEFAULT_BOLD);
            group.setGravity(Gravity.CENTER);

            group.addView(perfumeImageBtn);
            group.addView(perfumeInfo);
            resultView.addView(group);
        }

        Log.i("searchResult", String.valueOf(searchList));

}
}