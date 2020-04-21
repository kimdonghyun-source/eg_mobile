package kr.co.ajcc.wms.menu.registration;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Date;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.Utils;
import kr.co.ajcc.wms.model.RegistrationModel;

public class RegistrationView extends LinearLayout {
    Activity mActivity;

    RegistrationModel mModel;

    TextView tv_product;
    TextView tv_standard;
    EditText et_count;
    TextView tv_count;

    public RegistrationView(Activity activity) {
        super(activity);

        mActivity = activity;

        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.cell_registration, null);
        addView(v);

        tv_product = v.findViewById(R.id.tv_product);
        tv_standard = v.findViewById(R.id.tv_standard);
        et_count = v.findViewById(R.id.et_count);
        tv_count = v.findViewById(R.id.tv_count);
    }

    public void setData(RegistrationModel data){
        mModel = data;

        tv_product.setText(mModel.getProduct());
        tv_standard.setText(mModel.getStandard());
        tv_count.setText(Utils.setComma(mModel.getCount()));
    }

    public RegistrationModel getData(){
        return mModel;
    }

}
