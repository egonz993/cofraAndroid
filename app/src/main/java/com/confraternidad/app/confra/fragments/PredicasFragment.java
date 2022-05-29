package com.confraternidad.app.confra.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.confraternidad.app.confra.MainActivity;
import com.confraternidad.app.confra.R;
import com.confraternidad.app.confra.modelos_adapters.PredicaVideoAdapter;
import com.confraternidad.app.confra.modelos_adapters.PredicaVideoModelo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PredicasFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();

    private RecyclerView recyclerView_predicas;
    private PredicaVideoAdapter recyclerView_predicasAdapter;
    private static List<PredicaVideoModelo> videoList = new ArrayList<>();

    private View popupNwVideoView = null;
    private EditText txt_NewVideoTitle, txt_NewVideoDescription, txt_NewVideoDate, txt_NewVideoLink;
    private Button btn_Save, btn_Cancel;

    private View popupShowVideoView = null;
    private ImageView img_video;
    private ImageButton btn_watchVideo;
    private CardView card_showVideo;
    private TextView txt_videoTitle, txt_videoDescription, txt_videoDate, txt_videoLink, txt_videoID;
    private CardView card_editVideo;
    private EditText txt_editVideoTitle, txt_editVideoDescription, txt_editVideoDate;
    private Button btn_editCancelar, btn_editAceptar;

    private FloatingActionButton fabNewVideo, fabDeleteVideo, fabEditVideo;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_predicas, container, false);

        final GestureDetector mGestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        recyclerView_predicas = view.findViewById(R.id.recyclerView_predicas);
        recyclerView_predicas.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView_predicasAdapter = new PredicaVideoAdapter(getActivity(), getVideoList());
        recyclerView_predicas.setAdapter(recyclerView_predicasAdapter);

        recyclerView_predicas.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }

            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                try {
                    View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                    if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                        int position = recyclerView.getChildAdapterPosition(child);

                        TextView txt_uid = child.findViewById(R.id.txt_uid_video_servicios);
                        TextView txt_title = child.findViewById(R.id.txt_title_video_servicios);
                        TextView  txt_description = child.findViewById(R.id.txt_description_video_servicios);
                        TextView txt_date = child.findViewById(R.id.txt_date_video_servicios);
                        TextView txt_link = child.findViewById(R.id.txt_link_video_servicios);

                        String uid = txt_uid.getText().toString();
                        String title = txt_title.getText().toString();
                        String description = txt_description.getText().toString();
                        String date = txt_date.getText().toString();
                        String link = txt_link.getText().toString();

                        //Toast.makeText(getActivity(), title ,Toast.LENGTH_SHORT).show();
                        ShowVideo(uid, title, description, date, link);
                        return true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });
        fabNewVideo = view.findViewById(R.id.fabNewVideo);
        fabNewVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewVideo();
            }
        });


        if(MainActivity.isAdmin){
            fabNewVideo.animate().translationY(-getResources().getDimension(R.dimen.standard_65));
            fabNewVideo.show();
        }else{
            fabNewVideo.animate().translationY(0);
            fabNewVideo.hide();
        }


        return view;
    }



    //Funciones de usuario

    private void ShowVideo(String uid, final String title, final String description, final String date, String link){

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        popupShowVideoView = layoutInflater.inflate(R.layout.popup_servicios_show_video, null);

        img_video = popupShowVideoView.findViewById(R.id.img_video);

        card_editVideo = popupShowVideoView.findViewById(R.id.card_editVideo);
        txt_videoTitle = popupShowVideoView.findViewById(R.id.txt_videoTitle);
        txt_videoDescription = popupShowVideoView.findViewById(R.id.txt_videoDescription);
        txt_videoDate = popupShowVideoView.findViewById(R.id.txt_videoDate);
        txt_videoLink = popupShowVideoView.findViewById(R.id.txt_videoLink);
        txt_videoID = popupShowVideoView.findViewById(R.id.txt_videoID);

        card_showVideo = popupShowVideoView.findViewById(R.id.card_showVideo);
        txt_editVideoTitle = popupShowVideoView.findViewById(R.id.txt_editVideoTitle);
        txt_editVideoDescription = popupShowVideoView.findViewById(R.id.txt_editVideoDescription);
        txt_editVideoDate = popupShowVideoView.findViewById(R.id.txt_editVideoDate);

        btn_editCancelar = popupShowVideoView.findViewById(R.id.btn_editCancelar);
        btn_editAceptar = popupShowVideoView.findViewById(R.id.btn_editAceptar);

        card_showVideo.setVisibility(View.VISIBLE);
        card_editVideo.setVisibility(View.GONE);

        Glide.with(this)
                .load("https://img.youtube.com/vi/"+ getShortLink(link)+"/hqdefault.jpg")
                .into(img_video);

        txt_videoID.setText(uid);
        txt_videoTitle.setText(title);
        txt_videoDescription.setText(description);
        txt_videoDate.setText(formatDate(date));
        txt_videoLink.setText(link);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setView(popupShowVideoView);
        final AlertDialog showVideo = alertDialogBuilder.create();
        showVideo.show();

        btn_watchVideo = popupShowVideoView.findViewById(R.id.btn_watchVideo);
        btn_watchVideo.setAlpha(0f);
        btn_watchVideo.setVisibility(View.VISIBLE);
        btn_watchVideo.animate()
                .alpha(1f)
                .setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));

        btn_watchVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_videoLink = popupShowVideoView.findViewById(R.id.txt_videoLink);
                txt_videoTitle = popupShowVideoView.findViewById(R.id.txt_videoTitle);

                String URL = txt_videoLink.getText().toString();
                gotoURL(URL);

                showVideo.dismiss();
            }
        });

        fabDeleteVideo = popupShowVideoView.findViewById(R.id.fabDeleteVideo);
        fabDeleteVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_videoID = popupShowVideoView.findViewById(R.id.txt_videoID);
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.logo_confra_round)
                        .setTitle("¿Desea eliminar el video?")
                        .setMessage("Esta accion es irreversible")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "Video Eliminado", Toast.LENGTH_SHORT).show();

                                txt_videoID = popupShowVideoView.findViewById(R.id.txt_videoID);
                                String uid = txt_videoID.getText().toString();
                                mDatabase.child("servicios").child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getActivity(), "Video Eliminado", Toast.LENGTH_SHORT).show();
                                        showVideo.dismiss();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        fabEditVideo = popupShowVideoView.findViewById(R.id.fabEditVideo);
        fabEditVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txt_editVideoTitle.setText(title);
                txt_editVideoDescription.setText(description);

                txt_editVideoDate.setText(date);
                txt_editVideoDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePickerDialog_editVideo();
                    }
                });

                fabEditVideo.hide();
                fabDeleteVideo.hide();

                card_showVideo.setVisibility(View.GONE);

                card_editVideo.setAlpha(0f);
                card_editVideo.setVisibility(View.VISIBLE);
                card_editVideo.animate()
                        .alpha(1f)
                        .setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
            }
        });

        btn_editCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fabEditVideo.show();
                fabDeleteVideo.show();

                card_editVideo.setVisibility(View.GONE);

                card_showVideo.setAlpha(0f);
                card_showVideo.setVisibility(View.VISIBLE);
                card_showVideo.animate()
                        .alpha(1f)
                        .setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
            }
        });

        btn_editAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fabEditVideo.show();
                fabDeleteVideo.show();

                card_editVideo.setVisibility(View.GONE);

                card_showVideo.setAlpha(0f);
                card_showVideo.setVisibility(View.VISIBLE);
                card_showVideo.animate()
                        .alpha(1f)
                        .setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));



                String title, description, date;
                title = txt_editVideoTitle.getText().toString();
                description = txt_editVideoDescription.getText().toString();
                date = txt_editVideoDate.getText().toString();

                String uid = txt_videoID.getText().toString();
                mDatabase.child("servicios").child(uid).child("title").setValue(title);
                mDatabase.child("servicios").child(uid).child("description").setValue(description);
                mDatabase.child("servicios").child(uid).child("date").setValue(date);

                showVideo.dismiss();
                showVideo.show();
                Toast.makeText(getActivity(), "Datos actualizados", Toast.LENGTH_SHORT).show();

            }
        });

        if(MainActivity.isAdmin){
            fabEditVideo.show();
            fabDeleteVideo.show();
        }else{
            fabEditVideo.hide();
            fabDeleteVideo.hide();
        }
    }

    private List<PredicaVideoModelo> getVideoList(){
        mDatabase.child("servicios").orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                videoList.clear();


                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    String uid, date, title, description, link;

                    uid = objSnapshot.getKey();
                    date = objSnapshot.child("date").getValue().toString();
                    title = objSnapshot.child("title").getValue().toString();
                    description = objSnapshot.child("description").getValue().toString();
                    link = objSnapshot.child("link").getValue().toString();

                    videoList.add(new PredicaVideoModelo(uid, date, title, description, link));
                }
                Collections.reverse(videoList);
                recyclerView_predicas.setLayoutManager(new LinearLayoutManager(getActivity()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "DatabaseError", Toast.LENGTH_SHORT).show();
            }
        });
        return videoList;
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

        //newDate = day + "/" + month + "/" + year;
        newDate = year + "/" + month + "/" + day;
        return newDate;
    }

    private String getShortLink(String link){
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



    //Funciones de administrador
    private void AddNewVideo(){
        // Create a AlertDialog Builder.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Agregar Video");
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher_round);
        alertDialogBuilder.setCancelable(false);

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        popupNwVideoView = layoutInflater.inflate(R.layout.popup_servicios_new_video, null);

        // Get user input edittext and button ui controls in the popup dialog.
        txt_NewVideoTitle = (EditText)popupNwVideoView.findViewById(R.id.txt_NewVideoTitle);
        txt_NewVideoDescription = (EditText)popupNwVideoView.findViewById(R.id.txt_NewVideoDescription);
        txt_NewVideoDate = (EditText)popupNwVideoView.findViewById(R.id.txt_NewVideoDate);
        txt_NewVideoLink = (EditText)popupNwVideoView.findViewById(R.id.txt_NewVideoLink);
        btn_Save = (Button)popupNwVideoView.findViewById(R.id.btn_Save);
        btn_Cancel = (Button)popupNwVideoView.findViewById(R.id.btn_Cancel);

        txt_NewVideoDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog_newVideo();
            }
        });


        alertDialogBuilder.setView(popupNwVideoView);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        // When user click the save user data button in the popup dialog.
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int error = 0;
                String title, description, date, link;
                title = txt_NewVideoTitle.getText().toString();
                description = txt_NewVideoDescription.getText().toString();
                date = txt_NewVideoDate.getText().toString();
                link = txt_NewVideoLink.getText().toString();

                if(title.isEmpty()){
                    txt_NewVideoTitle.setError("Este campo es requerido");
                    error++;
                }
                if(description.isEmpty()){
                    txt_NewVideoDescription.setError("Este campo es requerido");
                    error++;
                }
                if(date.isEmpty()){
                    txt_NewVideoDate.setError("Este campo es requerido");
                    error++;
                }
                if(link.isEmpty()){
                    txt_NewVideoLink.setError("Este campo es requerido");
                    error++;
                }

                if (error==0){
                    newVideo(title, description, date, link);
                    alertDialog.cancel();
                }
            }
        });

        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

    }
    private void newVideo(String title, String description, String date, String link){

        String uid = UUID.randomUUID().toString();

        Map<String, Object> map = new HashMap<>();
        map.put("date", date);
        map.put("title", title);
        map.put("description", description);
        map.put("link", link);

        mDatabase.child("servicios").child(uid).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task2) {
                if(task2.isSuccessful()){
                    Toast.makeText(getContext(), "Video agregado exitosamente", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "Error en registro", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDatePickerDialog_editVideo() {
        Calendar rightNow = Calendar.getInstance();
        int day = rightNow.get(Calendar.DAY_OF_MONTH);
        int month = rightNow.get(Calendar.MONTH);
        int year = rightNow.get(Calendar.YEAR);

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                monthOfYear++;

                String yyyy, mm, dd;
                yyyy = year + "";

                if (monthOfYear < 10){
                    mm = "0" + monthOfYear;
                }else{
                    mm = monthOfYear + "";
                }

                if (dayOfMonth < 10){
                    dd = "0" + dayOfMonth;
                }else{
                    dd = dayOfMonth + "";
                }

                txt_editVideoDate = popupShowVideoView.findViewById(R.id.txt_editVideoDate);
                txt_editVideoDate.setText(yyyy + "/" + mm + "/" + dd);

                //newDatePicker = yyyy + "/" + mm + "/" + dd;
                //Toast.makeText(getActivity(), newDatePicker, Toast.LENGTH_SHORT).show();
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), dateSetListener, year, month, day);
        datePickerDialog.show();
    }
    private void showDatePickerDialog_newVideo() {
        Calendar rightNow = Calendar.getInstance();
        int day = rightNow.get(Calendar.DAY_OF_MONTH);
        int month = rightNow.get(Calendar.MONTH);
        int year = rightNow.get(Calendar.YEAR);

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                monthOfYear++;

                String yyyy, mm, dd;
                yyyy = year + "";

                if (monthOfYear < 10){
                    mm = "0" + monthOfYear;
                }else{
                    mm = monthOfYear + "";
                }

                if (dayOfMonth < 10){
                    dd = "0" + dayOfMonth;
                }else{
                    dd = dayOfMonth + "";
                }

                txt_NewVideoDate = popupNwVideoView.findViewById(R.id.txt_NewVideoDate);
                txt_NewVideoDate.setText(yyyy + "/" + mm + "/" + dd);

                //newDatePicker = yyyy + "/" + mm + "/" + dd;
                //Toast.makeText(getActivity(), newDatePicker, Toast.LENGTH_SHORT).show();
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), dateSetListener, year, month, day);
        datePickerDialog.show();
    }

    private void gotoURL(String URL){

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getActivity(), Uri.parse(URL));
    }
}

