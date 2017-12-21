package com.example.mazan.androboom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class UserActivityList extends AppCompatActivity {

    private class MyArrayAdapter extends ArrayAdapter<Profil> {
        List<Profil> liste;

        private MyArrayAdapter
                (UserActivityList context, int resource, List<Profil> liste) {
            super(context, resource, liste);
            this.liste = liste;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(getContext());
            tv.setText(liste.get(position).getEmail());
            return tv;
        }

        @Override
        public int getCount() {
            return liste.size();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final List<Profil> userList = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlistactivity);
        ListView listeView = (ListView) findViewById(R.id.listview1);
        final MyArrayAdapter adapter = new MyArrayAdapter(this, android.R.layout.simple_list_item_1, userList);
        listeView.setAdapter(adapter);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    userList.add(child.getValue(Profil.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {                 // Getting Post failed, log a message
                Log.v("AndroBoum", "loadPost:onCancelled", databaseError.toException());
            }
        };

        mDatabase.addValueEventListener(postListener);

    }

}


