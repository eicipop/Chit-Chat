package com.rajora.arun.chat.chit.chitchatdevelopers.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rajora.arun.chat.chit.authenticator.login.Login;
import com.rajora.arun.chat.chit.authenticator.login.User_Metadata;
import com.rajora.arun.chat.chit.chitchatdevelopers.R;
import com.rajora.arun.chat.chit.chitchatdevelopers.contentProviders.BotContentProvider;
import com.rajora.arun.chat.chit.chitchatdevelopers.database.BotContracts;
import com.rajora.arun.chat.chit.chitchatdevelopers.database.BotDatabase;
import com.rajora.arun.chat.chit.chitchatdevelopers.fragments.BotsListFragment;
import com.rajora.arun.chat.chit.chitchatdevelopers.recyclerview_adapters.adapter_bot_item;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_LOGIN=100;
    private static final int REQUEST_CODE_PROFILE=200;

    final static String FRAGMENT_TAG_CHAT_LIST="bot_list_fragment_tag";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.mainAppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(this);

        sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
        if(sharedPreferences.contains("login_status") && sharedPreferences.getString("login_status","").equals("complete")) {
            if(sharedPreferences.contains("first_profile_edit") && sharedPreferences.getBoolean("first_profile_edit",false)){
                showChatFragment(false);
            }
            else{
                showProfileEdit();
            }
        }
        else{
            startFirebaseForAuthentication();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(canShowChatFragment()) {
            showChatFragment(false);
        }
    }

    private void startFirebaseForAuthentication(){

        final Intent intent=new Intent(this,Login.class);
        startActivityForResult(intent,REQUEST_CODE_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==REQUEST_CODE_LOGIN && resultCode== Activity.RESULT_OK){
            if(sharedPreferences.contains("login_status") && sharedPreferences.getString("login_status","").equals("complete"))
            {
                showProfileEdit();
            }
            else{
                finish();
            }
        }
        else if(requestCode==REQUEST_CODE_PROFILE){
            if(canShowChatFragment()) {
                showChatFragment(true);
            }
        }
    }

    private void showProfileEdit(){
        Intent intent=new Intent(this,ProfileEditActivity.class);
        startActivityForResult(intent,REQUEST_CODE_PROFILE);
    }

    private void showChatFragment(boolean commitAllowingStateLoss){
        if(commitAllowingStateLoss){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_bot_list_holder,new BotsListFragment(),FRAGMENT_TAG_CHAT_LIST).commit();
        }
        else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_bot_list_holder,new BotsListFragment(),FRAGMENT_TAG_CHAT_LIST).commitAllowingStateLoss();
        }
    }

    private boolean canShowChatFragment(){
        return sharedPreferences.contains("login_status") && sharedPreferences.getString("login_status","").equals("complete")
                && getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_CHAT_LIST)==null;
    }

}
