package com.booker.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.booker.R;
import com.booker.databinding.ActivityMainBinding;
import com.booker.databinding.NavDrawerHeaderBinding;
import com.booker.model.api.Resource;
import com.booker.model.data.User;
import com.booker.ui.view.fragment.HomeFragment;
import com.booker.ui.viewmodel.UserViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MainActivity extends AppCompatActivity {
    private UserViewModel mUserViewModel;
    private ActivityMainBinding mBinding;
    private HomeFragment mHomeFragment;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.appBar);
        setNavigationDrawer(mBinding.drawerLayout);
        mBinding.navDrawer.setNavigationItemSelectedListener(this::menuItemClicked);

        mUserViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication())
                .create(UserViewModel.class);

        mUserViewModel.getCurrentUser().observe(this, this::loadUserDetails);
        startHomeFragment(savedInstanceState);
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

    private void loadUserDetails(Resource<User> userResource) {
        NavDrawerHeaderBinding bind = NavDrawerHeaderBinding.bind(mBinding.navDrawer.getHeaderView(0));
        bind.progressBar.setVisibility(View.VISIBLE);
        if (userResource.getStatus() == Resource.Status.SUCCESS) {
            mUser = userResource.getData();
            bind.userDisplayName.setText(mUser.getDisplayName());
            bind.userEmail.setText(mUser.getUsername());
            bind.userRoom.setText(getString(R.string.msg_room, mUser.getRoom()));
        } else if (userResource.getStatus() == Resource.Status.ERROR) {
            Snackbar.make(mBinding.getRoot(),
                    getString(R.string.msg_user_data_load_failed),
                    BaseTransientBottomBar.LENGTH_LONG)
                    .show();
        }
        bind.progressBar.setVisibility(View.GONE);
    }

    private void setNavigationDrawer(DrawerLayout drawerLayout) {
        ActionBarDrawerToggle drawerToggle = getDrawerToggle();
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
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
        if (item.getItemId() == R.id.btn_nav_home) {
            closeDrawer();
            startHomeFragment(null);
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

    private void startHomeFragment(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mHomeFragment = (HomeFragment) getSupportFragmentManager().getFragment(savedInstanceState, "homeFragment");
        else
            mHomeFragment = HomeFragment.newInstance();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(
                mBinding.fragmentPlaceholder.getId(),
                mHomeFragment,
                mHomeFragment.getClass().getSimpleName())
                .commit();
    }

    private void startLoginActivity() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}