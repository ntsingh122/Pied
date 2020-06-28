package com.example.news;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Feed extends AppCompatActivity {

    private Toolbar mToolBar;
    private FirebaseAuth mAuth;
    View parentLayout;
    Intent intent;
    private ViewPager mviewPager;
    private SectionsPageAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        mAuth = FirebaseAuth.getInstance();
        parentLayout = findViewById(android.R.id.content);

        mToolBar =  findViewById(R.id.feed_bar);
        mToolBar.setTitle("Chat");
        setSupportActionBar(mToolBar);

        mviewPager = findViewById(R.id.feed_view_pager);
        mSectionsPagerAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mviewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.feed_tool_bar);
        mTabLayout.setupWithViewPager(mviewPager);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentEmail = currentUser.getEmail();
            printInSnackbar("Current User: " + currentEmail);
        }
        else
        {
            printInSnackbar("No User Logged in");
            finish();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case R.id.logoutMenuBtn:
                logout();
                break;

            case R.id.about:
                AlertDialog.Builder alertDialoguilder = new AlertDialog.Builder(Feed.this).setTitle("About").setMessage("Chat Application built by Nitin ");
                AlertDialog alertDialog = alertDialoguilder.show();
                break;

            case R.id.settings:
                startActivity(new Intent(Feed.this,SettingsActivity.class));
                break;

            default:
                return true;


        }

        return true;


    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

    }


    private void logout(){
        FirebaseAuth.getInstance().signOut();
        if (mAuth.getCurrentUser() == null) {
            intent = new Intent(Feed.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else
            printInSnackbar("Couddln't Log Out! Try Again ");
    }

    private void printInSnackbar(String message){
        Snackbar.make(parentLayout,message,Snackbar.LENGTH_SHORT).show();
    }

}
