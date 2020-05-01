package kr.co.ajcc.wms;

import android.app.Application;

import kr.co.ajcc.wms.model.UserInfoModel;

public class GlobalApplication extends Application {

    static GlobalApplication instance;

    //유저정보
    UserInfoModel.Items userInfoModel;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }

    public static GlobalApplication getInstance() {
        if (instance != null) {
            return instance;
        } else {
            instance = new GlobalApplication();
            return instance;
        }
    }

    public UserInfoModel.Items getUserInfoModel() {
        return userInfoModel;
    }

    public void setUserInfoModel(UserInfoModel.Items model) {
        this.userInfoModel = model;
    }
}
