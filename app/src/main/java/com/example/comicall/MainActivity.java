package com.example.comicall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    HomeFragment homeFragment = new HomeFragment();
    FavoritesFragment favoritesFragment = new FavoritesFragment();
    SearchFragment searchFragment = new SearchFragment();
    NotificationsFragment notificationsFragment =new NotificationsFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Bundle extras = getIntent().getExtras();

        SharedPreferences preferences = getSharedPreferences("MY_APP_PREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("USER_EMAIL", extras.getString("email"));
        editor.apply();

        loadFragment(homeFragment);
    }
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.homeFragment:
                    loadFragment(homeFragment);
                    return true;
                case R.id.favoritesFragment:
                    loadFragment(favoritesFragment);
                    return true;
                case R.id.searchFragment:
                    loadFragment(searchFragment);
                    return true;
                case R.id.notificationsFragment:
                    loadFragment(notificationsFragment);
                    return true;
                case R.id.profileFragment:
                    loadFragment(profileFragment);
                    return true;
            }
            return false;
        }
    };
    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }
}