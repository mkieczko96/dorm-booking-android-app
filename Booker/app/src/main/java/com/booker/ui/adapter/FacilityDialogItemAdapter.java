package com.booker.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.booker.R;
import com.booker.data.models.Facility;
import com.booker.databinding.ItemFacilitySimpleBinding;

import java.util.List;

public class FacilityDialogItemAdapter extends BaseAdapter implements ListAdapter {

    List<Facility> dialogItems;
    Context context;

    public FacilityDialogItemAdapter(Context context, List<Facility> dialogItems) {
        this.context = context;
        this.dialogItems = dialogItems;
    }

    @Override
    public int getCount() {
        return dialogItems.size();
    }

    @Override
    public Facility getItem(int i) {
        return dialogItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_facility_simple, null);
        }

        ItemFacilitySimpleBinding binding = ItemFacilitySimpleBinding.bind(view);
        TextView itemTitle = binding.facilityDialogItemTitle;

        itemTitle.setText(dialogItems.get(i).getName());

        return view;
    }
}
