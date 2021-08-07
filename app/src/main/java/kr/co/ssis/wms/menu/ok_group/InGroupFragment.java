package kr.co.ssis.wms.menu.ok_group;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.honeywell.aidc.BarcodeReadEvent;

import java.util.List;

import kr.co.siss.wms.R;
import kr.co.ssis.wms.common.Utils;
import kr.co.ssis.wms.custom.CommonFragment;
import kr.co.ssis.wms.honeywell.AidcReader;
import kr.co.ssis.wms.model.InGroupModel;
import kr.co.ssis.wms.model.InLotModel;
import kr.co.ssis.wms.model.ResultModel;
import kr.co.ssis.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InGroupFragment extends CommonFragment {

    Context mContext;
    TextView tv_bor_code, tv_wh_code, tv_itm_code, tv_itm_name, tv_itm_size, tv_itm_unit, tv_qty;
    EditText et_bor_date, et_tin_date, et_qc_fin_date, et_from, et_in_date, et_in_join_dt, et_ok_in_date, et_product_date, et_product_join_dt, et_product_ok_date;
    InGroupModel mGroupModel;
    List<InGroupModel.Item> mGroupListModel;
    String barcodeScan, beg_barcode;
    LinearLayout ok_linear, ex_in_linear, product_ok_linear;
    ImageButton btn_next;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();


    }//Close onCreate


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_group, container, false);

        ok_linear = v.findViewById(R.id.ok_linear);
        ex_in_linear = v.findViewById(R.id.ex_in_linear);
        product_ok_linear = v.findViewById(R.id.product_ok_linear);
        tv_bor_code = v.findViewById(R.id.tv_bor_code);
        tv_wh_code = v.findViewById(R.id.tv_wh_code);
        tv_itm_code = v.findViewById(R.id.tv_itm_code);
        tv_itm_name = v.findViewById(R.id.tv_itm_name);
        tv_itm_size = v.findViewById(R.id.tv_itm_size);
        tv_itm_unit = v.findViewById(R.id.tv_itm_unit);
        tv_qty = v.findViewById(R.id.tv_qty);
        et_bor_date = v.findViewById(R.id.et_bor_date);
        et_tin_date = v.findViewById(R.id.et_tin_date);
        et_qc_fin_date = v.findViewById(R.id.et_qc_fin_date);
        et_in_date = v.findViewById(R.id.et_in_date);
        et_in_join_dt = v.findViewById(R.id.et_in_join_dt);
        et_ok_in_date = v.findViewById(R.id.et_ok_in_date);
        et_product_date = v.findViewById(R.id.et_product_date);
        et_product_join_dt = v.findViewById(R.id.et_product_join_dt);
        et_product_ok_date = v.findViewById(R.id.et_product_ok_date);
        et_from = v.findViewById(R.id.et_from);
        btn_next = v.findViewById(R.id.btn_next);

        btn_next.setOnClickListener(onClickListener);

        return v;

    }//Close onCreateView

    @Override
    public void onResume() {
        super.onResume();
        AidcReader.getInstance().claim(mContext);
        AidcReader.getInstance().setListenerHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {

                    BarcodeReadEvent event = (BarcodeReadEvent) msg.obj;
                    String barcode = event.getBarcodeData();
                    barcodeScan = barcode;
                    et_from.setText(barcodeScan);

                    if (beg_barcode != null) {
                        if (beg_barcode.equals(barcodeScan)) {
                            Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                            return;
                        }
                    }


                    pdaSerialScan();
                    beg_barcode = barcodeScan;
                }
            }
        });

    }//Close onResume

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_next:
                    getActivity().finish();
                    break;
            }
        }
    };


    /**
     * 자재입고확인(GROUP)
     */
    private void pdaSerialScan() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<InGroupModel> call = service.InGroupSerialScan("sp_pda_tin_list", barcodeScan);

        call.enqueue(new Callback<InGroupModel>() {
            @Override
            public void onResponse(Call<InGroupModel> call, Response<InGroupModel> response) {
                if (response.isSuccessful()) {
                    mGroupModel = response.body();
                    final InGroupModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mGroupModel != null) {
                        if (mGroupModel.getFlag() == ResultModel.SUCCESS) {
                            if (model.getItems().size() > 0) {
                                //tv_bor_code, tv_wh_code, tv_itm_code, tv_itm_size, tv_itm_unit, tv_qty;
                                tv_bor_code.setText(mGroupModel.getItems().get(0).getBor_code());
                                tv_wh_code.setText(mGroupModel.getItems().get(0).getWh_code());
                                tv_itm_code.setText(mGroupModel.getItems().get(0).getItm_code());
                                tv_itm_name.setText(mGroupModel.getItems().get(0).getItm_name());
                                tv_itm_size.setText(mGroupModel.getItems().get(0).getItm_size());
                                tv_itm_unit.setText(mGroupModel.getItems().get(0).getItm_unit());
                                tv_qty.setText(Integer.toString(mGroupModel.getItems().get(0).getTin_qty()));

                                if (mGroupModel.getItems().get(0).getBor_status().equals("1")){

                                    ok_linear.setVisibility(View.VISIBLE);
                                    et_bor_date.setVisibility(View.VISIBLE);
                                    et_tin_date.setVisibility(View.VISIBLE);
                                    et_qc_fin_date.setVisibility(View.VISIBLE);

                                    ex_in_linear.setVisibility(View.GONE);
                                    et_in_date.setVisibility(View.GONE);
                                    et_in_join_dt.setVisibility(View.GONE);
                                    et_ok_in_date.setVisibility(View.GONE);

                                    product_ok_linear.setVisibility(View.GONE);
                                    et_product_date.setVisibility(View.GONE);
                                    et_product_join_dt.setVisibility(View.GONE);
                                    et_product_ok_date.setVisibility(View.GONE);

                                    et_bor_date.setText(mGroupModel.getItems().get(0).getBor_date());
                                    et_tin_date.setText(mGroupModel.getItems().get(0).getTin_date());
                                    et_qc_fin_date.setText(mGroupModel.getItems().get(0).getQc_fin_date());

                                }else if (mGroupModel.getItems().get(0).getBor_status().equals("2")){

                                    ok_linear.setVisibility(View.GONE);
                                    et_bor_date.setVisibility(View.GONE);
                                    et_tin_date.setVisibility(View.GONE);
                                    et_qc_fin_date.setVisibility(View.GONE);

                                    ex_in_linear.setVisibility(View.VISIBLE);
                                    et_in_date.setVisibility(View.VISIBLE);
                                    et_in_join_dt.setVisibility(View.VISIBLE);
                                    et_ok_in_date.setVisibility(View.VISIBLE);

                                    product_ok_linear.setVisibility(View.GONE);
                                    et_product_date.setVisibility(View.GONE);
                                    et_product_join_dt.setVisibility(View.GONE);
                                    et_product_ok_date.setVisibility(View.GONE);

                                    et_in_date.setText(mGroupModel.getItems().get(0).getBor_date());
                                    et_in_join_dt.setText(mGroupModel.getItems().get(0).getTin_date());
                                    et_ok_in_date.setText(mGroupModel.getItems().get(0).getQc_fin_date());

                                }else if (mGroupModel.getItems().get(0).getBor_status().equals("3")){

                                    ok_linear.setVisibility(View.GONE);
                                    et_bor_date.setVisibility(View.GONE);
                                    et_tin_date.setVisibility(View.GONE);
                                    et_qc_fin_date.setVisibility(View.GONE);

                                    ex_in_linear.setVisibility(View.GONE);
                                    et_in_date.setVisibility(View.GONE);
                                    et_in_join_dt.setVisibility(View.GONE);
                                    et_ok_in_date.setVisibility(View.GONE);

                                    product_ok_linear.setVisibility(View.VISIBLE);
                                    et_product_date.setVisibility(View.VISIBLE);
                                    et_product_join_dt.setVisibility(View.VISIBLE);
                                    et_product_ok_date.setVisibility(View.VISIBLE);

                                    et_product_date.setText(mGroupModel.getItems().get(0).getBor_date());
                                    et_product_join_dt.setText(mGroupModel.getItems().get(0).getTin_date());
                                    et_product_ok_date.setText(mGroupModel.getItems().get(0).getQc_fin_date());
                                }
                            }

                        } else {
                            Utils.Toast(mContext, model.getMSG());
                        }
                    }
                } else {
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code() + " : " + response.message());
                }
            }


            @Override
            public void onFailure(Call<InGroupModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close




}