package com.example.temucafe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.temucafe.adapters.HorizontalCoffeeShopAdapter;
import com.example.temucafe.adapters.MallAdapter;
import com.example.temucafe.models.CoffeeShop;
import com.example.temucafe.models.Mall;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private RecyclerView industrialRecyclerView, franchiseRecyclerView, mallRecyclerView;
    private HorizontalCoffeeShopAdapter industrialAdapter, franchiseAdapter;
    private MallAdapter mallAdapter;
    private List<CoffeeShop> industrialList, franchiseList;
    private List<Mall> mallList;

    // --- NEW: Views for the Best Temu Banner ---
    private CardView bestTemuCard;
    private TextView bestTemuNameLocation;
    private ShapeableImageView bestTemuImage;
    private CoffeeShop bestTemuShop; // To store the fetched shop details

    private TextView greetingTextView;
    private ImageView menuIcon;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Replace the static includes in fragment_home.xml with this RecyclerView
        industrialRecyclerView = view.findViewById(R.id.rvIndustrialCoffee);
        franchiseRecyclerView = view.findViewById(R.id.franchise_recycler_view);
        mallRecyclerView = view.findViewById(R.id.mall_recycler_view);

        // --- NEW: Find Banner Views ---
        bestTemuCard = view.findViewById(R.id.best_temu_card);
        bestTemuNameLocation = view.findViewById(R.id.best_temu_name_location);
        bestTemuImage = view.findViewById(R.id.best_temu_image);
        greetingTextView = view.findViewById(R.id.greeting);
        menuIcon = view.findViewById(R.id.menuIcon);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Setup All RecyclerViews
        loadUserProfile();
        setupIndustrialRecycler();
        setupFranchiseRecycler();
        setupMallRecycler();

        // Fetch All Data
        fetchIndustrialCoffeeShops();
        fetchFranchiseCoffeeShops();
        fetchMalls();
        fetchBestTemuOfTheWeek();

        // Setup All Listeners
        setupLogoutButton();
        setupIndustrialClickListener();
        setupFranchiseClickListener();
        setupMallClickListener();
        setupBestTemuClickListener();

        // Untuk bagian mall
