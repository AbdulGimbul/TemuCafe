package com.example.temucafe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.temucafe.adapters.CoffeeShopAdapter;
import com.example.temucafe.models.CoffeeShop;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MallCoffeeListFragment extends Fragment {

    private String mallId;
    private String mallName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mallId = getArguments().getString("MALL_ID");
            mallName = getArguments().getString("MALL_NAME");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mall_coffee_list, container, false);

        TextView mallTitle = view.findViewById(R.id.mall_title);
        RecyclerView recyclerView = view.findViewById(R.id.coffee_shop_list_recycler_view);

        mallTitle.setText("Shops in " + mallName);

        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

        List<CoffeeShop> coffeeShopList = new ArrayList<>();
        CoffeeShopAdapter adapter = new CoffeeShopAdapter(getContext(), coffeeShopList);
        recyclerView.setAdapter(adapter);

        FirebaseFirestore.getInstance()
                .collection("malls")
                .document(mallId)
                .collection("coffeeshops")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        coffeeShopList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CoffeeShop coffeeShop = document.toObject(CoffeeShop.class);
                            coffeeShop.setDocumentId(document.getId());
                            coffeeShopList.add(coffeeShop);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

        adapter.setOnItemClickListener(coffeeShop -> {
            DetailItemFragment detailFragment = new DetailItemFragment();
            Bundle args = new Bundle();
            args.putString("MALL_ID", mallId); // Pass the mallId
            args.putString("COFFEESHOP_ID", coffeeShop.getDocumentId()); // Pass the coffeeShopId
            detailFragment.setArguments(args);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
