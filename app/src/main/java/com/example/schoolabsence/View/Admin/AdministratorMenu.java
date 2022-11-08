package com.example.schoolabsence.View.Admin;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.schoolabsence.R;
import com.example.schoolabsence.Utility.GlobalFunction;
import com.example.schoolabsence.Utility.GlobalVariable;
import com.example.schoolabsence.databinding.ActivityAdministratorMenuBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Objects;

public class AdministratorMenu extends AppCompatActivity {

    private ActivityAdministratorMenuBinding binding;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdministratorMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        GlobalFunction.checkWindowSetFlag(this);

        listenerComponent();

    }

    private void listenerComponent() {
        binding.backBtn.setOnClickListener(view -> finish());
        binding.cardLocationSchoolSettings.setOnClickListener(view -> GlobalFunction.updateUI(AdministratorMenu.this, LocationSchoolSettings.class));
        binding.cardAllUser.setOnClickListener(view -> GlobalFunction.updateUI(AdministratorMenu.this, Users.class));
        binding.cardAbsenceRecap.setOnClickListener(view -> GlobalFunction.updateUI(AdministratorMenu.this, AbsenceRecap.class));
        binding.cardAlgorithm.setOnClickListener(view -> GlobalFunction.updateUI(AdministratorMenu.this, CompareDistance.class));

        binding.cardDistance.setOnClickListener(view -> {
            bottomSheetDialog = new BottomSheetDialog(AdministratorMenu.this);
            setBottomDialogDistance();
            bottomSheetDialog.show();
        });

    }

    private void setBottomDialogDistance(){
        View viewBottomDialog = getLayoutInflater().inflate(R.layout.layout_dialog_distance_settings, null, false);
        TextInputEditText maxDistanceTi = viewBottomDialog.findViewById(R.id.max_distance_ti);
        Button finishBtn = viewBottomDialog.findViewById(R.id.finish_btn);

        GlobalVariable.reference.child("max_distance").child("value").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Float max_distance = task.getResult().getValue(Float.class);
                maxDistanceTi.setText(String.valueOf(max_distance));
            }else {
                Toast.makeText(AdministratorMenu.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        finishBtn.setOnClickListener(view -> {
            if (maxDistanceTi.getText().toString().isEmpty()){
                Toast.makeText(this, "Max distance is null", Toast.LENGTH_SHORT).show();
            }else {
                float maxDistance = Float.parseFloat(maxDistanceTi.getText().toString());
                GlobalVariable.reference.child("max_distance").child("value").setValue(maxDistance)
                        .addOnSuccessListener(unused -> Toast.makeText(AdministratorMenu.this, "Maximum distance is change", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(AdministratorMenu.this, e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(viewBottomDialog);

    }
}