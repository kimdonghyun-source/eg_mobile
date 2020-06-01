package kr.co.ajcc.wms.menu.items;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.common.Utils;
import kr.co.ajcc.wms.model.LotItemsModel;

public class LotItemView extends LinearLayout {
    Activity mActivity;

    LotItemsModel.Items mModel;

    TextView tv_product;
    TextView tv_standard;
    EditText et_count;
    TextView tv_count;

    public LotItemView(Activity activity) {
        super(activity);

        mActivity = activity;

        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.cell_lot_item, null);
        //layout으로는 match_parent가 되지 않아 소스에서 강제로 match_parent가 해줌
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(params);

        addView(v);

        tv_product = v.findViewById(R.id.tv_product);
        tv_standard = v.findViewById(R.id.tv_standard);
        et_count = v.findViewById(R.id.et_count);
        tv_count = v.findViewById(R.id.tv_count);

        tv_product.setSelected(true);

        et_count.addTextChangedListener(new TextWatcher(){
            String result="";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() > 0 && !s.toString().equals(result)) {     // StackOverflow를 막기위해,
                    result = Utils.setComma(Utils.stringToFloat(s.toString().replaceAll(",", "")));   // 에딧텍스트의 값을 변환하여, result에 저장.
                    et_count.setText(result);    // 결과 텍스트 셋팅.
                    et_count.setSelection(result.length());     // 커서를 제일 끝으로 보냄.

                    float cnt = Utils.stringToFloat(s.toString());
                    mModel.setInput_qty(cnt);
                }
            }
        });
    }

    public void setData(LotItemsModel.Items data){
        mModel = data;
        mModel.setInput_qty(0);

        tv_product.setText(mModel.getItm_name());
        tv_standard.setText(mModel.getItm_size());
        tv_count.setText(Utils.setComma(mModel.getInv_qty()));
    }

    public LotItemsModel.Items getData(){
        return mModel;
    }

}
