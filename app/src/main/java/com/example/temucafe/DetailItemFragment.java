package com.example.temucafe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.temucafe.models.CoffeeShop;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailItemFragment extends Fragment {
    private FirebaseFirestore db;
    private String coffeeShopId;
    private CoffeeShop currentCoffeeShop;

    // UI Elements from your fragment_detail_item.xml
    private TextView greetingText, placeName, placeLocation;
    private TextView accessibilityText, serviceOptionsText, placeAddress, placeHours, parkingText;
    private ImageView headerImage;
    private LinearLayout directionsButton, menusButton, shareButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DetailItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailItemFragment newInstance(String param1, String param2) {
        DetailItemFragment fragment = new DetailItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            coffeeShopId = getArguments().getString("COFFEESHOP_ID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_item, container, false);

        // Initialize your UI views here
        greetingText = view.findViewById(R.id.greeting);
        placeName = view.findViewById(R.id.place_name);
        placeLocation = view.findViewById(R.id.place_location);
        placeAddress = view.findViewById(R.id.place_address);
        placeHours = view.findViewById(R.id.place_hours);
        accessibilityText = view.findViewById(R.id.accessibility_text); // Make sure this ID exists
        serviceOptionsText = view.findViewById(R.id.service_options_text); // Make sure this ID exists
        parkingText = view.findViewById(R.id.parking_text); // Make sure this ID exists
        headerImage = view.findViewById(R.id.header_image);
        directionsButton = view.findViewById(R.id.directions);
        menusButton = view.findViewById(R.id.menus); // Find the menus button by its ID
        shareButton = view.findViewById(R.id.share); // Find the share button by its ID

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (coffeeShopId != null) {
            fetchCoffeeShopDetails(coffeeShopId);
        }

        setupClickListeners();
        // Add Wishlist - belum diatur
        // view.findViewById(R.id.add_wishlist).setOnClickListener(...);
    }

    private void fetchCoffeeShopDetails(String docId) {
        DocumentReference docRef = db.collection("coffee_shop").document(docId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                currentCoffeeShop = documentSnapshot.toObject(CoffeeShop.class);
                if (currentCoffeeShop != null) {
                    populateUI(currentCoffeeShop);
                }
            } else {
                Log.d("Firestore", "No such document");
            }
        }).addOnFailureListener(e -> Log.d("Firestore", "get failed with ", e));
    }

    private void setupClickListeners() {
        directionsButton.setOnClickListener(v -> {
            if (currentCoffeeShop != null && currentCoffeeShop.getMapLink() != null && !currentCoffeeShop.getMapLink().isEmpty()) {
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentCoffeeShop.getMapLink()));
                startActivity(mapIntent);
            } else {
                Toast.makeText(getContext(), "Map link is not available.", Toast.LENGTH_SHORT).show();
            }
        });

        menusButton.setOnClickListener(v -> {
            if (currentCoffeeShop != null && currentCoffeeShop.getMenuLink() != null && !currentCoffeeShop.getMenuLink().isEmpty()) {
                // Open the menu link in a browser or other appropriate app
                Intent menuIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentCoffeeShop.getMenuLink()));
                startActivity(menuIntent);
            } else {
                Toast.makeText(getContext(), "Menu link is not available.", Toast.LENGTH_SHORT).show();
            }
        });

        shareButton.setOnClickListener(v -> {
            if (currentCoffeeShop != null && currentCoffeeShop.getMapLink() != null && !currentCoffeeShop.getMapLink().isEmpty()) {
                // Create the share intent
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                // Create the text to be shared
                String shareText = "Hey, let's check out " + currentCoffeeShop.getName() + "!\n\nHere's the location:\n" + currentCoffeeShop.getMapLink();

                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

                // Show the Android share sheet
                startActivity(Intent.createChooser(shareIntent, "Share location via"));
            } else {
                Toast.makeText(getContext(), "Location link is not available to share.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUI(CoffeeShop coffeeShop) {
        greetingText.setText(coffeeShop.getGreeting());
        placeName.setText(coffeeShop.getName());
        placeLocation.setText(coffeeShop.getLocation());
        placeAddress.setText(coffeeShop.getAddress());
        placeHours.setText(coffeeShop.getHours());

//         For lists, join them into a single string with line breaks
        if (coffeeShop.getAccessibility() != null) {
            accessibilityText.setText(String.join("\n", coffeeShop.getAccessibility()));
        }
        if (coffeeShop.getServiceOptions() != null) {
            serviceOptionsText.setText(String.join("\n", coffeeShop.getServiceOptions()));
        }
        if (coffeeShop.getParking() != null) {
            parkingText.setText(String.join("\n", coffeeShop.getParking()));
        }


        if (getContext() != null && coffeeShop.getLogoIconUrl() != null) {
            Glide.with(getContext())
                    .load(coffeeShop.getLogoIconUrl())
                    .into(headerImage);
        }
    }

}