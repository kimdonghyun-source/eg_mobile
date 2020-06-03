package kr.co.ajcc.wms.menu.pallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.honeywell.aidc.BarcodeReadEvent;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.common.Define;
import kr.co.ajcc.wms.common.SharedData;
import kr.co.ajcc.wms.common.Utils;
import kr.co.ajcc.wms.custom.CommonFragment;
import kr.co.ajcc.wms.honeywell.AidcReader;
import kr.co.ajcc.wms.menu.main.BaseActivity;
import kr.co.ajcc.wms.menu.popup.TwoBtnPopup;
import kr.co.ajcc.wms.model.DeliveryOrderModel;
import kr.co.ajcc.wms.model.PalletSnanModel;
import kr.co.ajcc.wms.model.ResultModel;
import kr.co.ajcc.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PalletFragment extends CommonFragment {
    Context mContext;
    ImageButton ib_bunhal;
    ImageButton ib_merge;
    ImageView iv_gd;
    TwoBtnPopup mPopup = null;
    LinearLayout ll_pallet_bunhal = null;
    LinearLayout ll_pallet_merge = null;
    TextView tv_bunhal_product = null;
    TextView tv_bunhal_count = null;
    EditText et_bunhal  =   null;
    EditText et_bunhal_count  =   null;

    ImageButton btn_next = null;

    EditText et_merge_1  =   null;
    EditText et_merge_2  =   null;

    EditText et_merge_count_1  =   null;
    EditText et_merge_count_2  =   null;

    TextView tv_merge_product_1 = null;
    TextView tv_merge_count_1   = null;
    TextView tv_merge_product_2 = null;
    TextView tv_merge_count_2   = null;

    PalletSnanModel.Items bunhalItem = null;
    PalletSnanModel.Items mergeItem1 = null;
    PalletSnanModel.Items mergeItem2 = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_pallet, container, false);

        ib_bunhal       = v.findViewById(R.id.ib_bunhal);
        ib_merge        = v.findViewById(R.id.ib_merge);
        iv_gd           = v.findViewById(R.id.iv_gd);
        ll_pallet_bunhal= v.findViewById(R.id.ll_pallet_bunhal);
        ll_pallet_merge = v.findViewById(R.id.ll_pallet_merge);
        tv_bunhal_product= v.findViewById(R.id.tv_bunhal_product);
        tv_bunhal_count = v.findViewById(R.id.tv_bunhal_count);
        et_bunhal= v.findViewById(R.id.et_bunhal);
        et_bunhal_count= v.findViewById(R.id.et_bunhal_count);
        btn_next        = v.findViewById(R.id.btn_next);

        et_merge_1  =   v.findViewById(R.id.et_merge_1);
        et_merge_2  =   v.findViewById(R.id.et_merge_2);

        et_merge_count_1  =   v.findViewById(R.id.et_merge_count_1);
        et_merge_count_2  =   v.findViewById(R.id.et_merge_count_2);

        tv_merge_product_1  =   v.findViewById(R.id.tv_merge_product_1);
        tv_merge_count_1  =   v.findViewById(R.id.tv_merge_count_1);
        tv_merge_product_2  =   v.findViewById(R.id.tv_merge_product_2);
        tv_merge_count_2  =   v.findViewById(R.id.tv_merge_count_2);


        ib_bunhal.setOnClickListener(onClickListener);
        ib_merge.setOnClickListener(onClickListener);
        btn_next.setOnClickListener(onClickListener);

        v.findViewById(R.id.bt_scan_test).setOnClickListener(onClickListener);

        v.findViewById(R.id.ib_delete_1).setOnClickListener(onClickListener);
        v.findViewById(R.id.ib_delete_2).setOnClickListener(onClickListener);

        String printer = (String) SharedData.getSharedData(mContext, "printer_info","");
        String arr[] = printer.split(" ");
        if(arr!=null && arr.length>=2) {

        } else {
            mPopup = new TwoBtnPopup(getActivity(), "프린터가 설정되지 않았습니다. 확인을 누르시면 설정화면으로 이동합니다.", R.drawable.popup_title_alert, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        goConfig();
                    } else {
                        getActivity().finish();
                    }
                }
            });
        }
        setTab(1);
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
                    requestBunhalSnScan(barcode);
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

    private void setTab(int idx){
        if(idx == 1){
            ib_bunhal.setSelected(true);
            ib_merge.setSelected(false);
            iv_gd.setBackgroundResource(R.drawable.pallet_separate_guidetext1);
            ll_pallet_bunhal.setVisibility(View.VISIBLE);
            ll_pallet_merge.setVisibility(View.GONE);
            btn_next.setImageResource(R.drawable.pallet_bt_separate);
        } else if(idx == 2){
            ib_bunhal.setSelected(false);
            ib_merge.setSelected(true);
            iv_gd.setBackgroundResource(R.drawable.pallet_merge_guidetext);
            ll_pallet_merge.setVisibility(View.VISIBLE);
            ll_pallet_bunhal.setVisibility(View.GONE);
            btn_next.setImageResource(R.drawable.pallet_bottom_bt_merge);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.ib_bunhal: {
                    setTab(1);
                    break;
                }
                case R.id.ib_merge: {
                    setTab(2);
                    break;
                }
                case R.id.bt_scan_test: {
                    //1,2,3,6,7
                    requestBunhalSnScan("20200325-000001");
                    break;
                }
                case R.id.btn_next: {
                    makeBarcode();
                    break;
                }
                case R.id.ib_delete_1: {
                    mergeItem1 = null;
                    tv_merge_product_1.setText("");
                    tv_merge_product_1.setSelected(false);
                    tv_merge_count_1.setText("");
                    et_merge_count_1.setText("");
                    et_merge_1.setText("");
                    break;
                }
                case R.id.ib_delete_2: {
                    mergeItem2 = null;
                    tv_merge_product_2.setText("");
                    tv_merge_product_2.setSelected(false);
                    tv_merge_count_2.setText("");
                    et_merge_count_2.setText("");
                    et_merge_2.setText("");
                    break;
                }
            }
        }
    };

    private void makeBarcode(){
        if(ib_bunhal.isSelected()){
            if(bunhalItem!=null) {
                mPopup = new TwoBtnPopup(getActivity(), et_bunhal.getText().toString()+" PALLET를 분할 처리 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {

                            mPopup.hideDialog();
                        }
                    }
                });
            } else {
                Toast.makeText(mContext,"PALLET SN을 스캔하세요.",Toast.LENGTH_SHORT).show();
            }
        } else if(ib_merge.isSelected()){
            if(mergeItem1!=null && mergeItem2!=null) {
                String str1 = et_merge_1.getText().toString();
                String str2 = et_merge_2.getText().toString();
                mPopup = new TwoBtnPopup(getActivity(), str1+"과 "+str2 +" PALLET를 병합 처리하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {

                            mPopup.hideDialog();
                        }
                    }
                });
            } else {
                Toast.makeText(mContext,"PALLET SN을 스캔하세요.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void goConfig(){
        Intent intent = new Intent(mContext, BaseActivity.class);
        intent.putExtra("menu", Define.MENU_CONFIG);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    /**
     * 출고지시서 상세
     */
    private void requestBunhalSnScan(final String param) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<PalletSnanModel> call = service.postScanPallet("sp_pda_plt_serial_scan", param);

        call.enqueue(new Callback<PalletSnanModel>() {
            @Override
            public void onResponse(Call<PalletSnanModel> call, Response<PalletSnanModel> response) {
                if(response.isSuccessful()){

                    PalletSnanModel model = response.body();
                    if (model != null) {
                        if(model.getFlag() == ResultModel.SUCCESS) {
                            if(model.getItems()!=null && model.getItems().size() > 0){
                                if(ib_bunhal.isSelected()) {
                                    bunhalItem = model.getItems().get(0);
                                    tv_bunhal_product.setText(bunhalItem.getItm_name());
                                    tv_bunhal_product.setSelected(true);
                                    tv_bunhal_count.setText(Float.toString(bunhalItem.getWrk_inv_qty()));
                                    et_bunhal_count.setText("0");
                                    et_bunhal.setText(param);
                                } else if(ib_merge.isSelected()){
                                    String merge = et_merge_1.getText().toString();
                                    if(Utils.isEmpty(merge)){
                                        mergeItem1 = model.getItems().get(0);
                                        tv_merge_product_1.setText(mergeItem1.getItm_name());
                                        tv_merge_product_1.setSelected(true);
                                        tv_merge_count_1.setText(Float.toString(mergeItem1.getWrk_inv_qty()));
                                        et_merge_count_1.setText("0");
                                        et_merge_1.setText(param);
                                        mergeItem2 = null;
                                    } else {
                                        mergeItem2 = model.getItems().get(0);
                                        tv_merge_product_2.setText(mergeItem2.getItm_name());
                                        tv_merge_product_2.setSelected(true);
                                        tv_merge_count_2.setText(Float.toString(mergeItem2.getWrk_inv_qty()));
                                        et_merge_count_2.setText("0");
                                        et_merge_2.setText(param);
                                    }
                                }
                            } else {
                                Utils.Toast(mContext, "품목이 없습니다.");
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
            public void onFailure(Call<PalletSnanModel> call, Throwable t) {
                Utils.Log(t.getMessage());
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }
}
