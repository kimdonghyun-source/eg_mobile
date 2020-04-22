package kr.co.ajcc.wms.menu.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.common.Define;
import kr.co.ajcc.wms.custom.CommonCompatActivity;

public class MainActivity extends CommonCompatActivity {
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
            Intent intent = new Intent(mContext, BaseActivity.class);
            switch (view){
                case R.id.bt_menu_1:
                    intent.putExtra("menu", Define.MENU_REGISTRATION);
                    break;
                case R.id.bt_menu_2:
                    intent.putExtra("menu", Define.MENU_LOCATION);
                    break;
                case R.id.bt_menu_3:
                    intent.putExtra("menu", Define.MENU_MATERIAL_OUT);
                    break;
                case R.id.bt_menu_4:
                    intent.putExtra("menu", Define.MENU_PRODUCTION_IN);
                    break;
                case R.id.bt_menu_5:
                    intent.putExtra("menu", Define.MENU_PRODUCT_OUT);
                    break;
                case R.id.bt_menu_6:
                    intent.putExtra("menu", Define.MENU_PALLET);
                    break;
                case R.id.bt_menu_7:
                    intent.putExtra("menu", Define.MENU_CONFIG);
                    break;
            }
            startActivity(intent);
        }
    };
}
