package com.example.do_an.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.do_an.R;
import com.example.do_an.model.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class NapTienActivity extends AppCompatActivity {
    TextView soduviNT;
    Button btnt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nap_tien);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        SharedPreferences sharedPreferences = getSharedPreferences("my_phone", Context.MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("PHONE_NUMBER", "");
        btnt = findViewById(R.id.btNT);
        soduviNT = findViewById(R.id.soduviNT);
        btnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(soduviNT.getText() == null)
                    Toast.makeText(NapTienActivity.this, "Chưa nhập số tiền cần nạp", Toast.LENGTH_SHORT).show();
                else{
                    int sodu = Integer.parseInt(String.valueOf(soduviNT.getText()));
                    if(sodu < 10000)
                        Toast.makeText(NapTienActivity.this, "Nạp tối thiểu 10.000đ", Toast.LENGTH_SHORT).show();
                    else
                        updateToFireStore(phoneNumber,sodu);
                }
            }
        });
    }
    private void updateToFireStore(String id, int sodu) {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        // Tạo một tham chiếu đến tài khoản người dùng cần cập nhật
        DocumentReference userRef = db.collection("Users").document(id);

        // Lấy dữ liệu hiện tại từ Firestore
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        long currentSoDu = document.getLong("soDuVi").intValue();
                        long newSoDu = currentSoDu + sodu;
                        // Cập nhật số dư mới vào Firestore
                        userRef.update("soDuVi", newSoDu)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(NapTienActivity.this, "Nạp tiền thành công", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(NapTienActivity.this, "Lỗi khi nạp tiền: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(NapTienActivity.this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NapTienActivity.this, "Lỗi khi truy cập dữ liệu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}