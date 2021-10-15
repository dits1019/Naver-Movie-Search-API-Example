package kr.co.axissoft.apiex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String clientId = "lpDz4FozdlGsqfL2KfJJ";
    String clientSecret = "3RfwyMjkjp";

    ArrayList<ModelMovie> modelMovies = new ArrayList<>();

    EditText editText;
    Button btn;
    RecyclerView recyclerView;
    MovieAdapter movieAdapter;

    Handler handler = new Handler();
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        init();


        btn.setOnClickListener(v -> new Thread() {
            String responseBody;

            @Override
            public void run() {
                String text;

                try {
                    text = URLEncoder.encode(editText.getText().toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("검색어 인코딩 실패", e);
                }


                String apiURL = "https://openapi.naver.com/v1/search/movie.json?query=" + text;    // json 결과
                //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // xml 결과


                Map<String, String> requestHeaders = new HashMap<>();
                requestHeaders.put("X-Naver-Client-Id", clientId);
                requestHeaders.put("X-Naver-Client-Secret", clientSecret);
                responseBody = get(apiURL, requestHeaders);


                System.out.println("검색 결과 : " + responseBody);

                jsonParsing(responseBody);

            }
        }.start());

    }

    private static String get(String apiUrl, Map<String, String> requestHeaders) {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }


            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }


    private static HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }


    private static String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);


        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();


            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }


            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

    private void jsonParsing(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);

            JSONArray movieArray = jsonObject.getJSONArray("items");

            for(int i = 0; i < movieArray.length(); i++) {
                JSONObject movieObject = movieArray.getJSONObject(i);

                ModelMovie movie = new ModelMovie();

                movie.setTitle(removeTag(movieObject.getString("title") + "(" + movieObject.getString("subtitle") + ")"));
                movie.setDirector(movieObject.getString("director").replace('|', ' '));
                movie.setImage(movieObject.getString("image"));

                modelMovies.add(movie);
            }

            for (ModelMovie movie : modelMovies) {
                System.out.println("영화 제목 : " + movie.getTitle());
                System.out.println("영화 링크 : " + movie.getDirector());
                System.out.println("영화 이미지 : " + movie.getImage());

                handler.post(() -> {
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext, 2);
                    recyclerView.setLayoutManager(layoutManager);
                    movieAdapter = new MovieAdapter(mContext, modelMovies);
                    movieAdapter.setmOnItemClickListener((view, movie1) -> {
                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        intent.putExtra("title", movie1.getTitle());
                        intent.putExtra("image", movie1.getImage());
                        intent.putExtra("director", movie1.getDirector());
                        startActivity(intent);
                    });
                    recyclerView.addItemDecoration(new ItemDecoration(MainActivity.this));
                    recyclerView.setAdapter(movieAdapter);

                });

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        editText = findViewById(R.id.editText);
        btn = findViewById(R.id.btn);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private String removeTag(String str) {
        return str.replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>", "");
    }


}