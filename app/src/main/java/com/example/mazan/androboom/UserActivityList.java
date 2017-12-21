package com.example.mazan.androboom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

 /*           TextView tv = new TextView(getContext());             tv.setText(liste.get(position).getEmail());             return tv; */

            // on va chercher le bon profil dans la liste

            Profil p = liste.get(position);

            // on instancie le layout sous la forme d'un objet de type View
            View layout = View.inflate(getContext(), R.layout.profil_list_item, null);
            // on va chercher les trois composants du layout
            ImageView imageProfilView = (ImageView) layout.findViewById(R.id.imageView);
            TextView textView = (TextView) layout.findViewById(R.id.textView);
            ImageView imageConnectedView = (ImageView) layout.findViewById(R.id.imageView2);

            // on télécharge dans le premier composant l'image du profil
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference photoRef = storage.getReference().child(p.getEmail() + "/photo.jpg");
            if (photoRef != null) {
                Glide.with(getContext()).using(new FirebaseImageLoader())
                        .load(photoRef)
                        .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.ic_person_black_24dp)
                        .into(imageProfilView);
            }
            // on positionne le email dans le TextView
            textView.setText(p.getEmail());
            // si l'utilisateur n'est pas connecté, on rend invisible le troisième
            // composant
            if (!p.isConnected) {
                imageConnectedView.setVisibility(View.INVISIBLE);
            }

            // on retourne le layout
            return layout;
        }
            // on va chercher le bon profil dans la liste


/*
            // on instancie le layout sous la forme d'un objet de type View
            View layout = View.inflate(getContext(), R.layout.profil_list_item, null);
            // on va chercher les trois composants du layout
            ImageView imageProfilView = (ImageView) layout.findViewById(R.id.imageView);
            TextView textView = (TextView) layout.findViewById(R.id.textView);
            ImageView imageConnectedView = (ImageView) layout.findViewById(R.id.imageView2);
            // on retourne le layout
            return layout;
        }

*/



        @Override
        public int getCount() {
            return liste.size();
        }}


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
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.v("AndroBoum", "loadPost:onCancelled", databaseError.toException());
            }
        };

        mDatabase.addValueEventListener(postListener);

    }

}


