package com.example.temucafe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Untuk bagian industri / coffee shop
        ViewGroup industrialContainer = view.findViewById(R.id.industrials_category);
        setClickListenersByTag(industrialContainer, "openDetail", new DetailItemFragment());

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
}
