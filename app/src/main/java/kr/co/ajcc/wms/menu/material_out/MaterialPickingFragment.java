package kr.co.ajcc.wms.menu.material_out;

import android.app.Activity;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.common.Utils;
import kr.co.ajcc.wms.custom.CommonFragment;
import kr.co.ajcc.wms.honeywell.AidcReader;
import kr.co.ajcc.wms.menu.location.LocationAdapter;
import kr.co.ajcc.wms.menu.popup.OneBtnPopup;
import kr.co.ajcc.wms.model.MaterialLocAndLotModel;
import kr.co.ajcc.wms.model.MaterialOutDetailModel;
import kr.co.ajcc.wms.model.PalletSnanModel;
import kr.co.ajcc.wms.model.ResultModel;
import kr.co.ajcc.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaterialPickingFragment extends CommonFragment {
    MaterialOutDetailModel mMaterialOutDetailModel;
    MaterialOutDetailModel.Items mOrderModel;
    String mWarehouseCode;
    int mPosition;

    EditText et_cnt;

    //리스트가 없을때 보여지는 text
    TextView tv_empty;

    RecyclerView recycleview;
    MaterialPickingAdapter mAdapter;

    OneBtnPopup mOneBtnPopup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();

        if (getArguments() != null) {
            mMaterialOutDetailModel = (MaterialOutDetailModel) getArguments().getSerializable("model");
            mWarehouseCode = getArguments().getString("position");
            mPosition = getArguments().getInt("code");
            mOrderModel = mMaterialOutDetailModel.getItems().get(mPosition);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_material_picking, container, false);

        TextView tv_name = v.findViewById(R.id.tv_name);
        TextView tv_size = v.findViewById(R.id.tv_size);
        TextView tv_cnt = v.findViewById(R.id.tv_cnt);

        tv_name.setText(mOrderModel.getItm_name());
        tv_size.setText(mOrderModel.getItm_size());
        tv_cnt.setText(Utils.setComma(mOrderModel.getReq_qty()));

        v.findViewById(R.id.bt_next).setOnClickListener(onClickListener);

        //정제영 테스트
        v.findViewById(R.id.bt_picking).setOnClickListener(onClickListener);

        et_cnt = v.findViewById(R.id.et_cnt);
        tv_empty = v.findViewById(R.id.tv_empty);

        recycleview = v.findViewById(R.id.recycleview);
        recycleview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new MaterialPickingAdapter(getActivity());
        recycleview.setAdapter(mAdapter);

        mAdapter.setSumHandler(new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what == 1){
                    List<MaterialLocAndLotModel.Items> itms = mAdapter.getData();
                    float count = 0;
                    for(int i = 0 ; i < itms.size() ; i++){
                        MaterialLocAndLotModel.Items itm = itms.get(i);
                        count += itm.getInput_qty();
                    }
                    et_cnt.setText(Utils.setComma(count));
                    if(count > mOrderModel.getReq_qty()){
                        mOneBtnPopup = new OneBtnPopup(getActivity(), "재고를 초과하였습니다.", R.drawable.popup_title_alert, new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {
                                    String result = (String)msg.obj;
                                    Utils.Toast(mContext, result);
                                    mOneBtnPopup.hideDialog();
                                }
                            }
                        });
                    }
                }
            }
        });

        return v;
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
                    if(barcode.indexOf("-")>=0) {
                        requestLocAndLot(null,barcode);
                    } else {
                        requestLocAndLot(barcode,null);
                    }
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

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int view = v.getId();

            switch (view){
                case R.id.bt_next:
                    List<MaterialLocAndLotModel.Items> datas = new ArrayList<>();
                    List<MaterialLocAndLotModel.Items> itms = mAdapter.getData();
                    int count = 0;
                    for(int i = 0 ; i < itms.size() ; i++){
                        MaterialLocAndLotModel.Items itm = itms.get(i);
                        if(itm.getInput_qty() > 0){
                            datas.add(itm);
                            count += itm.getInput_qty();
                        }
                    }

                    mMaterialOutDetailModel.getItems().get(mPosition).setReq_qty(count);
                    mMaterialOutDetailModel.getItems().get(mPosition).setItems(datas);
                    Intent i = new Intent();
                    i.putExtra("model", mMaterialOutDetailModel);
                    getActivity().setResult(Activity.RESULT_OK, i);
                    getActivity().finish();
                    break;
                case R.id.bt_picking:
                    requestLocAndLot("160", "20200514-000002");
                    break;
            }
        }
    };

    /**
     * 로케이션 및 로트번호 스캔
     */
    private void requestLocAndLot(String loc, String lot) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<MaterialLocAndLotModel> call = service.postOutLocAndLot("sp_pda_out_loc_lot_scan", mWarehouseCode, loc, lot);

        call.enqueue(new Callback<MaterialLocAndLotModel>() {
            @Override
            public void onResponse(Call<MaterialLocAndLotModel> call, Response<MaterialLocAndLotModel> response) {
                if(response.isSuccessful()){
                    MaterialLocAndLotModel model = response.body();
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
            public void onFailure(Call<MaterialLocAndLotModel> call, Throwable t) {
                Utils.Log(t.getMessage());
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }
}
