package kr.co.ajcc.wms.menu.product_out;

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
import kr.co.ajcc.wms.menu.popup.OutProductListPopup;

public class ProductOutFragment extends CommonFragment {
    Context mContext;
    EditText et_location;
    OutProductListPopup mLocationListPopup;
    ListView o_listView;
    MergeAdapter mMergeAdapter;
    TextView text_empty, text_customer, text_info;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_product_out, container, false);
        et_location = v.findViewById(R.id.et_location);
        o_listView = v.findViewById(R.id.o_listView);
        text_empty = v.findViewById(R.id.text_empty);
        text_customer = v.findViewById(R.id.text_customer);
        text_info = v.findViewById(R.id.text_info);
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
                    list.add("거래처1");
                    list.add("거래처2");
                    list.add("거래처3");
                    list.add("거래처4");
                    list.add("거래처5");
                    mLocationListPopup = new OutProductListPopup(getActivity(), list, R.drawable.popup_title_searchoutorder, new Handler(){
                        @Override
                        public void handleMessage(Message msg){
                            if (msg.what ==1 ){
                                String result =  (String)msg.obj;
                                text_customer.setText(result);
                                mLocationListPopup.hideDialog();

                                for (int i=0; i< 3; i++){
                                    /*RegistrationModel model = new RegistrationModel();
                                    model.setProduct("품명"+(i+1));
                                    model.setStandard("규격"+(i+1));
                                    model.setCount(1000-(i*100));

                                    LotItemsView view = new LotItemsView(getActivity());
                                    view.setData(model);

                                    mMergeAdapter.addView(view);*/
                                }

                                o_listView.setAdapter(mMergeAdapter);
                                mMergeAdapter.notifyDataSetChanged();

                                text_empty.setVisibility(View.GONE);
                                o_listView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    break;
            }
        }
    };















}
