package kr.co.ajcc.wms.menu.registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.menu.common.Util;
import kr.co.ajcc.wms.menu.custom.BaseCompatActivity;
import kr.co.ajcc.wms.menu.popup.LocationListPopup;
import kr.co.ajcc.wms.menu.popup.OneBtnPopup;

/**
 * 입고등록
 */

public class RegistrationActivity extends BaseCompatActivity {
    Context mContext;

    LocationListPopup mLocationListPopup;
    OneBtnPopup mOneBtnPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.act_registration);

        findViewById(R.id.bt_menu).setOnClickListener(onClickListener);
        findViewById(R.id.bt_back).setOnClickListener(onClickListener);
        findViewById(R.id.bt_change).setOnClickListener(onClickListener);
        findViewById(R.id.bt_search).setOnClickListener(onClickListener);
        findViewById(R.id.bt_select).setOnClickListener(onClickListener);
        findViewById(R.id.bt_next).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int view = v.getId();
            switch (view) {
                case R.id.bt_menu:
                    break;
                case R.id.bt_back:
                    finish();
                    break;
                case R.id.bt_change:
                    break;
                case R.id.bt_search:
                    ArrayList<String> list = new ArrayList<>();
                    list.add("로케이션 1");
                    list.add("로케이션 2");
                    list.add("로케이션 3");
                    list.add("로케이션 4");
                    list.add("로케이션 5");
                    mLocationListPopup = new LocationListPopup(RegistrationActivity.this, list, R.drawable.popup_title_searchloc, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                String result = (String)msg.obj;
                                Util.Toast(mContext, result);
                                mLocationListPopup.hideDialog();
                            }
                        }
                    });
                    break;
                case R.id.bt_select:
                    break;
                case R.id.bt_next:
                    mOneBtnPopup = new OneBtnPopup(RegistrationActivity.this, "품질 검사 완료 후 입고등록을 하십시오.", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                String result = (String)msg.obj;
                                Util.Toast(mContext, result);
                                mOneBtnPopup.hideDialog();
                            }
                        }
                    });
                    break;
            }
        }
    };
}
