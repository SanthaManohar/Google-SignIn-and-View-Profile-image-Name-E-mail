package santhakumar.com.googleloginsampleone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener{

    private LinearLayout linear;
    private Button logOut,saveSQlite,saveFirebase;
    private SignInButton signIn;
    private TextView name,email;
    private ImageView image;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;

    DatabaseHelper myDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDB = new DatabaseHelper(MainActivity.this);

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        linear=(LinearLayout)findViewById(R.id.linear);
        logOut =(Button)findViewById(R.id.logOut);
        saveSQlite = (Button)findViewById(R.id.saveSQlite);
        saveFirebase = (Button)findViewById(R.id.saveFirebase);
        signIn = (SignInButton)findViewById(R.id.sign_in_button);
        name = (TextView)findViewById(R.id.name);
        email = (TextView)findViewById(R.id.email);
        image = (ImageView)findViewById(R.id.imageview);
        signIn.setOnClickListener(this);
        logOut.setOnClickListener(this);
        saveSQlite.setOnClickListener(this);
        saveFirebase.setOnClickListener(this);
        linear.setVisibility(View.GONE);

        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.sign_in_button:
                LogIn();
                break;
            case R.id.logOut:
                LogOut();
                break;
            case R.id.saveSQlite:
                Save();
                break;
            case R.id.saveFirebase:
                SaveF();
                break;

        }

    }

    private void SaveF() {

        Toast.makeText(this, "Go to Firebase", Toast.LENGTH_SHORT).show();

    }

    private void Save() {
        String name_str = name.getText().toString().trim();
        String email_str = email.getText().toString().trim();
        boolean result = myDB.saveData(name_str,email_str,imageViewToByteArray(image));
        if(result == true){
            Toast.makeText(this, "Saved into SQlite", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Not-Saved into SQlite", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] imageViewToByteArray(ImageView image) {

        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;


    }

    private void LogOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });
    }

    private void LogIn() {

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handlereslt(result);
        }

    }

    private void handlereslt(GoogleSignInResult result) {

        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            String name_str = account.getDisplayName();
            String email_str = account.getEmail();
            String image_url = account.getPhotoUrl().toString();
            name.setText(name_str);
            email.setText(email_str);
            Glide.with(this).load(image_url).into(image);
            updateUI(true);

        }else{
            updateUI(false);
        }

    }

    private void updateUI(boolean isLogin) {
        if(isLogin){
            linear.setVisibility(View.VISIBLE);
            signIn.setVisibility(View.GONE);
        }else{
            linear.setVisibility(View.GONE);
            signIn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
