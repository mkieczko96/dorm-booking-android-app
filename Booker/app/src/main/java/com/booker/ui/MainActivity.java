package com.booker.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.booker.R;
import com.booker.api.ApiClient;
import com.booker.data.models.User;
import com.booker.databinding.ActivityMainBinding;
import com.booker.ui.fragment.HomeFragment;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private User currentUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBar);

        ActionBarDrawerToggle drawerToggle = getDrawerToggle();
        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        binding.navDrawer.setNavigationItemSelectedListener(this::menuItemClicked);

        SharedPreferences sp = getSharedPreferences(getString(R.string.pref_file), MODE_PRIVATE);

        String bearer = "Bearer " + sp.getString("dorm.booker.jwt", null);
        Call<User> userCall = ApiClient.getUserService().getCurrentUser(bearer);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    currentUser = response.body();

                    View view = binding.navDrawer.getHeaderView(0);
                    TextView username = view.findViewById(R.id.app_current_username);
                    username.setText(currentUser.getDisplayName());

                    setDefaultFragment(HomeFragment.newInstance(currentUser.getId()));

                    TextView room = view.findViewById(R.id.app_current_room);
                    room.setText(getString(R.string.nav_header_room, currentUser.getRoom()));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Snackbar.make(binding.getRoot(), t.getMessage(), BaseTransientBottomBar.LENGTH_LONG);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            binding.navDrawer.setCheckedItem(R.id.btn_nav_home);
            getSupportFragmentManager().popBackStack();
        } else {
            startLoginActivity();
        }
    }

    private void setDefaultFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_placeholder,
                fragment,
                fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    @NotNull
    private ActionBarDrawerToggle getDrawerToggle() {
        return new ActionBarDrawerToggle(
                MainActivity.this,
                binding.drawerLayout,
                binding.appBar,
                R.string.open_nav_drawer,
                R.string.close_nav_drawer
        );
    }

    private boolean menuItemClicked(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_nav_home:
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                loadHomeFragment();
                return true;
            default:
                return false;
        }
    }

    private void loadHomeFragment() {
        HomeFragment fragment = HomeFragment.newInstance(currentUser.getId());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(
                binding.fragmentPlaceholder.getId(),
                fragment,
                fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    private void revokeToken() {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.remove("token");
        edit.apply();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}