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

import com.team1.roamio.R;
import com.team1.roamio.data.TravelPlanData;
import com.team1.roamio.utility.planner.SavedUserData;
import com.team1.roamio.utility.planner.TravelPlanParser;
import com.team1.roamio.view.ui.activity.ActivityPlanningResult;
import com.team1.roamio.view.ui.list_view_adapter.PlanDataListViewAdapter;
import com.team1.roamio.view.ui.list_view_item.PlanDataListViewItem;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

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


    private RecyclerView recyclerView;
    private PlanDataListViewAdapter adapter;
    private List<Pair<PlanDataListViewItem, TravelPlanData>> itemList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


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

        initList(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        View view = getView();

        if (view != null) {
            initList(view);
        }
    }

    public void initList(View view) {
        sharedPreferences = getActivity().getSharedPreferences("plan", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        recyclerView = view.findViewById(R.id.planDataSaveList);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);

        itemList = new ArrayList<>();

        int size = sharedPreferences.getInt("idx", 0);

        for (int i = 1; i < size; i++) {
            String title = sharedPreferences.getString("plan_title" + i, "");
            try {
                itemList.add(new Pair<>(
                                new PlanDataListViewItem(title, ""),
                                TravelPlanParser.parseJsonToPlanData(sharedPreferences.getString("plan_json" + i, ""))
                        )
                );
            }
            catch (JSONException e) {
                Log.e("error", e.getMessage());
            }
        }

        if (itemList.size() == 0) {
            itemList.add(new Pair<>(
                    new PlanDataListViewItem("저장된 계획이 없습니다.", ""),
                    null
            ));
        }

        adapter = new PlanDataListViewAdapter(view.getContext(), itemList);

        if(!itemList.get(0).first.equals("저장된 계획이 없습니다.")) {
            adapter.setOnItemClickListener((position, item) -> {

                SavedUserData.isShowSavedData = true;
                SavedUserData.planData = item.second;

                Intent intent = new Intent(getActivity(), ActivityPlanningResult.class);
                startActivity(intent);
            });
        }

        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(
                new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL)
        );
    }
}