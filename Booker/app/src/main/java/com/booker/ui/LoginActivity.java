package com.booker.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import com.booker.R;
import com.booker.api.ApiClient;
import com.booker.databinding.ActivityLoginBinding;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// TODO: add token session handling
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSignIn.setOnClickListener(view -> authenticate());
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void validate() {
        if (!isUsernameValid()) {
            binding.etUsername.setError("Entered value is not a valid e-mail address!");
            binding.btnSignIn.setEnabled(false);
        } else if (!isPasswordValid()) {
            binding.etPassword.setError("Password should be at least 6 characters long!");
            binding.btnSignIn.setEnabled(false);
        } else {
            binding.etUsername.setError(null);
            binding.etPassword.setError(null);
            binding.btnSignIn.setEnabled(true);
        }
    }

    private Boolean isUsernameValid() {
        String username = binding.etUsername.getText().toString();

        Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])");
        Matcher matcher = pattern.matcher(username);

        return matcher.matches();
    }

    private Boolean isPasswordValid() {
        return binding.etPassword.getText().toString().length() > 5;
    }

    private void authenticate() {
        if (!isUsernameValid() && !isPasswordValid()) {
            validate();
            onAuthenticationFailed();
            return;
        }

        binding.btnSignIn.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String username = binding.etUsername.getText().toString();
        String password = binding.etPassword.getText().toString();

        String httpBasic = "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP);

        Call<Map<Object, Object>> authCall = ApiClient.getLoginService().authenticate(httpBasic);
        authCall.enqueue(new Callback<Map<Object, Object>>() {
            @Override
            public void onResponse(Call<Map<Object, Object>> call, Response<Map<Object, Object>> response) {
                if (response.isSuccessful()) {
                    onAuthenticationSuccess(response);
                } else {
                    onAuthenticationFailed();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Map<Object, Object>> call, Throwable t) {
                Snackbar.make(binding.getRoot(),
                        t.getMessage(),
                        BaseTransientBottomBar.LENGTH_LONG)
                        .show();
                binding.btnSignIn.setEnabled(true);
            }
        });
    }

    private void onAuthenticationSuccess(Response<Map<Object, Object>> user) {
        binding.btnSignIn.setEnabled(true);
        new Handler().postDelayed(() -> {
            saveToken(user.body().get("token").toString());
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }, 700);
        finish();
    }

    private void saveToken(String token) {
        SharedPreferences sp = getSharedPreferences(getString(R.string.pref_file), MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("dorm.booker.jwt", token);
        edit.apply();
    }

    private void onAuthenticationFailed() {
        Snackbar.make(binding.getRoot(),
                R.string.login_failed,
                BaseTransientBottomBar.LENGTH_LONG)
                .show();
        binding.btnSignIn.setEnabled(true);
    }
}