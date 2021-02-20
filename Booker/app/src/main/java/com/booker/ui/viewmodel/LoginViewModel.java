package com.booker.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.booker.model.PreferenceProvider;
import com.booker.model.api.Resource;
import com.booker.model.data.Credential;
import com.booker.model.data.JwtToken;
import com.booker.model.repository.LoginRepository;

public class LoginViewModel extends AndroidViewModel {
    private final LoginRepository mLoginRepository;
    private final PreferenceProvider mProvider;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        mProvider = new PreferenceProvider(application);
        mLoginRepository = new LoginRepository();
    }

    public LiveData<Resource<JwtToken>> authenticate(Credential credential) {
        return mLoginRepository.authenticate(credential.getBase64Encoded());
    }

    public void saveToken(String token) {
        mProvider.saveToken(token);
    }
}
