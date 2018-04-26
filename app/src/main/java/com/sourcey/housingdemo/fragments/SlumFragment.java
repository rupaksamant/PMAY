package com.sourcey.housingdemo.fragments;

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
import com.sourcey.housingdemo.adapter.SurveyDataAdapter;
import com.sourcey.housingdemo.modal.SurveyDataModal;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SlumFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SlumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SlumFragment extends Fragment {

    private RecyclerView mRecyclerview;
    SurveyDataAdapter contactsAdapter;

    public SlumFragment() {
        // Required empty public constructor
    }

    public static SlumFragment newInstance() {
        SlumFragment fragment = new SlumFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactsAdapter = AddSurveyDataManager.getInstance().mSurveyDataAdapter;
        contactsAdapter.listener = listener;
        contactsAdapter.slumTab = "S";
        contactsAdapter.mSurveyDataModals = AddSurveyDataManager.getInstance().surveyDataModals;

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

    @Override
    public void onResume() {
        super.onResume();
        if(mRecyclerview != null && contactsAdapter.getItemCount() > 0) {
            mRecyclerview.invalidate();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerview = (RecyclerView) getActivity().findViewById(R.id.contactlist);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerview.setLayoutManager(layoutManager);
        mRecyclerview.setAdapter(contactsAdapter);
    }

    private List<SurveyDataModal> generateData(){
        /*ArrayList<SurveyDataModal> surveyDataModals = new ArrayList<>();

        surveyDataModals.add(new SurveyDataModal("John","987654321123", "12","Joseph", "2345678977", "true" ));
        surveyDataModals.add(new SurveyDataModal("John S","987654321123", "12","Joseph", "2345678977", "true"));
        surveyDataModals.add(new SurveyDataModal("John","987654321123", "12","Joseph", "2345678977", "true"));
        surveyDataModals.add(new SurveyDataModal("John","987654321123", "12","Joseph", "2345678977", "true"));
        surveyDataModals.add(new SurveyDataModal("John","987654321123", "12","Joseph", "2345678977", "true"));
        surveyDataModals.add(new SurveyDataModal("John","987654321123", "12","Joseph", "2345678977", "true"));
        surveyDataModals.add(new SurveyDataModal("John","987654321123", "12","Joseph", "2345678977", "true"));
        surveyDataModals.add(new SurveyDataModal("John","987654321123", "12","Joseph", "2345678977", "true"));*/

        return AddSurveyDataManager.getInstance().surveyDataModals;
    }
}
