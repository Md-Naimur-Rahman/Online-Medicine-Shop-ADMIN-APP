package com.sdmgapl1a0501.naimur.jpadmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.sdmgapl1a0501.naimur.jpadmin.Common.Common;
import com.sdmgapl1a0501.naimur.jpadmin.Interface.ItemClickListener;
import com.sdmgapl1a0501.naimur.jpadmin.Model.Request;
import com.sdmgapl1a0501.naimur.jpadmin.ViewHolder.OrderViewHolder;

import static com.sdmgapl1a0501.naimur.jpadmin.Common.Common.convertCodeToStatus;

public class OrderStatus extends AppCompatActivity {
RecyclerView recyclerView;
RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference requests;
    MaterialSpinner spinner;

 FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

       // Toast.makeText(OrderStatus.this, "order!!!", Toast.LENGTH_SHORT).show();

        // Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

requests.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
       // Toast.makeText(OrderStatus.this, "cccc!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
       //
    }
});
   //   recyclerView.setHasFixedSize(true);
    recyclerView =findViewById(R.id.listOrders);
   layoutManager = new LinearLayoutManager(this);
   recyclerView.setLayoutManager(layoutManager);


         loadOrder();











    }

  private void loadOrder() {

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order,
                OrderViewHolder.class,
                requests

        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent i = new Intent(OrderStatus.this,TrackingOrder.class);
                        Common.currentRequest = model;
                        startActivity(i);
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);




    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
      //  Toast.makeText(OrderStatus.this, "start!!", Toast.LENGTH_SHORT).show();
        if(item.getTitle().equals(Common.UPDATE)) {
            showUpdatedioalog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        //    Toast.makeText(OrderStatus.this, "yo!!", Toast.LENGTH_SHORT).show();
        } else if(item.getTitle().equals(Common.DELETE)) {
            deleteOrder(adapter.getRef(item.getOrder()).getKey());
         //   Toast.makeText(OrderStatus.this, "cccc!!", Toast.LENGTH_SHORT).show();
        }
        
        
        
        return super.onContextItemSelected(item);
    }

    private void deleteOrder(String key) {
        requests.child(key).removeValue();
    }

    private void showUpdatedioalog(String key, final Request item) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please choose status");

        LayoutInflater inflater = this.getLayoutInflater();
        final  View view = inflater.inflate(R.layout.update_order,null);

        spinner = view.findViewById(R.id.statusSpinnerid);
        spinner.setItems("Placed","On my Way", "Order Cancelled, Please call our hotline for information");
        alertDialog.setView(view);

        final String localKey = key ;
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));

                requests.child(localKey).setValue(item);


            }
        });
             alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialogInterface, int i) {
                     dialogInterface.dismiss();
                 }
             });
                     alertDialog.show();
    }
}
