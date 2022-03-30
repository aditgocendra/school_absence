package com.example.schoolabsence.View.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.schoolabsence.Adapter.AdapterAbsenceRecap;
import com.example.schoolabsence.Model.ModelAbsenceUsers;
import com.example.schoolabsence.R;
import com.example.schoolabsence.Utility.GlobalFunction;
import com.example.schoolabsence.Utility.GlobalVariable;
import com.example.schoolabsence.databinding.ActivityAbsenceRecapBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AbsenceRecap extends AppCompatActivity {

    private ActivityAbsenceRecapBinding binding;
    private BottomSheetDialog bottomSheetDialog;
    private TextView textDate;

    private AdapterAbsenceRecap adapterAbsenceRecap;
    private List<ModelAbsenceUsers> listAbsenceUser;

    private long countData;
    private String key = null;
    private String day = null;
    private boolean isLoadData = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAbsenceRecapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        GlobalFunction.checkWindowSetFlag(this);

        listenerComponent();
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        String month_name = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);


        day = month_name.substring(0, 3)+" "+cal.get(Calendar.DATE)+", "+cal.get(Calendar.YEAR);
        textDate.setText(day);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerRecapAbsence.setLayoutManager(layoutManager);
        binding.recyclerRecapAbsence.setItemAnimator(new DefaultItemAnimator());

        adapterAbsenceRecap = new AdapterAbsenceRecap(this);
        binding.recyclerRecapAbsence.setAdapter(adapterAbsenceRecap);

        // get totalData in firebase
        GlobalVariable.reference.child("absence_users").child(day).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                countData = task.getResult().getChildrenCount();
                isLoadData = true;
                setDataAbsenceUsers(day);
            }else {
                Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listenerComponent() {
        binding.backBtn.setOnClickListener(view -> finish());

        bottomSheetDialog = new BottomSheetDialog(AbsenceRecap.this);
        setBottomDialogFilter();
        binding.filterBtn.setOnClickListener(view -> bottomSheetDialog.show());


        binding.recyclerRecapAbsence.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // get total item in list USERS
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.recyclerRecapAbsence.getLayoutManager();
                int totalItem = linearLayoutManager.getItemCount();
                Log.d("Total Item", String.valueOf(totalItem));
                // check scroll on bottom
                if (!binding.recyclerRecapAbsence.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE){
                    // check data item if total item < total data in database == load more data
                    if (totalItem < countData){
                        // load more data
                        if (!isLoadData){
                            isLoadData = true;
                            setDataAbsenceUsers(day);
                        }
                    }
                }
            }
        });
    }


    private void setBottomDialogFilter() {
        View viewBottomDialog = getLayoutInflater().inflate(R.layout.layout_dialog_filter_absence, null, false);
        textDate = viewBottomDialog.findViewById(R.id.text_date_tv);

        CardView cardPickDate = viewBottomDialog.findViewById(R.id.card_pick_date);
        Button finishBtn = viewBottomDialog.findViewById(R.id.finish_btn);

        Locale.setDefault(Locale.ENGLISH);
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Date Attendance");
        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        MaterialDatePicker<Long> materialDatePicker = builder.build();

        cardPickDate.setOnClickListener(view -> materialDatePicker.show(getSupportFragmentManager(), "DATE PICKER"));
        materialDatePicker.addOnPositiveButtonClickListener(selection -> textDate.setText(materialDatePicker.getHeaderText()));

        finishBtn.setOnClickListener(view -> {
            String filterDay = textDate.getText().toString();
            listAbsenceUser.clear();
            adapterAbsenceRecap.notifyDataSetChanged();
            key = null;
            isLoadData = true;
            setDataAbsenceUsers(filterDay);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(viewBottomDialog);
    }

    private void setDataAbsenceUsers(String day) {
        if (!isLoadData){
            return;
        }

        Query query;
        if (key == null){
            query = GlobalVariable.reference.child("absence_users").child(day).orderByKey().limitToFirst(10);
        }else {
            query = GlobalVariable.reference.child("absence_users").child(day).orderByKey().startAfter(key).limitToFirst(10);
        }

        isLoadData = true;
        
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listAbsenceUser = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelAbsenceUsers modelAbsenceUsers = ds.getValue(ModelAbsenceUsers.class);
                    if (modelAbsenceUsers != null){
                        modelAbsenceUsers.setKey(ds.getKey());
                        key = ds.getKey();
                        listAbsenceUser.add(modelAbsenceUsers);
                    }
                }

                Handler handler = new Handler();
                handler.postDelayed(() -> {

                    if (listAbsenceUser.size() == 0){
                        Toast.makeText(AbsenceRecap.this, "nothing user absence record for today", Toast.LENGTH_SHORT).show();
                    }else {
                        adapterAbsenceRecap.setItem(listAbsenceUser);
                        adapterAbsenceRecap.notifyDataSetChanged();
                        Log.d("Item set", "True");
                    }

                    isLoadData = false;
                }, 500);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AbsenceRecap.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}