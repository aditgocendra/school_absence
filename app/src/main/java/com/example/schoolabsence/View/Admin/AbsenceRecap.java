package com.example.schoolabsence.View.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.schoolabsence.Adapter.AdapterAbsenceRecap;
import com.example.schoolabsence.Adapter.AdapterMonthSelection;
import com.example.schoolabsence.Model.ModelAbsenceUsers;
import com.example.schoolabsence.Model.ModelUser;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AbsenceRecap extends AppCompatActivity {

    private ActivityAbsenceRecapBinding binding;
    private BottomSheetDialog bottomSheetDialog, bottomSheetDialogReport;
    private TextView textDate;

    private AdapterAbsenceRecap adapterAbsenceRecap;
    private AdapterMonthSelection adapterMonthSelection;
    private List<ModelAbsenceUsers> listAbsenceUser;
    private List<ModelAbsenceUsers> listReportPdf;

    private long countData;
    private String key = null;
    private String day = null;
    private boolean isLoadData = false;
    private int countLatePresence = 0;
    private int countPresence = 0;
    private String fromDate, toDate;
    private String titleReportDate;


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
                binding.totalPresence.setText(String.valueOf(countData));
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

        binding.floatGeneratePdf.setOnClickListener(view -> {
            Locale.setDefault(Locale.ENGLISH);
            MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
            builder.setTitleText("Select Date Attendance");
            builder.setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()));
            MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();
            materialDatePicker.show(getSupportFragmentManager(), "DATE PICKER RANGE");

            materialDatePicker.addOnPositiveButtonClickListener(selection -> {

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
                fromDate = sdf.format(new Date(selection.first));
                toDate = sdf.format(new Date(selection.second));

//                try {
////                    Date from = new SimpleDateFormat("dd", Locale.ENGLISH).parse(fromDate);
//                    Date to = new SimpleDateFormat("dd").parse(toDate);
//                    Toast.makeText(this, to.toString(), Toast.LENGTH_SHORT).show();
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
                getReportData();

            });
        });

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

    private void getReportData(){
       listReportPdf = new ArrayList<>();

        GlobalVariable.reference.child("absence_users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("test1", String.valueOf(snapshot.getRef()));

                for (DataSnapshot ds : snapshot.getChildren()){
                    Log.d("test2", String.valueOf(ds.getRef()));

                    for (DataSnapshot snap : ds.getChildren()){
                        ModelAbsenceUsers modelAbsenceUsers = snap.getValue(ModelAbsenceUsers.class);
                        try {
                            Date dateFrom, dateTo, date;
                            date = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).parse(modelAbsenceUsers.getDay());
                            dateFrom = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).parse(fromDate);
                            dateTo = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).parse(toDate);

                            assert date != null;
                            if (date.after(dateFrom) && date.before(dateTo)){
                                listReportPdf.add(modelAbsenceUsers);
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }
                if (listReportPdf.size() > 0){
                    Log.d("test", String.valueOf(listReportPdf.size()));
                    setUsernameReport();
//                    generateReport();
                }else{
                    Toast.makeText(AbsenceRecap.this, "Belum ada satupun data absensi", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AbsenceRecap.this, "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUsernameReport(){
        for (int i = 0; i < listReportPdf.size(); i++) {
            int finalI = i;
            GlobalVariable.reference.child("users").child(listReportPdf.get(i).getKeyUser()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    ModelUser modelUser = task.getResult().getValue(ModelUser.class);
                    listReportPdf.get(finalI).setKeyUser(modelUser.getUsername());
                    if (finalI == listReportPdf.size() - 1){
                        generateReport();
                    }
                }
            });
        }
    }

    private void generateReport(){
        int width = 1800;

        PdfDocument pdfDocument = new PdfDocument();
        Paint titlePaint = new Paint();
        Paint dataPaint = new Paint();
        Paint greenPaint = new Paint();
        Paint redPaint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, 2000, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(32);

        greenPaint.setColor(getResources().getColor(R.color.green));
        redPaint.setColor(getResources().getColor(R.color.red_calm));


        canvas.drawText("Laporan Presensi Guru, dan Staff SD Negeri 1 Sukamaju", width / 2, 120, titlePaint);
        canvas.drawText("Periode " + fromDate + " - " + toDate, width / 2, 155, titlePaint);

        dataPaint.setColor(Color.BLACK);
        dataPaint.setTextSize(30f);

        dataPaint.setStyle(Paint.Style.STROKE);
        dataPaint.setStrokeWidth(2);
        canvas.drawRect(20, 180, width - 20, 260, dataPaint);

        dataPaint.setTextAlign(Paint.Align.LEFT);
        dataPaint.setStyle(Paint.Style.FILL);

        canvas.drawText("No", 35, 230, dataPaint);
        canvas.drawText("Nama", 115, 230, dataPaint);
        canvas.drawText("Tanggal Absen", 500, 230, dataPaint);
        canvas.drawText("Masuk",730, 230, dataPaint);
        canvas.drawText("Keluar", 890, 230, dataPaint);
        canvas.drawText("Jarak (Masuk)", 1050, 230, dataPaint);
        canvas.drawText("Jarak (Keluar)", 1400, 230, dataPaint);

        canvas.drawLine(100, 190, 100, 240, dataPaint);
        canvas.drawLine(490, 190, 490, 240, dataPaint);
        canvas.drawLine(720, 190, 720, 240, dataPaint);
        canvas.drawLine(880, 190, 880, 240, dataPaint);
        canvas.drawLine(1040, 190, 1040, 240, dataPaint);
        canvas.drawLine(1390, 190, 1390, 240, dataPaint);

        int yStart = 320;
        for (int i = 0; i < listReportPdf.size(); i++){
            canvas.drawText(String.valueOf(i+1), 35, yStart, dataPaint);

            canvas.drawText(listReportPdf.get(i).getKeyUser(), 115, yStart, dataPaint);

            canvas.drawText(listReportPdf.get(i).getDay(), 500, yStart, dataPaint);

            // Time in
            String timeIn = listReportPdf.get(i).getTimeIn();
            canvas.drawText(timeIn, 730, yStart, dataPaint);
            if (!timeIn.equals("-")){
                if (Integer.parseInt(timeIn.substring(0, 2)) < 11){
                    canvas.drawCircle(840, yStart - 10, 13, greenPaint);
                }else{
                    canvas.drawCircle(840, yStart - 10, 13, redPaint);
                }
            }

            // Time Out
            String timeOut = listReportPdf.get(i).getTimeOut();
            canvas.drawText(timeOut, 890, yStart, dataPaint);

            if (!timeOut.equals("-")) {
                if (Integer.parseInt(timeOut.substring(0, 2)) >= 15){
                    canvas.drawCircle(1000, yStart - 10, 13, redPaint);
                }else{
                    canvas.drawCircle(1000, yStart - 10, 13, greenPaint);
                }
            }
            canvas.drawText(listReportPdf.get(i).getDistanceIn(), 1050, yStart, dataPaint);
            canvas.drawText(listReportPdf.get(i).getDistanceOut(), 1400, yStart, dataPaint);

            yStart += 40;
        }

        pdfDocument.finishPage(page);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/Laporan-Absensi-Guru.pdf");
        Log.d("File", file.getAbsolutePath());
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "Laporan PDF berhasil dibuat", Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            e.printStackTrace();
            Log.d("Error Failed", e.getMessage());
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }
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
            countLatePresence = 0;
            countPresence = 0;
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


                        // Set late data
                        if (!modelAbsenceUsers.getTimeIn().equals("-")){
                            String hourAbsenceIn = modelAbsenceUsers.getTimeIn().substring(0, 2);

                            if (Integer.parseInt(hourAbsenceIn) >= 11){
                                countLatePresence += 1;
                            }else{
                                countPresence += 1;
                            }
                        }

                        if (!modelAbsenceUsers.getTimeOut().equals("-")){
                            String hourAbsenceIn = modelAbsenceUsers.getTimeOut().substring(0, 2);

                            if (Integer.parseInt(hourAbsenceIn) >= 15){
                                countLatePresence += 1;
                            }else{
                                countPresence += 1;
                            }
                        }
                    }
                }

                Handler handler = new Handler();
                handler.postDelayed(() -> {

                    if (listAbsenceUser.size() == 0){
                        Toast.makeText(AbsenceRecap.this, "nothing user absence record for today", Toast.LENGTH_SHORT).show();
                    }else {
                        binding.totalLatePresence.setText(String.valueOf(countLatePresence));
                        binding.totalPresence.setText(String.valueOf(countPresence));
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