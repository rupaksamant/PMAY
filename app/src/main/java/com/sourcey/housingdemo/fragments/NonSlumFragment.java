package com.sourcey.housingdemo.fragments;

/**
 * Created by Biswajit.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.pm.yojana.housingdemo.R;
import com.sourcey.housingdemo.AddSurveyDataManager;
import com.sourcey.housingdemo.MainActivity;
import com.sourcey.housingdemo.PmayItemClickListener;
import com.sourcey.housingdemo.adapter.SurveyDataAdapterNonSlum;
import com.sourcey.housingdemo.modal.SurveyDataModal;

import java.util.ArrayList;
import java.util.List;

public class NonSlumFragment extends Fragment{

    private RecyclerView mRecyclerview;
    SurveyDataAdapterNonSlum contactsAdapter;


    public NonSlumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mRecyclerview != null && contactsAdapter.getItemCount() > 0) {
            mRecyclerview.invalidate();
        }
    }

    PmayItemClickListener listener = new PmayItemClickListener() {
        @Override
        public void onItemClick(int pos, int slumRadio) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("ListItempos", pos);
            intent.putExtra("pos", slumRadio);
            startActivity(intent);
        }
    };

    public static SlumFragment newInstance() {
        SlumFragment fragment = new SlumFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactsAdapter = AddSurveyDataManager.getInstance().mSurveyDataAdapterNonSlum;
        contactsAdapter.listener = listener;
        contactsAdapter.slumTab = "N";
        contactsAdapter.mSurveyDataModals = AddSurveyDataManager.getInstance().surveyDataModals;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_customer, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerview = (RecyclerView) getActivity().findViewById(R.id.customerlist);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerview.setLayoutManager(layoutManager);
        mRecyclerview.setAdapter(contactsAdapter);
        mRecyclerview.invalidate();
    }

    private List<SurveyDataModal> generateData(){

        return AddSurveyDataManager.getInstance().surveyDataModals;


        /*surveyDataModals.add(new SurveyDataModal("John","987654321", "12","Joseph", "2345678977", "true"));
        surveyDataModals.add(new SurveyDataModal("John","987654321", "12","Joseph", "2345678977", "true"));
        surveyDataModals.add(new SurveyDataModal("John","987654321", "12","Joseph", "2345678977", "true"));
        surveyDataModals.add(new SurveyDataModal("John","987654321", "12","Joseph", "2345678977", "true"));
        surveyDataModals.add(new SurveyDataModal("John","987654321", "12","Joseph", "2345678977", "true"));
        surveyDataModals.add(new SurveyDataModal("John","987654321", "12","Joseph", "2345678977", "true"));
        surveyDataModals.add(new SurveyDataModal("John","987654321", "12","Joseph", "2345678977", "true"));
        surveyDataModals.add(new SurveyDataModal("John","987654321", "12","Joseph", "2345678977", "true"));*/
        /*Log.e("size", surveyDataModals.size()+"");
        return surveyDataModals;*/
    }
}

