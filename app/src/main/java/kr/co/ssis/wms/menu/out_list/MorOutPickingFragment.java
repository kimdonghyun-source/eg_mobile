package kr.co.ssis.wms.menu.out_list;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.honeywell.aidc.BarcodeReadEvent;

import java.util.ArrayList;
import java.util.List;


import kr.co.siss.wms.R;
import kr.co.ssis.wms.common.Define;
import kr.co.ssis.wms.common.SharedData;
import kr.co.ssis.wms.common.Utils;
import kr.co.ssis.wms.custom.CommonFragment;
import kr.co.ssis.wms.honeywell.AidcReader;
import kr.co.ssis.wms.menu.main.BaseActivity;
import kr.co.ssis.wms.menu.popup.LocationEmpPopup;
import kr.co.ssis.wms.menu.popup.LocationListPopup;
import kr.co.ssis.wms.menu.popup.OneBtnPopup;
import kr.co.ssis.wms.menu.popup.TwoBtnPopup;
import kr.co.ssis.wms.model.EmpModel;
import kr.co.ssis.wms.model.MorEmpModel;
import kr.co.ssis.wms.model.MorSerialScan;
import kr.co.ssis.wms.model.ResultModel;
import kr.co.ssis.wms.model.WarehouseModel;
import kr.co.ssis.wms.network.ApiClientService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MorOutPickingFragment extends CommonFragment {

    TextView tv_h_model, tv_h_color, tv_h_loft, tv_h_direc, tv_h_headcode, tv_h_weight, tv_s_model, tv_s_color, tv_s_loft, tv_s_direc, tv_s_sharftcode, tv_s_weight, picking_qty;
    TextView tv_scount, tv_s_all_count, tv_hcount, tv_h_all_count;
    ImageButton btn_next, bt_emp, bt_wh;
    EditText et_qty, et_emp, et_wh;
    ScrollView scrollView;
    List<WarehouseModel.Items> mWarehouseList;
    List<MorEmpModel.Items> mMorempList;
    List<MorSerialScan.Items> mSerialList;
    WarehouseModel.Items WareLocation;
    MorSerialScan.Items MorSerialLocation;
    MorEmpModel.Items MorEmpLocation;
    EmpModel.Items mEmpList;
    MorSerialScan mSerial;

    Spinner spinner_wh, spinner_emp;
    int mSpinnerSelect = 0;
    ListAdapter mAdapter;
    ScanListAdapter sAdapter;
    Dialog dialog;
    Spinner mSpinner;
    LocationListPopup mLocationListPopup;
    LocationEmpPopup mLocationEmpPopup;

    ListView mlistView;
    String mOrderNo, mor_no, wh_code, corp_code, mordate, h_qty, s_qty, m_type, m_gubun;
    int b_h_qty = 0, b_s_qty = 0;
    MorSerialScan morSerialScan;
    List<String> mBarcode;
    OneBtnPopup mOneBtnPopup;
    TwoBtnPopup mTwoBtnPopup;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        mBarcode = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_mor_out, container, false);

        tv_h_model = v.findViewById(R.id.tv_h_model);
        tv_h_color = v.findViewById(R.id.tv_h_color);
        tv_h_loft = v.findViewById(R.id.tv_h_loft);
        tv_h_direc = v.findViewById(R.id.tv_h_direc);
        tv_h_headcode = v.findViewById(R.id.tv_h_headcode);
        tv_h_weight = v.findViewById(R.id.tv_h_weight);

        tv_s_model = v.findViewById(R.id.tv_s_model);
        tv_s_color = v.findViewById(R.id.tv_s_color);
        tv_s_loft = v.findViewById(R.id.tv_s_loft);

        tv_hcount = v.findViewById(R.id.tv_hcount);
        tv_h_all_count = v.findViewById(R.id.tv_h_all_count);
        tv_scount = v.findViewById(R.id.tv_scount);
        tv_s_all_count = v.findViewById(R.id.tv_s_all_count);

        picking_qty = v.findViewById(R.id.picking_qty);
        //btn_wh_ok = v.findViewById(R.id.btn_wh_ok);

        btn_next = v.findViewById(R.id.btn_next);
        bt_emp = v.findViewById(R.id.bt_emp);
        bt_wh = v.findViewById(R.id.bt_wh);
        mSpinner = v.findViewById(R.id.spinner);
        et_emp = v.findViewById(R.id.et_emp);
        et_wh = v.findViewById(R.id.et_wh);
        mlistView = v.findViewById(R.id.ListView);
        scrollView = v.findViewById(R.id.scrollView);

        mAdapter = new ListAdapter();
        sAdapter = new ScanListAdapter();

        btn_next.setOnClickListener(onClickListener);
        bt_emp.setOnClickListener(onClickListener);
        bt_wh.setOnClickListener(onClickListener);

        Bundle args = getArguments();
        if (args != null) {
            final String CORPCODE = args.getString("CORPCODE");             //사업장번호
            final String PRODUCT = args.getString("PRODUCT");               //품목정보
            final String QTY = args.getString("QTY");                       //주문수량
            final String H_COUNT = args.getString("H_COUNT");               //헤드수량
            final String S_COUNT = args.getString("S_COUNT");               //샤프트수량
            final String mor_date = args.getString("mor_date");             //주문자재출고일자
            final String mor_no1 = args.getString("mor_no1");               //주문자재출고순번
            final String mor_h_qty = args.getString("mor_h_qty");           //헤드요청수량
            final String mor_s_qty = args.getString("mor_s_qty");           //샤프트요청수량
            final String TYPE = args.getString("TYPE");                     //회원/대리점 구분
            final String GUBUN = args.getString("GUBUN");                   //전표타입

            //헤드
            final String HAEDCODE = args.getString("HEADCODE");                     //헤드코드
            final String HAEDNAME = args.getString("HAEDNAME");                     //헤드명
            final String HEADCOLOR_C = args.getString("HEADCOLOR_C");               //헤드색상코드
            final String HEADCOLOR_NM = args.getString("HEADCOLOR_NM");             //헤드색상명
            final String HEADLOFT_C = args.getString("HEADLOFT_C");                 //헤드각도코드
            final String HEADLOFT_NM = args.getString("HEADLOFT_NM");               //헤드각도명
            final String HEADDIREC_C = args.getString("HEADDIREC_C");               //헤드방향코드
            final String HEADDIREC_NM = args.getString("HEADDIREC_NM");             //헤드방향명
            final String HEADHEAD_C = args.getString("HEADHEAD_C");                 //헤드헤드코드
            final String HEADHEAD_NM = args.getString("HEADHEAD_NM");               //헤드헤드명
            final String HEADWEIGHT_C = args.getString("HEADWEIGHT_C");             //헤드무게코드
            final String HEADWEIGHT_NM = args.getString("HEADWEIGHT_NM");           //헤드무게명

            //샤프트
            final String SHAFTCODE = args.getString("SHAFTCODE");                   //샤프트코드
            final String SHAFTNAME = args.getString("SHAFTNAME");                   //샤프트명
            final String SHAFTCOLOR_C = args.getString("SHAFTCOLOR_C");             //샤프트색상코드
            final String SHAFTCOLOR_NM = args.getString("SHAFTCOLOR_NM");           //샤프트색상명
            final String SHAFTSTRONG_C = args.getString("SHAFTSTRONG_C");           //샤프트각도코드
            final String SHAFTSTRONG_NM = args.getString("SHAFTSTRONG_NM");         //샤프트각도명

            tv_h_model.setText(HAEDNAME);
            tv_h_color.setText(HEADCOLOR_NM);
            tv_h_loft.setText(HEADLOFT_NM);
            tv_h_direc.setText(HEADDIREC_NM);
            tv_h_headcode.setText(HEADHEAD_NM);
            tv_h_weight.setText(HEADWEIGHT_NM);

            tv_s_model.setText(SHAFTNAME);
            tv_s_color.setText(SHAFTCOLOR_NM);
            tv_s_loft.setText(SHAFTSTRONG_NM);

            tv_h_all_count.setText(mor_h_qty);
            tv_s_all_count.setText(mor_s_qty);

            mor_no = mor_no1;           //출고순번
            mordate = mor_date;         //출고일자
            corp_code = CORPCODE;       //사업장
            h_qty = mor_h_qty;          //헤드요청수량
            s_qty = mor_s_qty;          //샤프트요청수량
            m_type = TYPE;              //회원/대리점 구분
            m_gubun = GUBUN;            //전표타입 구분 O=주문, A=AS

        }
        mlistView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        requestWhlist_setting();
        //et_wh.setText("[" + WareLocation.getWh_code() + "] " + WareLocation.getWh_name());


        return v;
    }//onCreateView Close

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
                    mOrderNo = barcode;

                    if (mBarcode.contains(barcode)) {
                        Utils.Toast(mContext, "동일한 SerialNo를 스캔하셨습니다.");
                        return;
                    }

                    if (et_wh.getText().toString() == null) {
                        Utils.Toast(mContext, getString(R.string.error_location_move));
                        return;
                    }else if(mEmpList == null){
                        Utils.Toast(mContext, getString(R.string.error_job_emp));
                    }
                    else {
                        requestMorPickingScan();
                    }

                }
            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
        AidcReader.getInstance().release();
        AidcReader.getInstance().setListenerHandler(null);
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_next: {
                    if (sAdapter.getCount() <= 0) {
                        Utils.Toast(mContext, getString(R.string.error_location_scan));
                        return;
                    }
                    if (h_qty != null) {
                        if (!h_qty.equals(String.valueOf(b_h_qty))) {
                            Utils.Toast(mContext, getString(R.string.error_h_qty));
                            return;
                        }
                    }

                    if (s_qty != null) {
                        if (!s_qty.equals(String.valueOf(b_s_qty))) {
                            Utils.Toast(mContext, getString(R.string.error_s_qty));
                            return;
                        }
                    }

                    mTwoBtnPopup = new TwoBtnPopup(getActivity(), "출고처리 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                requestWareSave();
                                mTwoBtnPopup.hideDialog();
                            }
                        }
                    });

                    break;
                }
                case R.id.bt_emp: {
                    requestEmplist();
                    break;
                }
                case R.id.bt_wh: {
                    requestWhlist();
                    break;
                }
            }
        }
    };

    /**
     * 창고검색 리스트 기본값
     */
    private void requestWhlist_setting() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<WarehouseModel> call = service.morWarehouse("sp_pda_mst_wh_list", "0800");

        call.enqueue(new Callback<WarehouseModel>() {
            @Override
            public void onResponse(Call<WarehouseModel> call, Response<WarehouseModel> response) {
                if (response.isSuccessful()) {
                    WarehouseModel model = response.body();
                    //Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mWarehouseList = model.getItems();
                            et_wh.setText("[" + mWarehouseList.get(0).getWh_code() + "] " + mWarehouseList.get(0).getWh_name());
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
            public void onFailure(Call<WarehouseModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }

    /**
     * 작업자 리스트
     */
    private void requestEmplist() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<EmpModel> call = service.empList("usp_GetCodeInfo_Table", "EMPPITING_GBN");
        //Call<WarehouseModel> call = service.morWarehouse("sp_pda_mst_wh_list", "");

        call.enqueue(new Callback<EmpModel>() {
            @Override
            public void onResponse(Call<EmpModel> call, Response<EmpModel> response) {
                if (response.isSuccessful()) {
                    EmpModel model = response.body();
                    //Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mLocationEmpPopup = new LocationEmpPopup(getActivity(), model.getItems(), R.drawable.popup_title_job_emp, new Handler() {
                                //mLocationListPopup = new LocationListPopup(getActivity(), model.getItems(), R.drawable.popup_title_searchloc, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        EmpModel.Items item = (EmpModel.Items) msg.obj;
//                                        mAdapter.notifyDataSetChanged();
                                        mEmpList = item;
                                        et_emp.setText("[" + mEmpList.getCode() + "] " + mEmpList.getName());

                                        mLocationEmpPopup.hideDialog();
                                    }

                                }
                            });

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
            public void onFailure(Call<EmpModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }


    /**
     * 입고처 리스트
     */
    private void requestWhlist() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<WarehouseModel> call = service.morWarehouse("sp_pda_mst_wh_list", "");

        call.enqueue(new Callback<WarehouseModel>() {
            @Override
            public void onResponse(Call<WarehouseModel> call, Response<WarehouseModel> response) {
                if (response.isSuccessful()) {
                    WarehouseModel model = response.body();
                    //Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mLocationListPopup = new LocationListPopup(getActivity(), model.getItems(), R.drawable.popup_title_searchloc, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    WarehouseModel.Items item = (WarehouseModel.Items) msg.obj;
                                    WareLocation = item;
                                    et_wh.setText("[" + WareLocation.getWh_code() + "] " + WareLocation.getWh_name());
                                    //mAdapter.notifyDataSetChanged();
                                    wh_code = WareLocation.getWh_code();
                                    mLocationListPopup.hideDialog();
                                }
                            });
                            mWarehouseList = model.getItems();


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
            public void onFailure(Call<WarehouseModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }

    /**
     * 시리얼번호 스캔
     */
    private void requestMorPickingScan() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<MorSerialScan> call = service.morSerialScan("sp_pda_dis_mor_serial_scan", mOrderNo, wh_code, corp_code, mordate, mor_no);

        call.enqueue(new Callback<MorSerialScan>() {
            @Override
            public void onResponse(Call<MorSerialScan> call, Response<MorSerialScan> response) {
                if (response.isSuccessful()) {
                    //morSerialScan
                    morSerialScan = response.body();
                    final MorSerialScan model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (morSerialScan != null) {
                        if (morSerialScan.getFlag() == ResultModel.SUCCESS) {
                            for (int i = 0; i < model.getItems().size(); i++) {
                                MorSerialScan.Items item = (MorSerialScan.Items) model.getItems().get(i);
                                sAdapter.addData(item);
                                if (item.getItm_id().equals("3")) {
                                    b_h_qty++;
                                    if (tv_hcount.getText().equals(tv_h_all_count.getText())) {
                                        Utils.Toast(mContext, "헤드 요청 수량을 초과하였습니다.");
                                        mSerialList.remove(i);
                                        b_h_qty--;
                                    } else {
                                        tv_hcount.setText(String.valueOf(b_h_qty));
                                    }
                                }
                                if (item.getItm_id().equals("4")) {
                                    b_s_qty++;
                                    if (tv_scount.getText().equals(tv_s_all_count.getText())) {
                                        Utils.Toast(mContext, "샤프트 요청 수량을 초과하였습니다.");
                                        mSerialList.remove(i);
                                        b_s_qty--;
                                    } else {
                                        tv_scount.setText(String.valueOf(b_s_qty));
                                    }
                                }
                            }

                            sAdapter.notifyDataSetChanged();
                            mBarcode.add(mOrderNo);
                            mlistView.setAdapter(sAdapter);


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
            public void onFailure(Call<MorSerialScan> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }

    /**
     * 주문자재출고 출고승인(주문)
     */
    private void requestMemberOrder() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        JsonObject json = new JsonObject();
        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");

        json.addProperty("p_corp_code", corp_code);
        json.addProperty("p_mor_date", mordate);
        json.addProperty("p_mor_no1", mor_no);
        json.addProperty("p_gbn", m_type);
        json.addProperty("p_user_id", userID);

        Utils.Log("new Gson().toJson(json) ==> : " + new Gson().toJson(json));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(json));

        Call<ResultModel> call = service.postSendWareAgree(body);

        call.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                if (response.isSuccessful()) {
                    ResultModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
            }
        });
    }

    /**
     * 주문자재출고 출고승인(AS)
     */
    private void requestMemberAS() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        JsonObject json = new JsonObject();
        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");

        json.addProperty("p_corp_code", corp_code);
        json.addProperty("p_mor_date", mordate);
        json.addProperty("p_mor_no1", mor_no);
        json.addProperty("p_gbn", m_type);
        json.addProperty("p_user_id", userID);

        Utils.Log("new Gson().toJson(json) ==> : " + new Gson().toJson(json));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(json));

        Call<ResultModel> call = service.postSendWareAS(body);

        call.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                if (response.isSuccessful()) {
                    ResultModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
            }
        });
    }

    /**
     * 주문자재출고 출고승인(대리점)
     */
    private void requestStore() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        JsonObject json = new JsonObject();
        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");

        json.addProperty("p_corp_code", corp_code);
        json.addProperty("p_mor_date", mordate);
        json.addProperty("p_mor_no1", mor_no);
        json.addProperty("p_gbn", m_type);
        json.addProperty("p_user_id", userID);

        Utils.Log("new Gson().toJson(json) ==> : " + new Gson().toJson(json));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(json));

        Call<ResultModel> call = service.postSendWareStore(body);

        call.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                if (response.isSuccessful()) {
                    ResultModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
            }
        });
    }

    /**
     * 주문자재출고 저장
     */
    private void requestWareSave() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        JsonObject json = new JsonObject();
        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");
        JsonArray list = new JsonArray();
        List<MorSerialScan.Items> items = sAdapter.getData();
        for (MorSerialScan.Items item : items) {
            JsonObject obj = new JsonObject();
            obj.addProperty("serial_no", item.getSerial_no());
            obj.addProperty("serial_qty", item.getSerial_qty());
            obj.addProperty("itm_code", item.getItm_code());
            obj.addProperty("wh_code_in", item.getWh_code_in());
            list.add(obj);
        }

        json.addProperty("p_corp_code", corp_code);
        json.addProperty("p_mor_date", mordate);
        json.addProperty("p_mor_no1", mor_no);
        json.addProperty("p_job_emp", mEmpList.getCode());
        json.addProperty("p_user_id", userID);
        json.add("detail", list);

        Utils.Log("new Gson().toJson(json) ==> : " + new Gson().toJson(json));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(json));

        Call<ResultModel> call = service.postSendWareSave(body);

        call.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                if (response.isSuccessful()) {
                    ResultModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mOneBtnPopup = new OneBtnPopup(getActivity(), "출고처리 되었습니다.", R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        getActivity().finish();
                                        mOneBtnPopup.hideDialog();
                                        Intent intent = new Intent(mContext, BaseActivity.class);
                                        intent.putExtra("menu", Define.MENU_PRODUCTION_IN);
                                        Bundle args=new Bundle();
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("args",args);
                                        startActivity(intent);

                                    }
                                }
                            });

                            if (m_type.equals("C") && m_gubun.equals("O")) {
                                requestMemberOrder();
                            } else if (m_type.equals("C") && m_gubun.equals("A")) {
                                requestMemberAS();
                            } else {
                                requestStore();
                            }

                        } else {
                            mOneBtnPopup = new OneBtnPopup(getActivity(), model.getMSG(), R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        mOneBtnPopup.hideDialog();

                                    }
                                }
                            });
                        }
                    }
                } else {
                    Utils.LogLine(response.message());
                    mTwoBtnPopup = new TwoBtnPopup(getActivity(), "이동 전송을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                requestWareSave();
                                mTwoBtnPopup.hideDialog();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResultModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                mTwoBtnPopup = new TwoBtnPopup(getActivity(), "이동 전송을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            requestWareSave();
                            mTwoBtnPopup.hideDialog();
                        }
                    }
                });
            }
        });

    }


    class ListAdapter extends BaseAdapter {
        LayoutInflater mInflater;

        public ListAdapter() {
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            if (mMorempList == null) {
                return 0;
            }

            return mMorempList.size();
        }

        @Override
        public MorEmpModel.Items getItem(int position) {
            return mMorempList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ListAdapter.ViewHolder holder;
            if (v == null) {
                holder = new ListAdapter.ViewHolder();
                v = mInflater.inflate(R.layout.cell_pop_location, null);
                v.setTag(holder);

                holder.tv_code = v.findViewById(R.id.tv_code);
                holder.tv_name = v.findViewById(R.id.tv_name);
            } else {
                holder = (ListAdapter.ViewHolder) v.getTag();
            }

            final MorEmpModel.Items item = mMorempList.get(position);
            holder.tv_code.setText(item.getCode());
            holder.tv_name.setText(item.getName());

            /*v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.obj = item;
                    mHandler.sendMessage(msg);
                }
            });*/

            return v;
        }

        class ViewHolder {
            TextView tv_code;
            TextView tv_name;
        }
    }

    class ScanListAdapter extends BaseAdapter {
        LayoutInflater mInflater;

        public ScanListAdapter() {
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            if (mSerialList == null) {
                return 0;
            }

            return mSerialList.size();
        }

        public List<MorSerialScan.Items> getData() {
            return mSerialList;
        }

        public void addData(MorSerialScan.Items item) {
            if (mSerialList == null) mSerialList = new ArrayList<>();
            mSerialList.add(item);
        }

        @Override
        public MorSerialScan.Items getItem(int position) {
            return mSerialList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            if (v == null) {
                holder = new ViewHolder();
                v = mInflater.inflate(R.layout.cell_mor_serial_scan, null);
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                holder.serialno = v.findViewById(R.id.tv_serialno);
                //holder.itmcode = v.findViewById(R.id.tv_name);
                holder.itmname = v.findViewById(R.id.tv_itmname);
                holder.serialqty = v.findViewById(R.id.tv_qty);
                //holder.whcodein = v.findViewById(R.id.tv_name);

                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            final MorSerialScan.Items item = mSerialList.get(position);
            holder.serialno.setText(item.getSerial_no());
            //holder.itmcode.setText(item.getItm_code());
            holder.itmname.setText(item.getItm_name());
            holder.serialqty.setText(Integer.toString(item.getSerial_qty()));
            //holder.whcodein.setText(item.getWh_code_in());

            /*v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.obj = item;
                    mHandler.sendMessage(msg);
                }
            });*/

            return v;
        }

        class ViewHolder {
            TextView serialno;
            TextView itmcode;
            TextView itmname;
            TextView serialqty;
            TextView whcodein;
        }
    }
}//Class close
