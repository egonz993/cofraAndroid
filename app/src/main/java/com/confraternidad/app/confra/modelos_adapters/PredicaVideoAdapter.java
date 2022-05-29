package com.confraternidad.app.confra.modelos_adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.confraternidad.app.confra.R;

import java.util.List;

public class PredicaVideoAdapter extends RecyclerView.Adapter<PredicaVideoAdapter.ViewHolder>{

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CardView video_container;
        private TextView uid, date, title, description, link;
        private ImageView imgVideo;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            video_container =  itemView.findViewById(R.id.video_container);
            uid = itemView.findViewById(R.id.txt_uid_video_servicios);
            date = itemView.findViewById(R.id.txt_date_video_servicios);
            title = itemView.findViewById(R.id.txt_title_video_servicios);
            description = itemView.findViewById(R.id.txt_description_video_servicios);
            link = itemView.findViewById(R.id.txt_link_video_servicios);
            imgVideo = itemView.findViewById(R.id.img_video_servicio);
        }

    }

    public List<PredicaVideoModelo> videoPredicaList;
    public PredicaVideoAdapter(List<PredicaVideoModelo> videoPredicaList) {
        this.videoPredicaList = videoPredicaList;
    }

    public Context context;
    public PredicaVideoAdapter(Context context, List<PredicaVideoModelo> videoPredicaList) {
        this.videoPredicaList = videoPredicaList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_predicas_video,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.uid.setText(videoPredicaList.get(position).getUid());
        holder.date.setText(formatDate(videoPredicaList.get(position).getDate()));
        holder.title.setText(videoPredicaList.get(position).getTitle());
        holder.description.setText(videoPredicaList.get(position).getDescription());
        holder.link.setText(videoPredicaList.get(position).getLink());
        holder.imgVideo.setImageResource(videoPredicaList.get(position).getImgVideo());

        String link = videoPredicaList.get(position).getLink();
        String shortLink = getshortLink(link);

        Glide.with(context)
                .load("https://img.youtube.com/vi/"+shortLink+"/hqdefault.jpg")
                .error(R.drawable.ic_video)
                .into(holder.imgVideo);


        holder.video_container.setOnLongClickListener(v -> {
            String text = holder.title.getText().toString() + "\n" +
                    "http://youtu.be/"+shortLink;

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND)
                    .putExtra(Intent.EXTRA_TEXT, text)
                    .setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            context.startActivity(shareIntent);

            return false;
        });
    }

    @Override
    public int getItemCount() {
        return videoPredicaList.size();
    }

    private String formatDate(String date){
        String newDate, year, month, day;
        String[] date_array;

        date_array = date.split("/");
        year = date_array[0];
        month = date_array[1];
        day = date_array[2];

        if (day.length()==1){
            day = "0" + day;
        }

        if (month.length()==1){
            month = "0" + month;
        }

        newDate = day + "/" + month + "/" + year;

        return newDate;
    }

    String getshortLink(String link){
        String[] longLink_array;
        String shortLink = link;

        if(shortLink.length() > 11){
            longLink_array = link.split("/");
            shortLink = longLink_array[3];

            if(shortLink.length() > 11){
                longLink_array = link.split("=");
                shortLink = longLink_array[1];

                if(shortLink.length() > 11){
                    //Toast.makeText(getActivity(), shortLink + "", Toast.LENGTH_LONG).show();
                    shortLink = shortLink.substring(0 , 11);
                    return shortLink;
                }
            }
        }

        return shortLink;
    }

}
