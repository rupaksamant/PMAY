package com.sourcey.housingdemo.adapter;

/**
 * Created by root on 10/3/17.
 */


import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.pm.yojana.housingdemo.R;
import com.sourcey.housingdemo.AddSurveyDataManager;
import com.sourcey.housingdemo.PmayItemClickListener;
import com.sourcey.housingdemo.modal.SurveyDataModal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Biswajit.
 */

public class SurveyDataAdapter extends RecyclerView.Adapter<SurveyDataAdapter.ContactsHolder> {

    public List<SurveyDataModal> mSurveyDataModals;
    private FragmentManager mFm;
    public String slumTab = "N";

    public PmayItemClickListener listener ;

    public SurveyDataAdapter(ArrayList<SurveyDataModal> surveyDataModals){
        mSurveyDataModals = surveyDataModals;
    }

    TextDrawable.IBuilder builder;

    public SurveyDataAdapter() {
        builder = TextDrawable.builder().beginConfig()
                .textColor(Color.GRAY)
                .fontSize(20)
                .endConfig().round();
    }

    @Override
    public ContactsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.single_contact, parent, false);

        ContactsHolder viewHolder = new ContactsHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContactsHolder holder, final int position) {
        final SurveyDataModal contact = mSurveyDataModals.get(position);
Log.v("PMAY ", " Items : "+contact.isSubmitted +" , "+contact.mSurveyId +" , slum : "+contact.mSlum);
        if(contact != null && ("N".equals(contact.isSubmitted) || "O".equals(contact.isSubmitted))) {

            holder.mPhoneView.setText(contact.getmPhoneNumber());
            holder.mContactsNameView.setText(contact.getmName());
            holder.mSurveyId.setText(contact.mSurveyId);
            if("Y".equals(contact.isSubmitted)) {
                TextDrawable drawable1 = builder.build("", Color.GREEN);
                holder.mSurveyId.setBackground(drawable1);
            } else if("N".equals(contact.isSubmitted)) {
                TextDrawable drawable1 = builder.build("", Color.parseColor("#039efe"));
                holder.mSurveyId.setBackground(drawable1);
            } else {
                TextDrawable drawable1 = builder.build("", Color.RED);
                holder.mSurveyId.setBackground(drawable1);
            }
            holder.mFatherName.setText(contact.mFatherName);
            if(contact.mBankAccNo == null && "S".equals(contact.mSlum))
                holder.mAccNo.setText("Slum");
			else 
                holder.mAccNo.setText("Non-Slum");   
            holder.mPhoneView.setVisibility(View.VISIBLE);
            holder.mContactsNameView.setVisibility(View.VISIBLE);

            holder.mSurveyId.setVisibility(View.VISIBLE);
            holder.mFatherName.setVisibility(View.VISIBLE);
            holder.mAccNo.setVisibility(View.VISIBLE);
            holder.divider.setVisibility(View.VISIBLE);
            AddSurveyDataManager.getInstance().SAVED_COUNT ++;
        } else {
            holder.mPhoneView.setVisibility(View.GONE);
            holder.mContactsNameView.setVisibility(View.GONE);

            holder.mSurveyId.setVisibility(View.GONE);
            holder.mFatherName.setVisibility(View.GONE);
            holder.mAccNo.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);
        }
        holder.mSurveyId.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(position, (contact.mSlum.equals("S")?0:1));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mSurveyDataModals.size();
    }


    public static class ContactsHolder extends RecyclerView.ViewHolder{

        TextView mContactsNameView;
        TextView mFatherName;

        TextView mSurveyId;
        TextView mAccNo;
        //TextView mAdharNo;
        TextView mPhoneView;
        View divider;


        public ContactsHolder(View itemView) {
            super(itemView);
            mContactsNameView = (TextView) itemView.findViewById(R.id.nameView);
            mPhoneView = (TextView) itemView.findViewById(R.id.adharNo);

            mFatherName = (TextView) itemView.findViewById(R.id.fname);
            mAccNo = (TextView) itemView.findViewById(R.id.accountno);
            mSurveyId = (TextView) itemView.findViewById(R.id.surveyNum);
            divider = (View) itemView.findViewById(R.id.divider);

        }

    }
}

