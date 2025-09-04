// app/src/main/java/com/example/temucafe/LandingActivity.java
package com.example.temucafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LandingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view for the activity right away. This is the correct place.
        setContentView(R.layout.activity_landing);

        mAuth = FirebaseAuth.getInstance();

        // Setup the buttons immediately after the layout is set.
        setupButtons();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for an existing session every time the activity becomes visible.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // If user is logged in, skip this screen and go to the main menu.
            goToMainMenu();
        }
        // If no user is logged in, do nothing and the user will see the landing page.
    }

    private void goToMainMenu() {
        Intent intent = new Intent(LandingActivity.this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupButtons() {
        Button loginButton = findViewById(R.id.btnLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}