package com.booker.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.booker.R;
import com.booker.databinding.ActivityLoginBinding;
import com.booker.model.api.Resource;
import com.booker.model.data.Credential;
import com.booker.ui.viewmodel.LoginViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding mLoginBinding;
    private LoginViewModel mLoginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(mLoginBinding.getRoot());
        mLoginViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication())
                .create(LoginViewModel.class);
        setSignInOnClickListener(mLoginBinding.signInButton);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void setSignInOnClickListener(MaterialButton signInButton) {
        signInButton.setOnClickListener(this::authenticate);
    }

    private void authenticate(View v) {
        if (areCredentialsValid()) {
            startProcessing();
            Credential c = new Credential();
            c.setUsername(mLoginBinding.inputUsername.getText().toString());
            c.setPassword(mLoginBinding.inputPassword.getText().toString());
            mLoginViewModel.authenticate(c).observe(LoginActivity.this, jwtToken -> {
                if (jwtToken.getStatus() == Resource.Status.SUCCESS) {
                    mLoginViewModel.saveToken(jwtToken.getData().getToken());
                    onSuccess();
                } else if (jwtToken.getStatus() == Resource.Status.ERROR) {
                    onFailure();
                }
            });
        }
    }

    // TODO: Consider moving validation logic to separate class/viewmodel.
    private boolean areCredentialsValid() {
        boolean isUsernameValid = isUsernameValid();
        boolean isPasswordValid = isPasswordValid();

        if (!isUsernameValid) {
            mLoginBinding.username.setError("Invalid e-mail address!");
            mLoginBinding.username.setErrorEnabled(true);
            mLoginBinding.signInButton.setEnabled(false);
        } else if (!isPasswordValid) {
            mLoginBinding.password.setError("Password should be at least 6 characters long!");
            mLoginBinding.password.setErrorEnabled(true);
            mLoginBinding.signInButton.setEnabled(false);
        } else {
            mLoginBinding.username.setError(null);
            mLoginBinding.username.setErrorEnabled(false);
            mLoginBinding.password.setError(null);
            mLoginBinding.password.setErrorEnabled(false);
            mLoginBinding.signInButton.setEnabled(true);
        }
        return isUsernameValid && isPasswordValid;
    }

    private Boolean isUsernameValid() {
        String username = mLoginBinding.inputUsername.getText().toString();

        Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|" +
                "\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*" +
                "\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])" +
                "|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:" +
                "[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])");
        Matcher matcher = pattern.matcher(username);

        return matcher.matches();
    }

    private Boolean isPasswordValid() {
        return mLoginBinding.inputPassword.getText().toString().length() > 5;
    }

    private void startProcessing() {
        mLoginBinding.username.setEnabled(false);
        mLoginBinding.password.setEnabled(false);
        mLoginBinding.signInButton.setEnabled(false);
        mLoginBinding.progressCircular.setVisibility(View.VISIBLE);
    }

    private void stopProcessing() {
        mLoginBinding.progressCircular.setVisibility(View.GONE);
        mLoginBinding.signInButton.setEnabled(true);
        mLoginBinding.password.setEnabled(true);
        mLoginBinding.username.setEnabled(true);
    }

    private void onSuccess() {
        stopProcessing();
        startMainActivity();
    }

    private void onFailure() {
        stopProcessing();
        Snackbar.make(mLoginBinding.getRoot(),
                R.string.error_login_failed,
                BaseTransientBottomBar.LENGTH_LONG)
                .show();
    }

    private void startMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}