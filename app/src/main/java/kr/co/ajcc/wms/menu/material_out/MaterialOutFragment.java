package kr.co.ajcc.wms.menu.material_out;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.honeywell.aidc.BarcodeReadEvent;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.common.Utils;
import kr.co.ajcc.wms.custom.CommonFragment;
import kr.co.ajcc.wms.honeywell.AidcReader;
import kr.co.ajcc.wms.menu.popup.OutMeterialListPopup;
import kr.co.ajcc.wms.model.MaterialOutDetailModel;
import kr.co.ajcc.wms.model.MaterialOutListModel;
import kr.co.ajcc.wms.model.ResultModel;
import kr.co.ajcc.wms.model.WarehouseModel;
import kr.co.ajcc.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaterialOutFragment extends CommonFragment {
    Context mContext;

    TextView tv_warehouse, tv_input;
    EditText et_order;
    OutMeterialListPopup mOutMeterialListPopup;

    RecyclerView recycleview;
    MaterialOutAdapter mAdapter;

    //리스트가 없을때 보여지는 text
    TextView tv_empty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
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

                    mAdapter.clearData();
                    mAdapter.notifyDataSetChanged();

                    requestOrderDetail(barcode);
                }
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        AidcReader.getInstance().release();
        AidcReader.getInstance().setListenerHandler(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_material_out, container, false);

        tv_empty = v.findViewById(R.id.tv_empty);
        et_order = v.findViewById(R.id.et_order);
        tv_warehouse = v.findViewById(R.id.tv_warehouse);
        tv_input = v.findViewById(R.id.tv_input);
        v.findViewById(R.id.bt_search).setOnClickListener(onClickListener);

        //정제영 테스트
        v.findViewById(R.id.bt_order).setOnClickListener(onClickListener);

        recycleview = v.findViewById(R.id.recycleview);
        recycleview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new MaterialOutAdapter(getActivity());
        recycleview.setAdapter(mAdapter);

        return v;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int view = v.getId();

            switch (view){
                case R.id.bt_search:
                    requestWarehouse();
                    break;
                case R.id.bt_order:
                    et_order.setText("100-20200323-001-160");
                    requestOrderDetail("100-20200323-001-160");
                    break;
            }
        }
    };

    /**
     * 창고 검색
     */
    private void requestWarehouse() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<WarehouseModel> call = service.postWarehouse("sp_pda_mst_wh_list", "M");

        call.enqueue(new Callback<WarehouseModel>() {
            @Override
            public void onResponse(Call<WarehouseModel> call, Response<WarehouseModel> response) {
                if(response.isSuccessful()){
                    WarehouseModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if(model.getFlag() == ResultModel.SUCCESS) {
                            mOutMeterialListPopup = new OutMeterialListPopup(getActivity(), model.getItems(), R.drawable.popup_title_searchloc, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        MaterialOutListModel.Items model = (MaterialOutListModel.Items) msg.obj;
                                        et_order.setText(model.getOut_slip_no());
                                        tv_warehouse.setText(model.getWh_name_out());
                                        tv_input.setText(model.getWh_name_in());
                                        mOutMeterialListPopup.hideDialog();

                                        requestOrderDetail(model.getOut_slip_no());
                                    }
                                }
                            });
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
            public void onFailure(Call<WarehouseModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }

    /**
     * 불출지시 상세
     */
    private void requestOrderDetail(String param) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<MaterialOutDetailModel> call = service.postOutOrderDetail("sp_pda_out_detail", param);

        call.enqueue(new Callback<MaterialOutDetailModel>() {
            @Override
            public void onResponse(Call<MaterialOutDetailModel> call, Response<MaterialOutDetailModel> response) {
                if(response.isSuccessful()){
                    MaterialOutDetailModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if(model.getFlag() == ResultModel.SUCCESS) {
                            if(model.getItems().size() > 0) {
                                mAdapter.setData(model.getItems());
                                mAdapter.notifyDataSetChanged();

                                tv_empty.setVisibility(View.GONE);
                                recycleview.setVisibility(View.VISIBLE);
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
            public void onFailure(Call<MaterialOutDetailModel> call, Throwable t) {
                Utils.Log(t.getMessage());
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }
}
