package kr.co.ajcc.wms.menu.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.menu.custom.BaseCompatActivity;
import kr.co.ajcc.wms.menu.location.Location;
import kr.co.ajcc.wms.menu.print.ConfigActivity;
import kr.co.ajcc.wms.menu.registration.RegistrationActivity;

public class MainActivity extends BaseCompatActivity {
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.act_main);

        findViewById(R.id.bt_menu_1).setOnClickListener(onClickListener);
        findViewById(R.id.bt_menu_2).setOnClickListener(onClickListener);
        findViewById(R.id.bt_menu_3).setOnClickListener(onClickListener);
        findViewById(R.id.bt_menu_4).setOnClickListener(onClickListener);
        findViewById(R.id.bt_menu_5).setOnClickListener(onClickListener);
        findViewById(R.id.bt_menu_6).setOnClickListener(onClickListener);
        findViewById(R.id.bt_menu_7).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int view = v.getId();
            Intent intent = null;
            switch (view){
                case R.id.bt_menu_1:
                    intent = new Intent(mContext, RegistrationActivity.class);
                    startActivity(intent);
                    break;
                case R.id.bt_menu_2:
                    intent = new Intent(mContext, Location.class);
                    startActivity(intent);
                    break;
                case R.id.bt_menu_3:
                    break;
                case R.id.bt_menu_4:
                    break;
                case R.id.bt_menu_5:
                    break;
                case R.id.bt_menu_6:
                    break;
                case R.id.bt_menu_7:
                    intent = new Intent(mContext, ConfigActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };
}
