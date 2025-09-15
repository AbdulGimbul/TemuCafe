package com.example.temucafe;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.temucafe.adapters.CoffeeShopAdapter;
import com.example.temucafe.models.CoffeeShop;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment {

    private FirebaseFirestore db;
    private EditText searchEditText;
    private RecyclerView searchResultsRecyclerView;
    private CoffeeShopAdapter adapter;

    private final List<CoffeeShop> allCoffeeShops = new ArrayList<>();
    private final List<CoffeeShop> filteredList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        db = FirebaseFirestore.getInstance();
        searchEditText = view.findViewById(R.id.search_edit_text);
        searchResultsRecyclerView = view.findViewById(R.id.search_results_recycler_view);

        setupRecyclerView();
        fetchAllCoffeeShops();
        setupSearchListener();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new CoffeeShopAdapter(getContext(), filteredList);
        searchResultsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        searchResultsRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(coffeeShop -> {
            // Navigate based on the origin of the coffee shop
            String origin = coffeeShop.getOriginType();
            if (origin == null) return;

            Bundle args = new Bundle();
            Fragment detailFragment;

            switch (origin) {
                case "industrial":
                    detailFragment = new DetailItemFragment();
                    args.putString("COFFEESHOP_ID", coffeeShop.getDocumentId());
                    break;
                case "franchise":
                    detailFragment = new DetailFranchiseFragment();
                    args.putString("FRANCHISE_ID", coffeeShop.getDocumentId());
                    break;
                case "mall":
                    detailFragment = new DetailItemFragment();
                    args.putString("MALL_ID", coffeeShop.getMallId());
                    args.putString("COFFEESHOP_ID", coffeeShop.getDocumentId());
                    break;
                default:
                    return; // Should not happen
            }

            detailFragment.setArguments(args);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void fetchAllCoffeeShops() {
        // Create tasks for each top-level collection
        Task<QuerySnapshot> industrialTask = db.collection("coffee_shop").get();
        Task<QuerySnapshot> franchiseTask = db.collection("franchise_coffees").get();
        Task<QuerySnapshot> mallsTask = db.collection("malls").get();

        // When all top-level tasks are complete, start fetching subcollections
        Tasks.whenAllSuccess(industrialTask, franchiseTask, mallsTask).addOnSuccessListener(results -> {
            allCoffeeShops.clear();

            // Process Industrial Coffee
            QuerySnapshot industrialResult = (QuerySnapshot) results.get(0);
            for (QueryDocumentSnapshot doc : industrialResult) {
                CoffeeShop shop = doc.toObject(CoffeeShop.class);
                shop.setDocumentId(doc.getId());
                shop.setOriginType("industrial");
                allCoffeeShops.add(shop);
            }

            // Process Franchise Coffee
            QuerySnapshot franchiseResult = (QuerySnapshot) results.get(1);
            for (QueryDocumentSnapshot doc : franchiseResult) {
                CoffeeShop shop = doc.toObject(CoffeeShop.class);
                shop.setDocumentId(doc.getId());
                shop.setOriginType("franchise");
                allCoffeeShops.add(shop);
            }

            // Process Malls to get their subcollections
            QuerySnapshot mallsResult = (QuerySnapshot) results.get(2);
            List<Task<QuerySnapshot>> mallShopTasks = new ArrayList<>();
            for (QueryDocumentSnapshot mallDoc : mallsResult) {
                String mallId = mallDoc.getId();
                Task<QuerySnapshot> mallShopsTask = db.collection("malls").document(mallId).collection("coffeeshops").get();
                mallShopsTask.addOnSuccessListener(mallShopsResult -> {
                    for (QueryDocumentSnapshot shopDoc : mallShopsResult) {
                        CoffeeShop shop = shopDoc.toObject(CoffeeShop.class);
                        shop.setDocumentId(shopDoc.getId());
                        shop.setOriginType("mall");
                        shop.setMallId(mallId); // Store the mall ID
                        allCoffeeShops.add(shop);
                    }
                });
                mallShopTasks.add(mallShopsTask);
            }

            // After all subcollection tasks are done, update the initial list
            Tasks.whenAll(mallShopTasks).addOnCompleteListener(task -> {
                Log.d("SearchFragment", "Total coffee shops fetched: " + allCoffeeShops.size());
                filterList(""); // Display all shops initially
            });
        });
    }

    private void setupSearchListener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterList(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(allCoffeeShops);
        } else {
            String lowerCaseQuery = query.toLowerCase(Locale.ROOT);
            for (CoffeeShop shop : allCoffeeShops) {
                // Search by name
                if (shop.getName().toLowerCase(Locale.ROOT).contains(lowerCaseQuery)) {
                    filteredList.add(shop);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}