package com.example.temucafe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.temucafe.models.CoffeeShop; // We can reuse the CoffeeShop model
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailFranchiseFragment extends Fragment {

    private FirebaseFirestore db;
    private String franchiseId;
    private CoffeeShop currentFranchise;

    // UI Elements
    private TextView greetingText, placeName, placeLocation, placeAddress, placeHours;
    private TextView accessibilityText, serviceOptionsText, parkingText;
    private ImageView headerImage, logoIcon;
    private LinearLayout directionsButton, menusButton, shareButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            franchiseId = getArguments().getString("FRANCHISE_ID");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_franchise, container, false);

        // Initialize Views
        greetingText = view.findViewById(R.id.greeting);
        placeName = view.findViewById(R.id.place_name);
        placeLocation = view.findViewById(R.id.place_location);
        placeAddress = view.findViewById(R.id.place_address);
        placeHours = view.findViewById(R.id.place_hours);
        accessibilityText = view.findViewById(R.id.accessibility_text);
        serviceOptionsText = view.findViewById(R.id.service_options_text);
        parkingText = view.findViewById(R.id.parking_text);
        headerImage = view.findViewById(R.id.header_image);
        logoIcon = view.findViewById(R.id.logoIcon);
        directionsButton = view.findViewById(R.id.directions);
        menusButton = view.findViewById(R.id.menus);
        shareButton = view.findViewById(R.id.share);

        if (franchiseId != null && !franchiseId.isEmpty()) {
            fetchFranchiseDetails(franchiseId);
        } else {
            Log.e("DetailFranchiseFragment", "Franchise ID is null or empty!");
        }

        setupClickListeners();

        return view;
    }

    private void fetchFranchiseDetails(String docId) {
        // Fetch from the 'franchise_coffees' collection
        DocumentReference docRef = db.collection("franchise_coffees").document(docId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                currentFranchise = documentSnapshot.toObject(CoffeeShop.class);
                if (currentFranchise != null) {
                    populateUI(currentFranchise);
                }
            }
        }).addOnFailureListener(e -> Log.e("Firestore", "Error fetching franchise", e));
    }

    private void setupClickListeners() {
        directionsButton.setOnClickListener(v -> {
            if (currentFranchise != null && currentFranchise.getMapLink() != null && !currentFranchise.getMapLink().isEmpty()) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(currentFranchise.getMapLink())));
            } else {
                Toast.makeText(getContext(), "Map link not available.", Toast.LENGTH_SHORT).show();
            }
        });

        menusButton.setOnClickListener(v -> {
            if (currentFranchise != null && currentFranchise.getMenuLink() != null && !currentFranchise.getMenuLink().isEmpty()) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(currentFranchise.getMenuLink())));
            } else {
                Toast.makeText(getContext(), "Menu link not available.", Toast.LENGTH_SHORT).show();
            }
        });

        shareButton.setOnClickListener(v -> {
            if (currentFranchise != null && currentFranchise.getMapLink() != null && !currentFranchise.getMapLink().isEmpty()) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareText = "Let's check out " + currentFranchise.getName() + "!\nLocation: " + currentFranchise.getMapLink();
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            } else {
                Toast.makeText(getContext(), "Location not available to share.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUI(CoffeeShop coffeeShop) {
        greetingText.setText(coffeeShop.getGreeting());
        placeName.setText(coffeeShop.getName());
        placeLocation.setText(coffeeShop.getLocation());
        placeAddress.setText(coffeeShop.getAddress());
        placeHours.setText(coffeeShop.getHours());

        if (coffeeShop.getAccessibility() != null) {
            accessibilityText.setText(String.join("\n", coffeeShop.getAccessibility()));
        }
        if (coffeeShop.getServiceOptions() != null) {
            serviceOptionsText.setText(String.join("\n", coffeeShop.getServiceOptions()));
        }
        if (coffeeShop.getParking() != null) {
            parkingText.setText(String.join("\n", coffeeShop.getParking()));
        }

        if (getContext() != null) {
            Glide.with(getContext()).load(coffeeShop.getBannerImageUrl()).into(headerImage);
            Glide.with(getContext()).load(coffeeShop.getLogoIconUrl()).into(logoIcon);
        }
    }
}