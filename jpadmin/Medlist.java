package com.sdmgapl1a0501.naimur.jpadmin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sdmgapl1a0501.naimur.jpadmin.Common.Common;
import com.sdmgapl1a0501.naimur.jpadmin.Interface.ItemClickListener;
import com.sdmgapl1a0501.naimur.jpadmin.Model.Category;
import com.sdmgapl1a0501.naimur.jpadmin.Model.Medicine;
import com.sdmgapl1a0501.naimur.jpadmin.ViewHolder.MedViewHolder;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class Medlist extends AppCompatActivity {
   RecyclerView recyclerView;
    FloatingActionButton fab;
    RelativeLayout rootLayout;

    RecyclerView.LayoutManager layoutManager ;

    // Firebase
    FirebaseDatabase db ;
    DatabaseReference medList ;
    FirebaseStorage storage ;
    StorageReference storageReference ;

    String categoryId = "";

    FirebaseRecyclerAdapter<Medicine, MedViewHolder> adapter;

   EditText edtName, edtDescription, edtPrice, edtDiscount;
    Button btnSelect, btnUpload;

    // new food
   Medicine newMed;

    // Uri for saving image
    Uri saveUri = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medlist);

        // Firebase
        db = FirebaseDatabase.getInstance();
        medList = db.getReference("Medicine");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // set recyclerview
rootLayout =findViewById(R.id.root);
        recyclerView = findViewById(R.id.recycler_med);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
fab = findViewById(R.id.add_new_med);

fab.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
       showAddFoodDialog();
    }
});

        if (getIntent() != null) {
            categoryId = getIntent().getStringExtra("CategoryId");
        }
        if (!categoryId.isEmpty()) {
            loadList(categoryId);
        }










    }

    private void showAddFoodDialog() {
        // set title and message
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Medlist.this);
        alertDialog.setTitle("Add new Medicine");
        alertDialog.setMessage("Please fill full information");

        // inflating layout
        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_med, null);

        edtName = add_menu_layout.findViewById(R.id.edtNameid);
        edtDescription = add_menu_layout.findViewById(R.id.edtDescriptionid);
        edtPrice = add_menu_layout.findViewById(R.id.edtPriceid);
    edtDiscount = add_menu_layout.findViewById(R.id.edtDiscountid);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);
        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);

        // set layout and icon for dialog
        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        // event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               uploadImage();
            }
        });

        // set button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                // Here, pushing new category to Firebase Database
                if (newMed != null) {
                    medList.push().setValue(newMed);
                    Snackbar.make(rootLayout, "New Medicine " + newMed.getName() + " was added",
                            Snackbar.LENGTH_SHORT).show();
                }
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
    private void loadList(String categoryId) {

        adapter = new FirebaseRecyclerAdapter<Medicine, MedViewHolder>(
                Medicine.class,
                R.layout.med_item,
                MedViewHolder.class,
                medList.orderByChild("menuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(MedViewHolder viewHolder, Medicine model, int position) {
                viewHolder.txtMedName.setText(model.getName());
                Picasso.get().load(model.getImage())
                        .into(viewHolder.imagemed);
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.PICK_IMAGE_REQUEST);
    }


    private void uploadImage() {
        if (saveUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            // set image name
            String imageName = UUID.randomUUID().toString();
            // set folder
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            // uploading image to folder
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(Medlist.this, "Uploaded!!!", Toast.LENGTH_SHORT).show();
                            // set value for new Category if image uploaded
                            // and we can get download link 'uri'
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newMed = new Medicine();
                                       newMed.setName(edtName.getText().toString());
                                    newMed.setDescription(edtDescription.getText().toString());
                                    newMed.setPrice(edtPrice.getText().toString());
                                    newMed.setDiscount(edtDiscount.getText().toString());
                                    newMed.setMenuId(categoryId);
                                    newMed.setImage(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(Medlist.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // don't worry about this error
                            double progress = (100 * taskSnapshot.getBytesTransferred() /
                                    taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded " +  progress + "%");
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            // get Uri from image selected
            saveUri = data.getData();
            btnSelect.setText("Image Selected!");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(Common.UPDATE)) {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        } else if(item.getTitle().equals(Common.DELETE)) {
            deleteCategory(adapter.getRef(item.getOrder()).getKey());
        }


        return super.onContextItemSelected(item);
    }


    private void deleteCategory(String key) {
        medList.child(key).removeValue();
        Toast.makeText(this, "Deleted",
                Toast.LENGTH_SHORT).show();
    }

    private void   showUpdateDialog(final String key, final Medicine item) {
        // set title and message
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Medlist.this);
        alertDialog.setTitle("Add new Food");
        alertDialog.setMessage("Please fill full information");

        // inflating layout
        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_med, null);

        edtName = add_menu_layout.findViewById(R.id.edtNameid);
        edtDescription = add_menu_layout.findViewById(R.id.edtDescriptionid);
        edtPrice = add_menu_layout.findViewById(R.id.edtPriceid);
     edtDiscount = add_menu_layout.findViewById(R.id.edtDiscountid);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);
        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);


        edtName.setText(item.getName());
        edtDescription.setText(item.getDescription());
        edtPrice.setText(item.getPrice());


        // set layout and icon for dialog
        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        // event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage(item);
            }
        });

        // set button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                // Here, pushing new category to Firebase Database


                    item.setName(edtName.getText().toString());
                    item.setName(edtPrice.getText().toString());
                    item.setName(edtDescription.getText().toString());

                    medList.child(key).setValue(item);


                    medList.push().setValue(newMed);
                    Snackbar.make(rootLayout, "Medicine" + item.getName() + " edited",
                            Snackbar.LENGTH_SHORT).show();

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

    private void changeImage(final Medicine item) {
        if (saveUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            // set image name
            String imageName = UUID.randomUUID().toString();
            // set folder
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            // uploading image to folder
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(Medlist.this, "Uploaded!!!", Toast.LENGTH_SHORT).show();
                            // set value for new Category if image uploaded
                            // and we can get download link 'uri'
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setImage(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(Medlist.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // don't worry about this error
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() /
                                    taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded " + progress + "%");
                        }
                    });
        }
    }
}
