package kr.co.ajcc.wms.menu.material_out;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.honeywell.aidc.BarcodeReadEvent;

import java.util.List;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.common.Define;
import kr.co.ajcc.wms.common.SharedData;
import kr.co.ajcc.wms.common.Utils;
import kr.co.ajcc.wms.custom.CommonFragment;
import kr.co.ajcc.wms.honeywell.AidcReader;
import kr.co.ajcc.wms.menu.main.BaseActivity;
import kr.co.ajcc.wms.menu.popup.OneBtnPopup;
import kr.co.ajcc.wms.menu.popup.OutMeterialListPopup;
import kr.co.ajcc.wms.menu.popup.TwoBtnPopup;
import kr.co.ajcc.wms.model.DeliveryOrderModel;
import kr.co.ajcc.wms.model.MaterialLocAndLotModel;
import kr.co.ajcc.wms.model.MaterialOutDetailModel;
import kr.co.ajcc.wms.model.MaterialOutListModel;
import kr.co.ajcc.wms.model.MaterialSaveModel;
import kr.co.ajcc.wms.model.PalletSnanModel;
import kr.co.ajcc.wms.model.ResultModel;
import kr.co.ajcc.wms.model.WarehouseModel;
import kr.co.ajcc.wms.network.ApiClientService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
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

    //불출 창고 코드
    String mWarehouseCode;
    //불출지시 상세
    MaterialOutDetailModel mMaterialOutDetailModel;

    TwoBtnPopup mTwoBtnPopup;
    OneBtnPopup mOneBtnPopup;

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
        v.findViewById(R.id.bt_next).setOnClickListener(onClickListener);

        //정제영 테스트
        v.findViewById(R.id.bt_order).setOnClickListener(onClickListener);

        recycleview = v.findViewById(R.id.recycleview);
        recycleview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new MaterialOutAdapter(getActivity());
        recycleview.setAdapter(mAdapter);

        mAdapter.setRetHandler(new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what >= 0){
                    goMaterialPicking(msg.what);
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 100){
            if(resultCode == Activity.RESULT_OK){
                mMaterialOutDetailModel = (MaterialOutDetailModel) data.getSerializableExtra("model");
                mAdapter.setData(mMaterialOutDetailModel.getItems());
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void goMaterialPicking(int position){
        Intent intent = new Intent(mContext, BaseActivity.class);
        intent.putExtra("menu", Define.MENU_MATERIAL_PICKING);
        Bundle extras = new Bundle();
        extras.putSerializable("model", mMaterialOutDetailModel);
        extras.putSerializable("position", position);
        extras.putSerializable("code", mWarehouseCode);
        intent.putExtra("args",extras);
        startActivityForResult(intent,100);
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
                case R.id.bt_next:
                    if(Utils.isEmpty(et_order.getText().toString())){
                        Utils.Toast(mContext, "불출지시서를 입력해주세요.");
                        return;
                    }
                    if(mAdapter.getItemCount() <= 0){
                        Utils.Toast(mContext, "불출할 자재를 입력해주세요.");
                        return;
                    }
                    mTwoBtnPopup = new TwoBtnPopup(getActivity(), et_order.getText().toString()+"의 자재불출을 전송하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                requestMaterialSend();
                                mTwoBtnPopup.hideDialog();
                            }
                        }
                    });
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
                                        MaterialOutListModel.Items item = (MaterialOutListModel.Items) msg.obj;
                                        et_order.setText(item.getOut_slip_no());
                                        tv_warehouse.setText(item.getWh_name_out());
                                        tv_input.setText(item.getWh_name_in());
                                        mOutMeterialListPopup.hideDialog();

                                        mWarehouseCode = item.getWh_code_out();
                                        requestOrderDetail(item.getOut_slip_no());
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
                    mMaterialOutDetailModel = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (mMaterialOutDetailModel != null) {
                        if(mMaterialOutDetailModel.getFlag() == ResultModel.SUCCESS) {
                            if(mMaterialOutDetailModel.getItems().size() > 0) {
                                //장고코드 입력
                                mWarehouseCode = mMaterialOutDetailModel.getItems().get(0).getWh_code_out();
                                tv_warehouse.setText(mMaterialOutDetailModel.getItems().get(0).getWh_name_out());
                                tv_input.setText(mMaterialOutDetailModel.getItems().get(0).getWh_name_in());
                                mAdapter.setData(mMaterialOutDetailModel.getItems());
                                mAdapter.notifyDataSetChanged();

                                tv_empty.setVisibility(View.GONE);
                                recycleview.setVisibility(View.VISIBLE);
                            }
                        }else{
                            Utils.Toast(mContext, mMaterialOutDetailModel.getMSG());
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

    /**
     * 자재불출 전송
     */
    private void requestMaterialSend() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        JsonObject json = new JsonObject();
        //로그인ID
        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");

        json.addProperty("p_out_slip_no", et_order.getText().toString());
        json.addProperty("p_user_id", userID);

        List<MaterialOutDetailModel.Items> items = mMaterialOutDetailModel.getItems();
        JsonArray list = new JsonArray();
        int count = 0;
        if(items != null && items.size() > 0) {
            for (MaterialOutDetailModel.Items item : items) {
                JsonObject obj = new JsonObject();
                List<MaterialLocAndLotModel.Items> itemList = item.getItems();
                if (itemList != null && itemList.size() > 0) {
                    for (MaterialLocAndLotModel.Items material : itemList) {
                        //시스템로트번호
                        obj.addProperty("lot_no", material.getLot_no());
                        //로케이션코드
                        obj.addProperty("location_code", material.getLocation_code());
                        //불출수량
                        obj.addProperty("out_qty", material.getInput_qty());
                        //자재불출품목순번
                        obj.addProperty("out_no2", item.getOut_no2());
                        list.add(obj);
                        count += material.getInput_qty();
                    }
                }
            }
        }
        if(count <= 0){
            Toast.makeText(mContext,"불출할 자재수량이 없습니다.",Toast.LENGTH_SHORT).show();
            return;
        }
        json.add("detail", list);

        Utils.Log("new Gson().toJson(json) ==> : "+new Gson().toJson(json));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(json));

        Call<ResultModel> call = service.postMaterialSend(body);

        call.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                if(response.isSuccessful()){
                    ResultModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if(model.getFlag() == ResultModel.SUCCESS) {
                            mOneBtnPopup = new OneBtnPopup(getActivity(), et_order.getText().toString()+"의 자재불출 처리가 완료되었습니다.", R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        getActivity().finish();
                                        mOneBtnPopup.hideDialog();
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
            public void onFailure(Call<ResultModel> call, Throwable t) {
                Utils.Log(t.getMessage());
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }
}
