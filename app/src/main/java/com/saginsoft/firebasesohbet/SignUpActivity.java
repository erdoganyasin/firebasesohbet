package com.saginsoft.firebasesohbet;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    ProgressBar progressBar;
    EditText editName, editEposta, editSifre, editSifreTekrar;
    CircleImageView circleImageView;
    Uri uriPhoto;

    FirebaseAuth firebaseAuth;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Üye Ol");

        editName = findViewById(R.id.editName);
        editEposta = findViewById(R.id.editEposta);
        editSifre = findViewById(R.id.editSifre);
        editSifreTekrar = findViewById(R.id.ediSifreTekrar);
        circleImageView = findViewById(R.id.circleImageView);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            }
        }

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 100);
            }
        });

        findViewById(R.id.btnKaydol).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uriPhoto == null ||
                        editName.getText().toString().isEmpty() ||
                        editEposta.getText().toString().isEmpty() ||
                        editSifre.getText().toString().isEmpty() ||
                        editSifreTekrar.getText().toString().isEmpty()) {

                    Toast.makeText(getBaseContext(), "Tüm alanları doldurunuz", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!editSifre.getText().toString().equals(editSifreTekrar.getText().toString())) {
                    Toast.makeText(getBaseContext(), "Şifre tekrarları uyuşmuyor", Toast.LENGTH_LONG).show();
                    return;
                }

                String eposta = editEposta.getText().toString();
                String sifre = editSifre.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(eposta, sifre)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                setUserInfo();
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
    }

    void setUserInfo() {
        UUID uuid = UUID.randomUUID();
        final String path = "image/" + uuid + ".jpg";

        storageReference.child(path).putFile(uriPhoto)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        storageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String uid = firebaseAuth.getUid();
                                String name = editName.getText().toString();
                                String url = uri.toString();

                                databaseReference.child(Child.users).push().setValue(
                                        new UserInfo(uid, name, url)
                                );

                                progressBar.setVisibility(View.GONE);

                                Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK)
            return;

        if (requestCode == 100 && data != null) {
            uriPhoto = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriPhoto);
                circleImageView.setImageBitmap(bitmap);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}

