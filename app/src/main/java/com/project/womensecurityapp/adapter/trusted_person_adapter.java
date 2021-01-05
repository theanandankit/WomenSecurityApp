package com.project.womensecurityapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.project.womensecurityapp.R;
import com.project.womensecurityapp.model.Trusted_person_model;

import java.util.ArrayList;

public class trusted_person_adapter extends ArrayAdapter<Trusted_person_model> {

    ArrayList<Trusted_person_model> list_member_list = new ArrayList<>();

    public trusted_person_adapter(Context value_context, int textViewResourceId, ArrayList<Trusted_person_model> object) {

        super(value_context, textViewResourceId, object);
        list_member_list = object;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.trusted_person_view, null);
        TextView name = (TextView) v.findViewById(R.id.trusted_person_view_name);
        TextView contact = (TextView) v.findViewById(R.id.trusted_person_view_contact);
        TextView relation = (TextView) v.findViewById(R.id.trusted_person_view_relation);
        TextView address = v.findViewById(R.id.trusted_person_view_adress);
        name.setText(list_member_list.get(position).getName());
        contact.setText(list_member_list.get(position).getContact());
        relation.setText(list_member_list.get(position).getRelation());
        address.setText(list_member_list.get(position).getAddress());
        return v;
    }
}