//        ViewGroup mallContainer = view.findViewById(R.id.malls_category);
//        setClickListenersByTag(mallContainer, "openMall", new DetailMallFragment());
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            // Check if email is not null before processing
            if (email != null && !email.isEmpty()) {
                // Split the email at the "@" and take the first part
                String username = email.split("@")[0];
                // Set the custom greeting text
                greetingTextView.setText("What's up, " + username);
            }
        }
    }

    // --- NEW: Method to fetch the featured coffee shop ---
    private void fetchBestTemuOfTheWeek() {
        // We query the 'coffee_shop' collection for the document with our flag
        db.collection("coffee_shop")
                .whereEqualTo("bestOfTheWeek", true)
                .limit(1) // We only need one
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Get the first (and only) document from the result
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        bestTemuShop = document.toObject(CoffeeShop.class);
                        bestTemuShop.setDocumentId(document.getId());

                        // Update the banner UI
                        String nameAndLocation = bestTemuShop.getName() + "\n" + bestTemuShop.getLocation();
                        bestTemuNameLocation.setText(nameAndLocation);

                        if (getContext() != null) {
                            Glide.with(getContext())
                                    .load(bestTemuShop.getLogoIconUrl())
                                    .into(bestTemuImage);
                        }
                    } else {
                        // Handle case where no "best" is found
                        bestTemuCard.setVisibility(View.GONE);
                        Log.w("Firestore", "Could not find a 'bestOfTheWeek' document.", task.getException());
                    }
                });
    }

    // --- SETUP METHODS ---
    private void setupIndustrialRecycler() {
        industrialList = new ArrayList<>();
        // Use the new adapter
        industrialAdapter = new HorizontalCoffeeShopAdapter(getContext(), industrialList); // <-- CHANGE HERE
        industrialRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        industrialRecyclerView.setAdapter(industrialAdapter);
    }

    private void setupFranchiseRecycler() {
        franchiseList = new ArrayList<>();
        // Use the new adapter
        franchiseAdapter = new HorizontalCoffeeShopAdapter(getContext(), franchiseList); // <-- CHANGE HERE
        franchiseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        franchiseRecyclerView.setAdapter(franchiseAdapter);
    }

    private void setupMallRecycler() {
        mallList = new ArrayList<>();
        mallAdapter = new MallAdapter(getContext(), mallList);
        mallRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mallRecyclerView.setAdapter(mallAdapter);
    }

    private void fetchIndustrialCoffeeShops() {
        // Remember to use the new collection name "coffeeshop"
        db.collection("coffee_shop")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        industrialList.clear(); // Clear old data
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Map the document from Firestore to our CoffeeShop object
                            CoffeeShop coffeeShop = document.toObject(CoffeeShop.class);
                            coffeeShop.setDocumentId(document.getId()); // Store the document ID
                            industrialList.add(coffeeShop);
                        }

                        Log.d("HomeFragment", "Firestore cek" + industrialList.get(0).getName());
                        industrialAdapter.notifyDataSetChanged(); // Refresh the list
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    private void fetchFranchiseCoffeeShops() {
        db.collection("franchise_coffees")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        franchiseList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CoffeeShop coffeeShop = document.toObject(CoffeeShop.class);
                            coffeeShop.setDocumentId(document.getId());
                            franchiseList.add(coffeeShop);
                        }
                        franchiseAdapter.notifyDataSetChanged();
                    } else {
                        Log.w("Firestore", "Error getting franchise documents.", task.getException());
                    }
                });
    }

    private void fetchMalls() {
        db.collection("malls").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mallList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Mall mall = document.toObject(Mall.class);
                    mall.setDocumentId(document.getId());
                    mallList.add(mall);
                }
                mallAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setupBestTemuClickListener() {
        bestTemuCard.setOnClickListener(v -> {
            if (bestTemuShop != null) {
                // Navigate to the correct detail page
                DetailItemFragment detailFragment = new DetailItemFragment();
                Bundle args = new Bundle();
                args.putString("COFFEESHOP_ID", bestTemuShop.getDocumentId());
                detailFragment.setArguments(args);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, detailFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    private void setupIndustrialClickListener() {
        industrialAdapter.setOnItemClickListener(coffeeShop -> {
            DetailItemFragment detailFragment = new DetailItemFragment();
            Bundle args = new Bundle();
            args.putString("COFFEESHOP_ID", coffeeShop.getDocumentId());
            detailFragment.setArguments(args);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    private void setupFranchiseClickListener() {
        franchiseAdapter.setOnItemClickListener(coffeeShop -> {
            DetailFranchiseFragment detailFragment = new DetailFranchiseFragment();
            Bundle args = new Bundle();
            args.putString("FRANCHISE_ID", coffeeShop.getDocumentId());
            detailFragment.setArguments(args);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    private void setupMallClickListener() {
        mallAdapter.setOnItemClickListener(mall -> {
            MallCoffeeListFragment fragment = new MallCoffeeListFragment();
            Bundle args = new Bundle();
            args.putString("MALL_ID", mall.getDocumentId());
            args.putString("MALL_NAME", mall.getName());
            fragment.setArguments(args);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    // --- NEW: Method to handle the logout process ---
    private void setupLogoutButton() {
        menuIcon.setOnClickListener(v -> {
            // Sign the user out of Firebase
            mAuth.signOut();
            Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Create an intent to go back to the LandingActivity
            Intent intent = new Intent(getActivity(), LandingActivity.class);
            // These flags clear the entire task stack, so the user can't press "back" to get into the app
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            // Finish the current activity (MainMenuActivity)
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

}
