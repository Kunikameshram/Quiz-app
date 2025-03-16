package com.example.quizquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private FrameLayout frameLayout;
    private DrawerLayout drawer;
    private ImageView drawerBtn;
    private TextView drawerProfileName;
    private ImageView drawerProfileImg;
    private TextView drawerProfileText;
    private FirebaseAuth mAuth;
    private NavigationView navigationView;
    BottomNavigationView bottomNavigationView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setFragment(new CategoryFragment());
                    return true;
                case R.id.navigation_leaderboard:
                    setFragment(new LeaderBoardFragment());
                    return true;
                case R.id.navigation_myaccount:
                    setFragment(new MyProfileFragment());
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        String navLayout = getIntent().getStringExtra("LAYOUT");

        if(navLayout != null)
        {
            if(navLayout.compareTo("LEADERBOARD") == 0)
                bottomNavigationView.setSelectedItemId(R.id.navigation_leaderboard);
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null)
        {
            String name = user.getDisplayName();

            Uri photoUrl = user.getPhotoUrl();
            if(photoUrl != null)
            {
                //set Profile img
                drawerProfileImg.setVisibility(View.VISIBLE);
                drawerProfileText.setVisibility(View.GONE);

                Glide.with(this).load(photoUrl).into(drawerProfileImg);
                //drawerProfileImg.setImageURI(photoUrl);
            }
            else
            {
                //set Alphabet img
                drawerProfileImg.setVisibility(View.GONE);
                drawerProfileText.setVisibility(View.VISIBLE);
                drawerProfileText.setText(name.toUpperCase().substring(0,1));
            }

            drawerProfileName.setText(name);
        }

    }


    private void init()
    {
        initViews();

        mAuth = FirebaseAuth.getInstance();

        drawerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( ! drawer.isDrawerOpen(GravityCompat.START)) {

                    if(navigationView.getCheckedItem() != null)
                        navigationView.getCheckedItem().setChecked(false);
                    drawer.openDrawer(GravityCompat.START);

                }
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        setFragment(new CategoryFragment());
    }

    private void initViews()
    {
        drawer = findViewById(R.id.main_drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerProfileName = navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_uname);
        drawerProfileImg = navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_img);
        drawerProfileText = navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_text_img);
        frameLayout = findViewById(R.id.main_frame);
        drawerBtn = findViewById(R.id.main_drawer_btn);

    }


    private void setFragment(Fragment fragment){
        FragmentTransaction fragment_trans = getSupportFragmentManager().beginTransaction();
        fragment_trans.replace(frameLayout.getId(),fragment);
        fragment_trans.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        switch (id)
        {
            case R.id.nav_profile:
                bottomNavigationView.setSelectedItemId(R.id.navigation_myaccount);
                break;
            case R.id.nav_leader:
                bottomNavigationView.setSelectedItemId(R.id.navigation_leaderboard);
                break;

            case R.id.nav_bookmark:

                Intent intent = new Intent(MainActivity.this,BookMarksActivity.class);
                startActivity(intent);

                break;

            case R.id.nav_share:

                break;

            case R.id.nav_send:

                break;


                default:

        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
