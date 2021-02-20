package com.booker.model.data;

import android.util.Base64;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Credential {
    private String mUsername;
    private String mPassword;

    public String getBase64Encoded() {
        String combined = mUsername + ":" + mPassword;
        return Base64.encodeToString(
                combined.getBytes(),
                Base64.NO_WRAP
        );
    }
}
