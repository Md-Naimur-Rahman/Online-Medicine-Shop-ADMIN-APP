package com.sdmgapl1a0501.naimur.jpadmin.ViewHolder;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdmgapl1a0501.naimur.jpadmin.Common.Common;
import com.sdmgapl1a0501.naimur.jpadmin.Interface.ItemClickListener;
import com.sdmgapl1a0501.naimur.jpadmin.R;

public class MedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnCreateContextMenuListener

{

    public TextView txtMedName;
    public ImageView imagemed;

    private ItemClickListener itemClickListener;

    public MedViewHolder(@NonNull View itemView) {
        super(itemView);
        txtMedName = itemView.findViewById(R.id.med_name);
        imagemed= itemView.findViewById(R.id.med_image);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select the action");

        contextMenu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0, 1, getAdapterPosition(), Common.DELETE);
    }
}
