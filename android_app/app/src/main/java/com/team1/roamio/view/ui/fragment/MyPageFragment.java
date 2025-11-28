package com.team1.roamio.view.ui.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.gson.JsonObject;
import com.team1.roamio.R;
import com.team1.roamio.data.TravelPlanData;
import com.team1.roamio.utility.database.DriveManager;
import com.team1.roamio.utility.planner.SavedUserData;
import com.team1.roamio.utility.planner.TravelPlanParser;
import com.team1.roamio.view.ui.activity.ActivityPlanningResult;
import com.team1.roamio.view.ui.activity.SavedPlanDataActivity;
import com.team1.roamio.view.ui.activity.SplashActivity;
import com.team1.roamio.view.ui.list_view_adapter.PlanDataListViewAdapter;
import com.team1.roamio.view.ui.list_view_item.PlanDataListViewItem;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kotlin.Triple;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageButton savedListButton;
    private ImageButton logoutButton;

    private ImageView icon;
    private TextView textView;


    public MyPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyPageFragment newInstance(String param1, String param2) {
        MyPageFragment fragment = new MyPageFragment();
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

        View view = inflater.inflate(R.layout.fragment_my_page, container, false);

        savedListButton = view.findViewById(R.id.imageButton9);
        logoutButton = view.findViewById(R.id.imageButton10);
        icon = view.findViewById(R.id.imageView19);
        textView = view.findViewById(R.id.textView7);

        Glide.with(getActivity()).asGif().load(R.drawable.romeo1).into(icon);

        setWelcomText();

        savedListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SavedPlanDataActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
                googleSignInClient.signOut();

                Intent intent = new Intent(getActivity(), SplashActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setWelcomText() {
        int rand = (new Random()).nextInt(5);
        String name = SavedUserData.userName;

        switch (rand) {
            case 0:
                textView.setText("반가워요 " + name + "님!\n여행 계획 짜는 걸 좋아하는 '로미'에요!");
                break;

            case 1:
                textView.setText("어서오세요 " + name + "님!\n로미와 함께 멋진 여행 계획 만들어봐요 :)");
                break;

            case 2:
                textView.setText(name + "님, 다시 만나서 반가워요!\n여행을 더 즐겁게 만들어주는 로미예요.");
                break;

            case 3:
                textView.setText("안녕하세요 " + name + "님!\n로미가 최고의 여행 코스를 준비해드릴게요!");
                break;

            default:
                textView.setText("환영해요 " + name + "님 :)\n여행의 설렘을 함께 채워가는 로미입니다!");
                break;
        }
    }
}