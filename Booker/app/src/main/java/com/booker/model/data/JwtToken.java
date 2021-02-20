package com.booker.model.data;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JwtToken {
    @Expose
    private String username;

    @Expose
    private String token;
}
