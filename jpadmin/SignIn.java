package com.sdmgapl1a0501.naimur.jpadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdmgapl1a0501.naimur.jpadmin.Common.Common;
import com.sdmgapl1a0501.naimur.jpadmin.Model.User;

public class SignIn extends AppCompatActivity {

    EditText etphone, etpassword;
    Button signin;

    FirebaseDatabase database;
    DatabaseReference users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("User");


        etphone = findViewById(R.id.numberid);
        etpassword = findViewById(R.id.passwrdid);
        signin= findViewById(R.id.btnsignid);

signin.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        signInUser(etphone.getText().toString(), etpassword.getText().toString());
    }
});



    }


    private void signInUser( String phone, String password) {
        final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
        mDialog.setMessage("Please waiting...");
        mDialog.show();

        final String localPhone = phone;
        final String localPassword = password ;

users.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // Check if user is exits
        if(dataSnapshot.child(localPhone).exists()) {
            mDialog.dismiss();
            // get user and set phone
            User user = dataSnapshot.child(localPhone).getValue(User.class);
            user.setPhone(localPhone);
            // check user is staff
            if(Boolean.parseBoolean(user.getIsStaff())) {
                // check password
                if(user.getPassword().equals(localPassword)) {
                    // Login ok
                    Intent homeIntent = new Intent(SignIn.this, MainActivity.class);
                    Common.currentUser = user;
                    startActivity(homeIntent);
                    finish();
                } else {
                    Toast.makeText(SignIn.this, "Wrong pass!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SignIn.this, "Please login with Staff account!", Toast.LENGTH_SHORT).show();
            }
        } else {
            mDialog.dismiss();
            Toast.makeText(SignIn.this, "User is not exits!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
})
;





    }



}
