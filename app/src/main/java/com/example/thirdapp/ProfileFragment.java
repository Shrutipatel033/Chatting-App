package com.example.thirdapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileFragment extends Fragment {

    ImageView img_profile,img_changeprofile;

    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_profile,container,false);
        //******open gallery to choose profile picture-----
        img_profile=v.findViewById(R.id.img_profile);
        img_changeprofile=v.findViewById(R.id.img_changeprofile);
        img_changeprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(Intent.ACTION_GET_CONTENT);
                in.setType("image/*");
                startActivityForResult(in,1);
            }
        });
        //select all info about current user from database----
        TextView txt_pname=v.findViewById(R.id.txt_pname);
        TextView txt_pemail=v.findViewById(R.id.txt_pemail);
        TextView txt_pname2=v.findViewById(R.id.txt_pname2);
        TextView txt_about=v.findViewById(R.id.pabout);
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        txt_pname.setText(snapshot.child("name").getValue(String.class));
                        txt_pname2.setText(snapshot.child("name").getValue(String.class));
                        txt_pemail.setText(snapshot.child("email").getValue(String.class));
                        TextView txt_about=v.findViewById(R.id.pabout);
                        Picasso.get()
                                .load(snapshot.child("pic").getValue(String.class))
                                .placeholder(R.drawable.user)
                                .error(R.drawable.user)
                                .into(img_profile);
                        txt_about.setText(snapshot.child("about").getValue(String.class));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //*******select all info about current user  from database---


        //**** logout from contact
         Button btn_logout1=v.findViewById(R.id.btn_logout1);
        btn_logout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent in=new Intent(getContext(), Login.class);
                startActivity(in);
            }
        });
        //change about of user on click of pencil----
        ImageView img_changeabout=v.findViewById(R.id.img_changeabout);
        img_changeabout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
                alert.setTitle("About");
                alert.setMessage("Tell me your current  feeling....");
                EditText input=new EditText(getContext());
                alert.setView(input);
                alert.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialogInterface, int i) {
               HashMap<String,Object> data=new HashMap<>();
               data.put("about",input.getText().toString());
               FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser()
                       .getUid()).updateChildren(data);
               txt_about.setText(input.getText());
               dialogInterface.dismiss();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });
                alert.show();
            }
        });
        //back button click event---
        ImageView btn_back=v.findViewById(R.id.img_pback);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
       return v;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==1){
            img_profile.setImageURI(data.getData());
            FirebaseStorage.getInstance().getReference().child(FirebaseAuth
                    .getInstance().getCurrentUser().getUid()).putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            HashMap<String,Object>data=new HashMap<>();
                            data.put("pic",uri.toString());
                            FirebaseDatabase.getInstance().getReference().
                                    child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                    updateChildren(data);
                            Toast.makeText(getContext(),"profile updated",Toast.LENGTH_LONG).show();

                        }
                    });
                }
            });

        }
    }
}