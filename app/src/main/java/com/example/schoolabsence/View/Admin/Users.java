package com.example.schoolabsence.View.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.example.schoolabsence.Adapter.AdapterUsers;
import com.example.schoolabsence.Model.ModelUser;
import com.example.schoolabsence.Utility.GlobalFunction;
import com.example.schoolabsence.Utility.GlobalVariable;
import com.example.schoolabsence.databinding.ActivityUsersBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Users extends AppCompatActivity {

    private ActivityUsersBinding binding;
    private AdapterUsers adapterUsers;
    private List<ModelUser> listUsers;


    private long countData;
    private String key = null;
    private boolean isLoadData = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        GlobalFunction.checkWindowSetFlag(this);

        listenerComponent();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerUsers.setLayoutManager(layoutManager);
        binding.recyclerUsers.setItemAnimator(new DefaultItemAnimator());

        adapterUsers = new AdapterUsers(this);
        binding.recyclerUsers.setAdapter(adapterUsers);

        // get totalData in firebase
        GlobalVariable.reference.child("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                countData = task.getResult().getChildrenCount();
                isLoadData = true;
                setDataUsers();
            }else {
                Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void listenerComponent() {
        binding.backBtn.setOnClickListener(view -> finish());

        binding.recyclerUsers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // get total item in list USERS
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.recyclerUsers.getLayoutManager();
                int totalItem = linearLayoutManager.getItemCount();
                Log.d("Total Item", String.valueOf(totalItem));
                // check scroll on bottom
                if (!binding.recyclerUsers.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE){
                    // check data item if total item < total data in database == load more data
                    if (totalItem < countData){
                        // load more data
                        if (!isLoadData){
                            isLoadData = true;
                            setDataUsers();
                        }
                    }
                }
            }
        });
    }

    private void setDataUsers() {
        if (!isLoadData){
            return;
        }

        Query query;
        if (key == null){
            query = GlobalVariable.reference.child("users").orderByKey().limitToFirst(10);
        }else {
            query = GlobalVariable.reference.child("users").orderByKey().startAfter(key).limitToFirst(10);
        }

        isLoadData = true;

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUsers = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (modelUser != null){
                        modelUser.setKey(ds.getKey());
                        key = ds.getKey();
                        listUsers.add(modelUser);
                    }
                }
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    if (listUsers.size() == 0){
                        Toast.makeText(Users.this, "nothing user record", Toast.LENGTH_SHORT).show();
                    }
                    adapterUsers.setItem(listUsers);
                    adapterUsers.notifyDataSetChanged();
                    Log.d("Item set", "True");
                    isLoadData = false;
                }, 500);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Users.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}