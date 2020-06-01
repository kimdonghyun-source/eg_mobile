package kr.co.ajcc.wms.menu.product_out;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.honeywell.aidc.BarcodeReadEvent;

import java.util.ArrayList;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.common.Utils;
import kr.co.ajcc.wms.custom.CommonFragment;
import kr.co.ajcc.wms.custom.MergeAdapter;
import kr.co.ajcc.wms.honeywell.AidcReader;
import kr.co.ajcc.wms.menu.popup.OutProductListPopup;
import kr.co.ajcc.wms.menu.production_in.ProductionInAdapter;
import kr.co.ajcc.wms.model.CustomerInfoModel;
import kr.co.ajcc.wms.model.DeliveryOrderModel;
import kr.co.ajcc.wms.model.ResultModel;
import kr.co.ajcc.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductOutFragment extends CommonFragment {
    Context mContext;
    EditText et_location;
    OutProductListPopup mLocationListPopup;
    //ListView o_listView;
    MergeAdapter mMergeAdapter;
    TextView text_empty, text_customer, text_info;

    ProductOutAdapter mAdapter = null;

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
        //o_listView = v.findViewById(R.id.o_listView);
        text_empty = v.findViewById(R.id.text_empty);
        text_customer = v.findViewById(R.id.text_customer);
        text_info = v.findViewById(R.id.text_info);
        mMergeAdapter = new MergeAdapter();
        v.findViewById(R.id.bt_search).setOnClickListener(onClickListener);
        //v.findViewById(R.id.bt_next).setOnClickListener(onClickListener);

        //정제영 테스트
        v.findViewById(R.id.bt_test_1).setOnClickListener(onClickListener);

        RecyclerView recycleview = v.findViewById(R.id.recycleview);
        recycleview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ProductOutAdapter(getActivity());
        recycleview.setAdapter(mAdapter);

        return v;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 100){
            Utils.Log("---------------------------------------------1");
            if(resultCode == Activity.RESULT_OK){
                Utils.Log("---------------------------------------------2");
            }
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        AidcReader.getInstance().claim(mContext);
        AidcReader.getInstance().setListenerHandler(new Handler() {
            @Override
            public void handleMessage(Message msg){
                if(msg.what == 1){
                    BarcodeReadEvent event = (BarcodeReadEvent)msg.obj;
                    String barcode = event.getBarcodeData();
                    text_customer.setText("");
                    text_info.setText("");
                    mAdapter.clearData();

                    requestDeliveryOrderDetail(barcode);
                }
            }
        });
        if(mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        AidcReader.getInstance().release();
        AidcReader.getInstance().setListenerHandler(null);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            int view = v.getId();

            switch (view){
                case R.id.bt_search:
                    mLocationListPopup = new OutProductListPopup(getActivity(), R.drawable.popup_title_searchoutorder, new Handler(){
                        @Override
                        public void handleMessage(Message msg){
                            if (msg.what ==1 ){
                                mLocationListPopup.hideDialog();
                                CustomerInfoModel.CustomerInfo order = (CustomerInfoModel.CustomerInfo)msg.obj;
                                et_location.setText(order.getReq_car_no());
                                text_customer.setText(order.getCst_name());
                                text_info.setText(order.getPo_no());
                                requestDeliveryOrderDetail(order.getReq_car_no());
                            }
                        }
                    });
                    break;
                case R.id.bt_test_1:
                    text_customer.setText("");
                    text_info.setText("");
                    requestDeliveryOrderDetail("100-1-20200310-0703");
                    break;
            }
        }
    };

    /**
     * 출고지시서 상세
     */
    private void requestDeliveryOrderDetail(String param) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<DeliveryOrderModel> call = service.postShipReqDetail("sp_pda_ship_req_detail", param);

        call.enqueue(new Callback<DeliveryOrderModel>() {
            @Override
            public void onResponse(Call<DeliveryOrderModel> call, Response<DeliveryOrderModel> response) {
                if(response.isSuccessful()){
                    DeliveryOrderModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if(model.getFlag() == ResultModel.SUCCESS) {
                            if(model.getItems().size() > 0) {
                                mAdapter.setData(model.getItems());
                                mAdapter.notifyDataSetChanged();
                            }
                        }else{
                            Utils.Toast(mContext, model.getMSG());
                        }
                    }
                }else{
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code()+" : "+response.message());
                }
            }

            @Override
            public void onFailure(Call<DeliveryOrderModel> call, Throwable t) {
                Utils.Log(t.getMessage());
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }
}
