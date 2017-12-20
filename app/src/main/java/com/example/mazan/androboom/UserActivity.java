package com.example.mazan.androboom;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;


import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by mazan on 08/12/2017.
 */

public class UserActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private static final int SELECT_PICTURE = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        // on demande une instance du mécanisme d'authentification
        FirebaseAuth auth = FirebaseAuth.getInstance();
        // la méthode ci-dessous renvoi l'utilisateur connecté ou null si personne
        if (auth.getCurrentUser() != null) {
            // déjà connecté
            Log.v("AndroBoum", "je suis déjà connecté sous l'email :" + auth.getCurrentUser().getEmail());
        } else

        {
            // on lance l'activité qui gère l'écran de connexion en la paramétrant avec les providers googlet et facebook.
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                    .setAvailableProviders(Arrays.asList(
                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                    .build(), 123);
        }

        TextView email = (TextView) findViewById(R.id.email);
        email.setText("Email : " + auth.getCurrentUser().getEmail());



        ImageView imageView = (ImageView) findViewById(R.id.imageProfil);
        imageView.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.setAction(Intent.ACTION_PICK); Intent chooserIntent = Intent.createChooser(intent, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[] { captureIntent });
            startActivityForResult(chooserIntent, SELECT_PICTURE);
            return true;
                }
        });

    }


    // cette méthode est appelée quand l'appel StartActivityForResult est terminé

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);         // on vérifie que la réponse est bien liée au code de connexion choisi
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Authentification réussie
            if (resultCode == RESULT_OK) {
                Log.v("AndroBoum", "je me suis connecté et mon email est :" +
                        response.getEmail());
                return;
            } else {
                // echec de l'authentification
                if (response == null) {
                    // L'utilisateur a pressé "back", on revient à l'écran principal en fermant l'activité
                    Log.v("AndroBoum", "Back Button appuyé");
                    finish();
                    return;
                }                 // pas de réseau
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.v("AndroBoum", "Erreur réseau");
                    finish();
                    return;
                }
            }

            // une erreur quelconque
            if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                Log.v("AndroBoum", "Erreur inconnue");
                finish();
                return;
            }
        }

        Log.v("AndroBoum", "Réponse inconnue");

        if (requestCode == SELECT_PICTURE) {
            Bitmap selectedImage;
            if (resultCode == RESULT_OK) {
                try {
                    ImageView imageView = (ImageView) findViewById(R.id.imageProfil);

                    boolean isCamera = (data.getData() == null);
                    if (!isCamera) {
                        final Uri imageUri = data.getData();

                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    selectedImage = BitmapFactory.decodeStream(imageStream);}
            else{
                        selectedImage = (Bitmap) data.getExtras().get("data");
                    }             // on redimensionne le bitmap pour ne pas qu'il soit trop grand
                    Bitmap finalbitmap = Bitmap.createScaledBitmap(selectedImage, 500, (selectedImage.getHeight() * 500) / selectedImage.getWidth(), false);
                    imageView.setImageBitmap(finalbitmap);
                }
                catch
                        (Exception e) {
                    Log.v("AndroBoum", e.getMessage());
                }

            }

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // choix de l'action "Paramètres", on ne fait rien       // pour l'instant
                return true;
            case R.id.action_logout:
                // choix de l'action logout             // on termine l'activité ce qui déconnectera l’utilisateur
                finish();
                return true;
            default:
                /// aucune action reconnue
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    protected void onDestroy() {
        // on déconnecte l'utilisateur
        AuthUI.getInstance().signOut(this);
        super.onDestroy();
    }


}



