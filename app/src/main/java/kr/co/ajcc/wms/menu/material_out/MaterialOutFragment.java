package kr.co.ajcc.wms.menu.material_out;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.custom.CommonFragment;
import kr.co.ajcc.wms.custom.MergeAdapter;
import kr.co.ajcc.wms.menu.popup.OutMeterialListPopup;
import kr.co.ajcc.wms.menu.registration.RegistrationView;
import kr.co.ajcc.wms.model.RegistrationModel;

public class MaterialOutFragment extends CommonFragment {
    Context mContext;

    ListView m_listView;
    TextView text_empty, text_input, text_out;
    EditText et_location;
    OutMeterialListPopup mLocationListPopup;
    MergeAdapter mMergeAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_material_out, container, false);

        m_listView = v.findViewById(R.id.m_listView);
        text_empty = v.findViewById(R.id.text_empty);
        et_location = v.findViewById(R.id.et_location);
        text_input = v.findViewById(R.id.text_input);
        text_out = v.findViewById(R.id.text_out);
        mMergeAdapter = new MergeAdapter();
        v.findViewById(R.id.bt_search).setOnClickListener(onClickListener);
        return v;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int view = v.getId();

            switch (view){
                case R.id.bt_search:
                    ArrayList<String> list = new ArrayList<>();
                    list.add("로케이션1");
                    list.add("로케이션2");
                    list.add("로케이션3");
                    list.add("로케이션4");
                    list.add("로케이션5");
                    mLocationListPopup = new OutMeterialListPopup(getActivity(), list, R.drawable.popup_title_searchrelease, new Handler(){
                        @Override
                        public void handleMessage(Message msg){
                            if (msg.what ==1 ){
                                String result =  (String)msg.obj;
                                et_location.setText(result);
                                mLocationListPopup.hideDialog();

                                for (int i=0; i< 3; i++){
                                    RegistrationModel model = new RegistrationModel();
                                    model.setProduct("품명"+(i+1));
                                    model.setStandard("규격"+(i+1));
                                    model.setCount(1000-(i*100));

                                    RegistrationView view = new RegistrationView(getActivity());
                                    view.setData(model);

                                    mMergeAdapter.addView(view);
                                }

                                m_listView.setAdapter(mMergeAdapter);
                                mMergeAdapter.notifyDataSetChanged();

                                text_empty.setVisibility(View.GONE);
                                m_listView.setVisibility(View.VISIBLE);
                            }else if (msg.what == 2){
                                String result = (String)msg.obj;
                                text_out.setText(result);

                            }else if (msg.what == 3){
                                String result = (String) msg.obj;
                                text_input.setText(result);
                                mLocationListPopup.hideDialog();
                            }
                        }
                    });
                    break;
            }

        }
    };



}
