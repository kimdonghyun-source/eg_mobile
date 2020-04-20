package kr.co.ajcc.wms.menu.registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.menu.common.Util;
import kr.co.ajcc.wms.menu.custom.BaseCompatActivity;
import kr.co.ajcc.wms.menu.popup.LocationListPopup;
import kr.co.ajcc.wms.menu.popup.OneBtnPopup;
import kr.co.ajcc.wms.menu.spinner.SpinnerAdapter;

/**
 * 입고등록
 */

public class RegistrationActivity extends BaseCompatActivity {
    Context mContext;

    LocationListPopup mLocationListPopup;
    OneBtnPopup mOneBtnPopup;

    Spinner mSpinner;
    int mSpinnerSelect = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.act_registration);

        findViewById(R.id.bt_menu).setOnClickListener(onClickListener);
        findViewById(R.id.bt_back).setOnClickListener(onClickListener);
        findViewById(R.id.bt_change).setOnClickListener(onClickListener);
        findViewById(R.id.bt_search).setOnClickListener(onClickListener);
        findViewById(R.id.bt_next).setOnClickListener(onClickListener);

        List<String> list = new ArrayList<String>();
        list.add("창고 1");
        list.add("창고 2");
        list.add("창고 3");
        list.add("창고 4");
        list.add("창고 5");

        mSpinner =  findViewById(R.id.spinner);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(mContext, list, mSpinner);
        mSpinner.setAdapter(spinnerAdapter);
        mSpinner.setOnItemSelectedListener(onItemSelectedListener);
        mSpinner.setSelection(mSpinnerSelect);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener(){

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //최초에 setOnItemSelectedListener 하면 이벤트가 들어오기 때문에
            //onResume에서 mSpinnerSelect에 현재 선택된 position을 넣고 여기서 비교
            if(mSpinnerSelect == position)return;

            mSpinnerSelect = position;

            String item = (String) mSpinner.getSelectedItem();
            Util.Toast(mContext, item+" 선택");
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    };

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
