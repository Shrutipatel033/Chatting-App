package com.example.thirdapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devlomi.circularstatusview.CircularStatusView;
import com.example.thirdapp.model.StatusModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StatusAdapter extends
        RecyclerView.Adapter<StatusAdapter.ViewHolder> {
    Context context;
    ArrayList<StatusModel>status;
    FragmentManager f;
    public StatusAdapter(Context context,ArrayList<StatusModel>status,FragmentManager f)
    {
        this.context=context;
        this.status=status;
        this.f=f;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.samplestatusdesign,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(status.get(position).id.equals
                (FirebaseAuth.getInstance().getCurrentUser().getUid())){
           holder.statusname.setText("this is your status");
        }
        else{
            holder.statusname.setText(status.get(position).name);
        }
   holder.statusname.setText(status.get(position).name);
   holder.statusdate.setText(status.get(position).date);
   holder.statusborder.setPortionsCount(status.get(position).statusurls.size());
   int totalstatus=status.get(position).statusurls.size();

        Picasso.get()
                .load(Uri.parse(status.get(position).statusurls.get(totalstatus-1)))
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(holder.statusicon);
        //set on long press event on every status----
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(status.get(position).id.equals
                        (FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    FirebaseDatabase.getInstance().getReference().child("status").child(FirebaseAuth.getInstance().getCurrentUser()
                            .getUid()).setValue(null);
                    Toast.makeText(context,"status deleted ",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(context,"Don't try to delete .This is not your status ",Toast.LENGTH_LONG).show();
                }

                return false;
            }
        });
        //set on click of status,to view every status-----

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MyStory> myStories = new ArrayList<>();

                for(String story: status.get(position).statusurls){
                    myStories.add(new MyStory(
                          story
                    ));
                }
                new StoryView.Builder(f)

                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(status.get(position).name) // Default is Hidden
                        .setSubtitleText("Story") // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return status.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView statusicon;
        CircularStatusView statusborder;
        TextView statusname,statusdate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            statusicon=itemView.findViewById(R.id.statusicon);
            statusborder=itemView.findViewById(R.id.circular_status_view);
            statusname=itemView.findViewById(R.id.statusname);
            statusdate=itemView.findViewById(R.id.statusdate);
        }
    }

}
