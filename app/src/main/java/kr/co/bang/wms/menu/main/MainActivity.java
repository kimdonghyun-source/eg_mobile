package kr.co.bang.wms.menu.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import kr.co.bang.wms.R;
import kr.co.bang.wms.common.Define;
import kr.co.bang.wms.custom.CommonCompatActivity;
import kr.co.bang.wms.menu.login.LoginActivity;
import kr.co.bang.wms.menu.popup.TwoBtnPopup;

public class MainActivity extends CommonCompatActivity {
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.act_main);


        findViewById(R.id.bt_menu_4).setOnClickListener(onClickListener);
        findViewById(R.id.bt_menu_5).setOnClickListener(onClickListener);
        findViewById(R.id.bt_menu_8).setOnClickListener(onClickListener); //재고실사
        findViewById(R.id.bt_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TwoBtnPopup(MainActivity.this, "로그아웃 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            goLogin();
                        }
                    }
                });
            }
        });
    }

    private void goLogin(){
        Intent i = new Intent(mContext,LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int view = v.getId();
            Intent intent = new Intent(mContext, BaseActivity.class);
            switch (view){

                case R.id.bt_menu_4:
                    intent.putExtra("menu", Define.MENU_PRODUCTION_IN);
                    break;
                case R.id.bt_menu_5:
                    intent.putExtra("menu", Define.MENU_HOUSE_MOVE);
                    break;
                case R.id.bt_menu_8:
                    intent.putExtra("menu", Define.MENU_INVENTORY);
                    break;
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
    };
}
