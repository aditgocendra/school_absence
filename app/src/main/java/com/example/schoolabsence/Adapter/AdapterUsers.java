package com.example.schoolabsence.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schoolabsence.Model.ModelUser;
import com.example.schoolabsence.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.UsersViewHolder> {

    private final List<ModelUser> listUsers = new ArrayList<>();
    private Context mContext;

    public AdapterUsers(Context mContext) {
        this.mContext = mContext;
    }

    public void setItem(List<ModelUser> listUsers){
        this.listUsers.addAll(listUsers);
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_card_user, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        ModelUser modelUser = listUsers.get(position);

        if (!modelUser.getUrl_photo().equals("-")){
            Picasso.get().load(modelUser.getUrl_photo()).into(holder.accountImage);
        }

        holder.usernameTv.setText("Username : "+modelUser.getUsername());
        holder.nikTv.setText("NIK : "+modelUser.getNik());
    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        ImageView accountImage;
        TextView usernameTv, nikTv;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);

            accountImage = itemView.findViewById(R.id.account_photo);
            usernameTv = itemView.findViewById(R.id.username_tv);
            nikTv = itemView.findViewById(R.id.nik_tv);

        }
    }
}
