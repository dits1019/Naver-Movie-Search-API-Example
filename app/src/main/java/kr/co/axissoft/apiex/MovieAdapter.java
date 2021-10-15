package kr.co.axissoft.apiex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ModelMovie> list_movie;

    public OnItemClickListener mOnItemClickListener = null;

    public interface OnItemClickListener {
        void onItemClick(View view, ModelMovie movie);
    }

    public void setmOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public MovieAdapter(Context context, ArrayList<ModelMovie> list_movie) {
        this.context = context;
        this.list_movie = list_movie;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.custom_item, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelMovie movie = list_movie.get(position);

        if(!movie.getImage().equals("")) {
            Glide.with(context) // View, Fragment 혹은 Activity로부터 Context를 가져옴
                    .load(movie.getImage()) // 이미지를 로드. 다양한 방법으로 이미지를 불러올 수 있음
                    .thumbnail(0.5f)
                    .into(holder.item_image); // 이미지를 보여줄 View를 지정
        } else {
            Glide.with(context)
                    .load(R.drawable.ic_launcher_foreground)
                    .thumbnail(0.5f)
                    .into(holder.item_image);
        }

       holder.item_title.setText(movie.getTitle());
        holder.item_director.setText(movie.getDirector());
        holder.movie_item.setOnClickListener(v -> {
            mOnItemClickListener.onItemClick(v, movie);
        });

    }

    @Override
    public int getItemCount() {
        return list_movie.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout movie_item;
        private ImageView item_image;
        private TextView item_title;
        private TextView item_director;

        public ViewHolder(View convertView) {
            super(convertView);

            movie_item = (LinearLayout) convertView.findViewById(R.id.movie_item);
            item_image = (ImageView) convertView.findViewById(R.id.item_image);
            item_title = (TextView) convertView.findViewById(R.id.item_title);
            item_director = (TextView) convertView.findViewById(R.id.item_director);
        }

    }



}
