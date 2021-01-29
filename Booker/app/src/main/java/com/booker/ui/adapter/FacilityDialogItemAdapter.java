package com.booker.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.booker.R;
import com.booker.databinding.FacilityDialogItemBinding;

import java.util.List;

public class FacilityDialogItemAdapter extends BaseAdapter implements ListAdapter {

    List<FacilityDialogItem> dialogItems;
    Context context;

    public FacilityDialogItemAdapter(Context context, List<FacilityDialogItem> dialogItems) {
        this.context = context;
        this.dialogItems = dialogItems;
    }

    @Override
    public int getCount() {
        return dialogItems.size();
    }

    @Override
    public Object getItem(int i) {
        return dialogItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.facility_dialog_item, null);
        }

        FacilityDialogItemBinding binding = FacilityDialogItemBinding.bind(view);
        ImageView itemIcon = binding.facilityDialogItemIcon;
        TextView itemTitle = binding.facilityDialogItemTitle;

        itemIcon.setImageResource(dialogItems.get(i).iconResId);
        itemTitle.setText(dialogItems.get(i).name);

        return view;
    }
}
