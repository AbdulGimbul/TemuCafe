package com.example.temucafe;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.temucafe.adapters.CoffeeShopAdapter;
import com.example.temucafe.models.CoffeeShop;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    // For Industrial Coffee Shops
    private RecyclerView industrialRecyclerView;
    private CoffeeShopAdapter industrialAdapter;
    private List<CoffeeShop> industrialList;

    // For Franchise Coffee Shops
    private RecyclerView franchiseRecyclerView;
    private CoffeeShopAdapter franchiseAdapter; // We can reuse the same adapter
    private List<CoffeeShop> franchiseList;

    private FirebaseFirestore db;

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

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        industrialRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        industrialList = new ArrayList<>();
        industrialAdapter = new CoffeeShopAdapter(getContext(), industrialList);
        industrialRecyclerView.setAdapter(industrialAdapter);

        franchiseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        franchiseList = new ArrayList<>();
        franchiseAdapter = new CoffeeShopAdapter(getContext(), franchiseList);
        franchiseRecyclerView.setAdapter(franchiseAdapter);

        fetchFranchiseCoffeeShops();
        fetchIndustrialCoffeeShops();
        setupIndustrialClickListener();
        setupFranchiseClickListener();

        // Untuk bagian mall
        ViewGroup mallContainer = view.findViewById(R.id.malls_category);
        setClickListenersByTag(mallContainer, "openMall", new DetailMallFragment());
    }

    private void setClickListenersByTag(ViewGroup container, String tagToMatch, Fragment fragmentToOpen) {
        if (container == null) return;

        int count = container.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = container.getChildAt(i);
            if (child instanceof CardView) {
                Object tag = child.getTag();
                if (tag != null && tag.equals(tagToMatch)) {
                    child.setOnClickListener(v -> {
                        FragmentTransaction transaction = requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction();
                        transaction.replace(R.id.fragment_container, fragmentToOpen);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    });
                }
            }
        }
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

                        Log.d("HomeFragment","Firestore cek" + industrialList.get(0).getName());
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

}
