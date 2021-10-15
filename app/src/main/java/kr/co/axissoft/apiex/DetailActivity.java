package kr.co.axissoft.apiex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {
    TextView tv_title, tv_director;
    ImageView imageView;
    ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tv_title = findViewById(R.id.tv_title);
        tv_director = findViewById(R.id.tv_director);
        imageView = findViewById(R.id.img);
        imageButton = findViewById(R.id.back);

        Intent intent = getIntent();
        tv_title.setText(intent.getStringExtra("title"));
        tv_director.setText(intent.getStringExtra("director"));

        if(!intent.getStringExtra("image").equals("")) {
            Glide.with(getApplicationContext())
                    .load(intent.getStringExtra("image"))
                    .into(imageView);
        } else {
            Glide.with(getApplicationContext())
                    .load(R.drawable.ic_launcher_background)
                    .into(imageView);
        }

        imageButton.setOnClickListener(v -> {
            finish();
        });

    }
}