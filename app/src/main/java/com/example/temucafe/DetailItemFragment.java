package com.example.temucafe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailItemFragment extends Fragment {

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_item, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Directions - Buka Google Maps
        view.findViewById(R.id.directions).setOnClickListener(v -> {
            String mapUrl = "https://maps.app.goo.gl/QJgRs36RazSD7ciS7"; // Ganti sesuai lokasi kamu
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));
            startActivity(intent);
        });

        // Menus - Buka Web
        view.findViewById(R.id.menus).setOnClickListener(v -> {
            String websiteUrl = "https://drive.google.com/file/d/1xKXTqH0_wJ7E1jI0q13d93jP995tfH-C/view?usp=sharing"; // Ganti dengan link web menu kamu
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl));
            startActivity(intent);
        });

        // Share - Share ke WhatsApp, IG, Facebook (via chooser)
        view.findViewById(R.id.share).setOnClickListener(v -> {
            String shareText = "Check out Mattea Social Space!\nhttps://maps.app.goo.gl/QJgRs36RazSD7ciS7";
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);

            Intent chooser = Intent.createChooser(sendIntent, "Share via");
            startActivity(chooser);
        });

        // Add Wishlist - belum diatur
        // view.findViewById(R.id.add_wishlist).setOnClickListener(...);
    }

}