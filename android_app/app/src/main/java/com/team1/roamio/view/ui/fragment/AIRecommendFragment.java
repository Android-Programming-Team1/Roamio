package com.team1.roamio.view.ui.fragment;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.getSystemService;
import static com.google.gson.internal.$Gson$Types.arrayOf;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.team1.roamio.R;
import com.team1.roamio.utility.ai.AttractionData;
import com.team1.roamio.utility.ai.NearbyAttractionRecommender;
import com.team1.roamio.utility.ai.RecommendationCallback;
import com.team1.roamio.utility.planner.SavedUserData;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AIRecommendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AIRecommendFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView icon;
    private TextView textView;

    private final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private final int REQUEST_PERMISSION_CODE = 1;

    private NearbyAttractionRecommender nearbyAttractionRecommender;

    private List<AttractionData> attractionDatas = new ArrayList<>();

    public AIRecommendFragment() {
        // Required empty public constructor
    }
    public static AIRecommendFragment newInstance(String param1, String param2) {
        AIRecommendFragment fragment = new AIRecommendFragment();
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
        View view = inflater.inflate(R.layout.fragment_ai_recommend, container, false);

        icon = view.findViewById(R.id.imageView6);
        textView = view.findViewById(R.id.textView4);

        Glide.with(getActivity()).asGif().load(R.drawable.romeo1).into(icon);
        textView.setText(SavedUserData.userName + "님과 딱 맞는 여행지를 발견했어요!");

        while(!initView(view)) { Log.d("initView", "initView fail"); }

        return view;
    }

    private boolean checkPermissions(View view) {
        for (var permission : PERMISSIONS) {
            if (checkSelfPermission(view.getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    private double[] getLocation() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, REQUEST_PERMISSION_CODE);

            return null;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        double latitude = 0;
        double longitude = 0;
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        double[] ret = new double[2];
        ret[0] = latitude;
        ret[1] = longitude;

        return ret;
    }

    private boolean initView(View view) {
        nearbyAttractionRecommender = new NearbyAttractionRecommender();

        if (!checkPermissions(view)) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, REQUEST_PERMISSION_CODE);
        }

        double[] location = getLocation();

        if(location == null) {
            return false;
        }

        final boolean[] isFail = {false};

        nearbyAttractionRecommender.recommend(location[0], location[1], new RecommendationCallback() {
            @Override
            public void onSuccess(List<AttractionData> recommendations) {
                attractionDatas.addAll(recommendations);

                for(var attractionData : attractionDatas) {
                    Log.d("attractionData", attractionData.toString());
                }

                ImageButton btnPlace1 = view.findViewById(R.id.btn_place1);
                ImageButton btnPlace2 = view.findViewById(R.id.btn_place2);
                ImageButton btnPlace3 = view.findViewById(R.id.btn_place3);

                setButtonImg(btnPlace1, attractionDatas.get(0));
                setButtonImg(btnPlace2, attractionDatas.get(1));
                setButtonImg(btnPlace3, attractionDatas.get(2));

                btnPlace1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (attractionDatas.get(0).getUri().equals(""))
                            return;

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(attractionDatas.get(0).getUri()));
                        requireContext().startActivity(intent);
                    }
                });
                btnPlace2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (attractionDatas.get(1).getUri().equals(""))
                            return;

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(attractionDatas.get(1).getUri()));
                        requireContext().startActivity(intent);
                    }
                });
                btnPlace3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (attractionDatas.get(2).getUri().equals(""))
                            return;

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(attractionDatas.get(2).getUri()));
                        requireContext().startActivity(intent);
                    }
                });

                TextView placeName1 = view.findViewById(R.id.txt_place_name1);
                TextView placeName2 = view.findViewById(R.id.txt_place_name2);
                TextView placeName3 = view.findViewById(R.id.txt_place_name3);

                placeName1.setText(attractionDatas.get(0).getName());
                placeName2.setText(attractionDatas.get(1).getName());
                placeName3.setText(attractionDatas.get(2).getName());

                TextView placeScore1 = view.findViewById(R.id.txt_place_score1);
                TextView placeScore2 = view.findViewById(R.id.txt_place_score2);
                TextView placeScore3 = view.findViewById(R.id.txt_place_score3);

                placeScore1.setText(String.valueOf(attractionDatas.get(0).getStarPoint()));
                placeScore2.setText(String.valueOf(attractionDatas.get(1).getStarPoint()));
                placeScore3.setText(String.valueOf(attractionDatas.get(2).getStarPoint()));
            }

            @Override
            public void onError(Exception e) {
                Log.e("error_ai_recommend", e.getMessage());
                isFail[0] = true;
            }
        });

        return !isFail[0];
    }

    private void setButtonImg(ImageButton btnPlace, AttractionData attractionData) {
        if(attractionData.getCategory().equals("음식점")) //(음식점, 카페, 명소, 공원, 쇼핑, 박물관, 미술관, 엔터테이먼트, 바)
            btnPlace.setImageResource(R.drawable.img_restaurant);

        else if(attractionData.getCategory().equals("카페"))
            btnPlace.setImageResource(R.drawable.img_cafe);

        else if(attractionData.getCategory().equals("명소"))
            btnPlace.setImageResource(R.drawable.img_landmark);

        else if(attractionData.getCategory().equals("공원"))
            btnPlace.setImageResource(R.drawable.img_park);

        else if(attractionData.getCategory().equals("쇼핑"))
            btnPlace.setImageResource(R.drawable.img_shopping);

        else if(attractionData.getCategory().equals("박물관"))
            btnPlace.setImageResource(R.drawable.img_museum);

        else if(attractionData.getCategory().equals("미술관"))
            btnPlace.setImageResource(R.drawable.img_art_gallery);

        else if(attractionData.getCategory().equals("엔터테이먼트"))
            btnPlace.setImageResource(R.drawable.img_entertainment);

        else if(attractionData.getCategory().equals("바"))
            btnPlace.setImageResource(R.drawable.img_bar);

        else
            btnPlace.setImageResource(R.drawable.img_not_found_img);

    }
}