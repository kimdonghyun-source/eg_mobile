package kr.co.ajcc.wms.menu.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.menu.custom.BaseCompatActivity;
import kr.co.ajcc.wms.menu.login.LoginActivity;

public class SplashActivity extends BaseCompatActivity {
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_splash);

        mContext = this;

        ImageView iv_splash = findViewById(R.id.iv_splash);

        Handler delayHandler = new Handler(Looper.getMainLooper());
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO
                startActivity(new Intent(mContext, LoginActivity.class));
            }
        }, 1000);
    }
}
