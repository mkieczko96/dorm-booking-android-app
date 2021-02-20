package com.booker.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.booker.model.PreferenceProvider;
import com.booker.model.api.Resource;
import com.booker.model.data.User;
import com.booker.model.repository.UserRepository;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository mUserRepository;
    private final PreferenceProvider mProvider;
    private LiveData<Resource<User>> mCurrentUser;

    public UserViewModel(@NonNull Application application) {
        super(application);
        mProvider = new PreferenceProvider(application);
        mUserRepository = new UserRepository();
    }

    public LiveData<Resource<User>> getCurrentUser() {
        if(mCurrentUser == null) {
            mCurrentUser = mUserRepository.getCurrentUser(mProvider.getToken());
        }
        return mCurrentUser;
    }
}
