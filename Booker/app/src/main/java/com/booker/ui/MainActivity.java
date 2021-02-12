package com.booker.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.booker.R;
import com.booker.model.api.ApiClient;
import com.booker.model.api.pojo.User;
import com.booker.databinding.ActivityMainBinding;
import com.booker.ui.fragment.HomeFragment;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;
    private HomeFragment mHomeFragment;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.appBar);

        ActionBarDrawerToggle drawerToggle = getDrawerToggle();
        mBinding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        mBinding.navDrawer.setNavigationItemSelectedListener(this::menuItemClicked);

        SharedPreferences sp = getSharedPreferences(getString(R.string.pref_file), MODE_PRIVATE);

        String bearer = "Bearer " + sp.getString("dorm.booker.jwt", null);
        Call<User> userCall = ApiClient.getUserService().getCurrentUser(bearer);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    mUser = response.body();

                    View view = mBinding.navDrawer.getHeaderView(0);
                    TextView username = view.findViewById(R.id.app_current_username);
                    username.setText(mUser.getDisplayName());

                    if (savedInstanceState != null)
                        mHomeFragment = (HomeFragment) getSupportFragmentManager().getFragment(savedInstanceState, "homeFragment");
                    else
                        mHomeFragment = HomeFragment.newInstance(mUser);

                    setDefaultFragment(mHomeFragment);

                    TextView room = view.findViewById(R.id.app_current_room);
                    room.setText(getString(R.string.msg_room, mUser.getRoom()));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Snackbar.make(mBinding.getRoot(), t.getMessage(), BaseTransientBottomBar.LENGTH_LONG);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(
                outState,
                "homeFragment",
                mHomeFragment
        );
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            mBinding.navDrawer.setCheckedItem(R.id.btn_nav_home);
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    private void setDefaultFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_placeholder,
                fragment,
                fragment.getClass().getSimpleName())
                .commit();
    }

    @NotNull
    private ActionBarDrawerToggle getDrawerToggle() {
        return new ActionBarDrawerToggle(
                MainActivity.this,
                mBinding.drawerLayout,
                mBinding.appBar,
                R.string.open_nav_drawer,
                R.string.close_nav_drawer
        );
    }

    private boolean menuItemClicked(MenuItem item) {
        if(item.getItemId() == R.id.btn_nav_home) {
            closeDrawer();
            loadHomeFragment();
            return true;
        } else if (item.getItemId() == R.id.btn_nav_sign_out) {
            closeDrawer();
            startLoginActivity();
            return true;
        } else {
            return false;
        }
    }

    private void closeDrawer() {
        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void loadHomeFragment() {
        mHomeFragment = HomeFragment.newInstance(mUser);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(
                mBinding.fragmentPlaceholder.getId(),
                mHomeFragment,
                mHomeFragment.getClass().getSimpleName())
                .commit();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}