package kr.co.ajcc.wms.menu.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.custom.CommonCompatActivity;
import kr.co.ajcc.wms.menu.login.LoginActivity;

public class SplashActivity extends CommonCompatActivity {
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_splash);

        mContext = this;

        //ImageView iv_splash = findViewById(R.id.iv_splash);

        Handler delayHandler = new Handler(Looper.getMainLooper());
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO
                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }, 1000);
    }
}
