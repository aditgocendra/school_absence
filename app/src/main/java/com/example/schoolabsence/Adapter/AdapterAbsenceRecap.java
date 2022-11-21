package com.example.schoolabsence.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolabsence.Model.ModelAbsenceUsers;
import com.example.schoolabsence.Model.ModelUser;
import com.example.schoolabsence.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdapterAbsenceRecap extends RecyclerView.Adapter<AdapterAbsenceRecap.AbsenceRecapViewHolder> {

    private List<ModelAbsenceUsers> listAbsenceUser = new ArrayList<>();
    private final Context mContext;

    private BottomSheetDialog bottomSheetDialog;

    public AdapterAbsenceRecap(Context mContext) {
        this.mContext = mContext;
    }

    public void setItem(List<ModelAbsenceUsers> listAbsenceUser){
        this.listAbsenceUser = listAbsenceUser;
    }

    @NonNull
    @Override
    public AbsenceRecapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_card_user, parent, false);
        return new AbsenceRecapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AbsenceRecapViewHolder holder, int position) {
        ModelAbsenceUsers modelAbsenceUsers = listAbsenceUser.get(position);
        getDataUsers(modelAbsenceUsers.getKeyUser(), holder);

        holder.cardUser.setOnClickListener(view -> {
            bottomSheetDialog = new BottomSheetDialog(mContext);
            setBottomDialogDetail(modelAbsenceUsers);
            bottomSheetDialog.show();
        });

        holder.layoutTimeIn.setVisibility(View.VISIBLE);
        holder.layoutTimeOut.setVisibility(View.VISIBLE);

        String hourAbsenceIn = modelAbsenceUsers.getTimeIn();
        String hourAbsenceOut = modelAbsenceUsers.getTimeOut();

        if (hourAbsenceIn.equals("-")){
            holder.indicatorPresenceTimeIn.setColorFilter(mContext.getResources().getColor(R.color.white));
        }else{
            if (Integer.parseInt(hourAbsenceIn.substring(0,2)) >= 11){
                holder.indicatorPresenceTimeIn.setColorFilter(mContext.getResources().getColor(R.color.red_calm));
            }
        }

//        if (!hourAbsenceIn.equals("-") && Integer.parseInt(hourAbsenceIn.substring(0,2)) >= 11 ){
//            holder.indicatorPresenceTimeIn.setColorFilter(mContext.getResources().getColor(R.color.red_calm));
//        }

        if (hourAbsenceOut.equals("-")){
            holder.indicatorPresenceTimeOut.setColorFilter(mContext.getResources().getColor(R.color.white));
        }else{
            if (Integer.parseInt(hourAbsenceOut.substring(0,2)) >= 15){
                holder.indicatorPresenceTimeOut.setColorFilter(mContext.getResources().getColor(R.color.red_calm));
            }
        }

//        if (!hourAbsenceOut.equals("-") && Integer.parseInt(hourAbsenceOut.substring(0,2)) >= 15 ){
//            holder.indicatorPresenceTimeOut.setColorFilter(mContext.getResources().getColor(R.color.red_calm));
//        }
    }

    @Override
    public int getItemCount() {
        return listAbsenceUser.size();
    }

    public static class AbsenceRecapViewHolder extends RecyclerView.ViewHolder {
        ImageView accountImage, indicatorPresenceTimeIn, indicatorPresenceTimeOut;
        TextView usernameTv, nikTv;
        CardView cardUser;
        LinearLayout layoutTimeIn, layoutTimeOut;
        public AbsenceRecapViewHolder(@NonNull View itemView) {
            super(itemView);

            accountImage = itemView.findViewById(R.id.account_photo);
            usernameTv = itemView.findViewById(R.id.username_tv);
            nikTv = itemView.findViewById(R.id.nik_tv);
            cardUser = itemView.findViewById(R.id.card_user);
            layoutTimeIn = itemView.findViewById(R.id.time_in_layout);
            layoutTimeOut = itemView.findViewById(R.id.time_out_layout);
            indicatorPresenceTimeIn = itemView.findViewById(R.id.indicator_presence_time_in);
            indicatorPresenceTimeOut = itemView.findViewById(R.id.indicator_presence_time_out);
        }
    }

    private void getDataUsers(String keyUser, AbsenceRecapViewHolder holder){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(keyUser).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ModelUser modelUser = task.getResult().getValue(ModelUser.class);
                if (modelUser != null){
                    holder.usernameTv.setText(modelUser.getUsername());
                    if (!modelUser.getUrl_photo().equals("-")){
                        Picasso.get().load(modelUser.getUrl_photo()).into(holder.accountImage);
                    }
                }else {
                    Toast.makeText(mContext, "Not data user found", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(mContext, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setBottomDialogDetail(ModelAbsenceUsers modelAbsenceUsers){
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewBottomDialog = li.inflate(R.layout.layout_dialog_detail_absence, null, false);

        TextView textDateAttend, textTimeAttendIn,textTimeAttendOut, textDistanceAttendIn, textDistanceAttendOut;
        textDateAttend = viewBottomDialog.findViewById(R.id.text_date_attend);
        textTimeAttendIn = viewBottomDialog.findViewById(R.id.text_time_attend_in);
        textTimeAttendOut = viewBottomDialog.findViewById(R.id.text_time_attend_out);
        textDistanceAttendIn = viewBottomDialog.findViewById(R.id.text_distance_attend_in);
        textDistanceAttendOut = viewBottomDialog.findViewById(R.id.text_distance_attend_out);


        Button closeBtn = viewBottomDialog.findViewById(R.id.close_btn);

        textDateAttend.setText(modelAbsenceUsers.getDay());
        textTimeAttendIn.setText(modelAbsenceUsers.getTimeIn());
        textTimeAttendOut.setText(modelAbsenceUsers.getTimeOut());
        textDistanceAttendIn.setText(modelAbsenceUsers.getDistanceIn());
        textDistanceAttendOut.setText(modelAbsenceUsers.getDistanceOut());

        closeBtn.setOnClickListener(view -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setContentView(viewBottomDialog);

    }

}
