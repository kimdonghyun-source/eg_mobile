package kr.co.ajcc.wms.menu.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.menu.custom.BaseCompatActivity;
import kr.co.ajcc.wms.menu.main.MainActivity;

public class LoginActivity extends BaseCompatActivity {
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_login);

        mContext = this;

        findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, MainActivity.class));
            }
        });
    }
}
