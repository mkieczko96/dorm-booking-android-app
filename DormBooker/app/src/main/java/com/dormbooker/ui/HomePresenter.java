package com.dormbooker.ui;

public class HomePresenter {
    HomeActivity view;

    public void bind(HomeActivity view) {
        this.view = view;
    }

    public void unbind(){
        this.view = null;
    }

}
