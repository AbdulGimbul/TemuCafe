package com.example.temucafe;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.temucafe.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText etUsername, etEmail, etPassword, etConfirmPassword;
    ImageView ivTogglePassword, ivToggleConfirmPassword;
    Button btnSignUp, btnGoToLogin;
    FirebaseAuth mAuth;

    boolean isPasswordVisible = false;
    boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        ivToggleConfirmPassword = findViewById(R.id.ivToggleConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp); // pastikan ID di XML sudah btnSignUp
        btnGoToLogin = findViewById(R.id.btnGoToLogin);

        ivTogglePassword.setOnClickListener(view -> {
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.login_ic_eye_open);
            } else {
                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.login_ic_eye_closed);
            }
            isPasswordVisible = !isPasswordVisible;
            etPassword.setSelection(etPassword.getText().length());
        });

        ivToggleConfirmPassword.setOnClickListener(view -> {
            if (isConfirmPasswordVisible) {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivToggleConfirmPassword.setImageResource(R.drawable.login_ic_eye_open);
            } else {
                etConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivToggleConfirmPassword.setImageResource(R.drawable.login_ic_eye_closed);
            }
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });

        btnSignUp.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(SignUpActivity.this, "Password tidak cocok", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = task.getResult().getUser();
                                if (user != null) {
                                    User newUser = new User(username, email);

                                    // ✅ Tambahkan listener DI SINI
                                    FirebaseDatabase.getInstance("https://temucafe-62d46902-default-rtdb.asia-southeast1.firebasedatabase.app")
                                            .getReference("Users")
                                            .child(user.getUid())
                                            .setValue(newUser)
                                            .addOnCompleteListener(userSaveTask -> {
                                                if (userSaveTask.isSuccessful()) {
                                                    // lanjut simpan mapping username → email
                                                    FirebaseDatabase.getInstance().getReference("Usernames")
                                                            .child(username)
                                                            .setValue(email)
                                                            .addOnCompleteListener(dbTask -> {
                                                                if (dbTask.isSuccessful()) {
                                                                    Toast.makeText(SignUpActivity.this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(SignUpActivity.this, "Gagal simpan username: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                } else {
                                                    Toast.makeText(SignUpActivity.this, "Gagal simpan user: " + userSaveTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }
                            } else {
                                Toast.makeText(SignUpActivity.this, "Registrasi gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        btnGoToLogin.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
