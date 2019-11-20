package com.saginsoft.firebasesohbet;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText editEposta, editSifre;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        setTitle("Giriş Yap");

        firebaseAuth = FirebaseAuth.getInstance();

        editEposta = findViewById(R.id.editEposta);
        editSifre = findViewById(R.id.editSifre);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.btnGiris).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editEposta.getText().toString().isEmpty() || editSifre.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Tüm alanları doldurnuz", Toast.LENGTH_LONG).show();
                    return;
                }

                String eposta = editEposta.getText().toString();
                String sifre = editSifre.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.signInWithEmailAndPassword(eposta, sifre)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                progressBar.setVisibility(View.GONE);

                                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getBaseContext(), "Hata!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });

        findViewById(R.id.btnKaydol).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), SignUpActivity.class));
            }
        });
    }
}
