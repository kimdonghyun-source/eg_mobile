package kr.co.leeku.wms.menu.scrap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.leeku.wms.R;
import kr.co.leeku.wms.common.Define;
import kr.co.leeku.wms.common.SharedData;
import kr.co.leeku.wms.common.Utils;
import kr.co.leeku.wms.custom.CommonFragment;
import kr.co.leeku.wms.menu.main.BaseActivity;
import kr.co.leeku.wms.menu.popup.OneBtnPopup;
import kr.co.leeku.wms.menu.popup.TwoBtnPopup;
import kr.co.leeku.wms.menu.popup.TwoBtnShipPopup;
import kr.co.leeku.wms.model.LabelComboModel;
import kr.co.leeku.wms.model.RemeltModel;
import kr.co.leeku.wms.model.ResultModel;
import kr.co.leeku.wms.network.ApiClientService;
import kr.co.leeku.wms.spinner.SpinnerAdapter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LabelFragment extends CommonFragment {

    Context mContext;
    Spinner spinner_gbn, spinner_j_no, spinner_itm, spinner_do;
    EditText tv_weight, tv_location;
    Button bt_add, bt_cancel;
    int mSpinnerSelect = 0;
    LabelComboModel mComboModel;
    List<LabelComboModel.Item> mComboList;
    List<Map<String, Object>> spList;
    List<Map<String, Object>> spList1;
    List<Map<String, Object>> spList2;
    OneBtnPopup mOneBtnPopup;
    TwoBtnPopup mTwoBtnPopup;
    String cmp_id, cmp_rnk, dogum, cnt, weight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        spList = new ArrayList<>();
        spList1 = new ArrayList<>();
        spList2 = new ArrayList<>();

    }//Close onCreate


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_label, container, false);
        spinner_gbn = v.findViewById(R.id.spinner_gbn);
        spinner_j_no = v.findViewById(R.id.spinner_j_no);
        spinner_itm = v.findViewById(R.id.spinner_itm);
        tv_weight = v.findViewById(R.id.tv_weight);
        spinner_do = v.findViewById(R.id.spinner_do);
        tv_location = v.findViewById(R.id.tv_location);
        bt_add = v.findViewById(R.id.bt_add);
        bt_cancel = v.findViewById(R.id.bt_cancel);

        bt_cancel.setOnClickListener(onClickListener);
        bt_add.setOnClickListener(onClickListener);

        List<String> list = new ArrayList<String>();
        list.add("자동");
        list.add("수동");

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(mContext, list, spinner_gbn, 0);
        spinner_gbn.setAdapter(spinnerAdapter);
        spinner_gbn.setOnItemSelectedListener(onItemSelectedListener);
        spinner_gbn.setSelection(mSpinnerSelect);

        requestScale();
        requestFg_name();
        requestDogum();

        return v;

    }//Close onCreateView

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.bt_cancel:
                    getActivity().finish();
                    break;

                case R.id.bt_add:
                    mTwoBtnPopup = new TwoBtnPopup(getActivity(), "등록하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                bt_add.setEnabled(false);
                                request_label_save();
                            }
                        }
                    });


                    break;
            }
        }
    };

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //최초에 setOnItemSelectedListener 하면 이벤트가 들어오기 때문에
            //onResume에서 mSpinnerSelect에 현재 선택된 position을 넣고 여기서 비교
            if (mSpinnerSelect == position) return;

            mSpinnerSelect = position;


            if (mSpinnerSelect == 0){
                spinner_j_no.setVisibility(View.VISIBLE);
                requestScale();
            }else{
                spinner_j_no.setVisibility(View.GONE);
            }


        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /**
     * 저울번호
     */
    private void requestScale() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<LabelComboModel> call = service.LabelComboList("sp_api_scrap_combo", "J");

        call.enqueue(new Callback<LabelComboModel>() {
            @Override
            public void onResponse(Call<LabelComboModel> call, Response<LabelComboModel> response) {
                if(response.isSuccessful()){
                    LabelComboModel model = response.body();
                    List<String> list = new ArrayList<>();

                    for(LabelComboModel.Item itm : model.getItems()){
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", Integer.toString(itm.getScale_id()));
                        map.put("value", Float.toString(itm.getScale()));
                        spList.add(map);
                        //list.add(Float.toString(itm.getScale()));
                        list.add(Float.toString(itm.getScale_id()));
                    }

                    SpinnerAdapter spinnerAdapter = new SpinnerAdapter(mContext, list, spinner_j_no, 0);
                    spinner_j_no.setAdapter(spinnerAdapter);
                    //spinner_j_no.setOnItemSelectedListener(onItemSelectedListener);
                    spinner_j_no.setSelection(mSpinnerSelect);
                    cnt = spList.get(mSpinnerSelect).get("name").toString();
                    weight = spList.get(mSpinnerSelect).get("value").toString();
                    tv_weight.setText(spList.get(mSpinnerSelect).get("value").toString());


                }else{
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code()+" : "+response.message());
                }
            }

            @Override
            public void onFailure(Call<LabelComboModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 품명
     */
    private void requestFg_name() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<LabelComboModel> call = service.LabelComboList("sp_api_scrap_combo", "I");

        call.enqueue(new Callback<LabelComboModel>() {
            @Override
            public void onResponse(Call<LabelComboModel> call, Response<LabelComboModel> response) {
                if(response.isSuccessful()){
                    LabelComboModel model = response.body();
                    List<String> list = new ArrayList<>();

                    for(LabelComboModel.Item itm : model.getItems()){
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", itm.getFg_name());
                        map.put("id", itm.getCmp_id());
                        map.put("rnk", itm.getCmp_rnk());
                        spList1.add(map);
                        list.add(itm.getFg_name());
                    }

                    SpinnerAdapter spinnerAdapter = new SpinnerAdapter(mContext, list, spinner_itm, 0);
                    spinner_itm.setAdapter(spinnerAdapter);
                    //spinner_itm.setOnItemSelectedListener(onItemSelectedListener);
                    spinner_itm.setSelection(mSpinnerSelect);
                    cmp_id = spList1.get(mSpinnerSelect).get("id").toString();
                    cmp_rnk = spList1.get(mSpinnerSelect).get("rnk").toString();


                }else{
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code()+" : "+response.message());
                }
            }

            @Override
            public void onFailure(Call<LabelComboModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 도금
     */
    private void requestDogum() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<LabelComboModel> call = service.LabelComboList("sp_api_scrap_combo", "D");

        call.enqueue(new Callback<LabelComboModel>() {
            @Override
            public void onResponse(Call<LabelComboModel> call, Response<LabelComboModel> response) {
                if(response.isSuccessful()){
                    LabelComboModel model = response.body();
                    List<String> list = new ArrayList<>();

                    for(LabelComboModel.Item itm : model.getItems()){
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", itm.getDogum_nm());
                        map.put("code", itm.getDogum());
                        spList2.add(map);
                        list.add(itm.getDogum_nm());
                    }

                    SpinnerAdapter spinnerAdapter = new SpinnerAdapter(mContext, list, spinner_do, 0);
                    spinner_do.setAdapter(spinnerAdapter);
                    //spinner_itm.setOnItemSelectedListener(onItemSelectedListener);
                    spinner_do.setSelection(mSpinnerSelect);
                    dogum = spList2.get(mSpinnerSelect).get("code").toString();


                }else{
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code()+" : "+response.message());
                }
            }

            @Override
            public void onFailure(Call<LabelComboModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 라벨등록
     */
    private void request_label_save() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        JsonObject json = new JsonObject();
        //로그인 ID
        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");

        StringBuffer buffer = new StringBuffer();


        JsonObject obj = new JsonObject();
        JsonArray list = new JsonArray();

        obj.addProperty("cmp_id", cmp_id);                                //전ERP품목코드
        obj.addProperty("cmp_rnk", cmp_rnk);                              //황동구분
        obj.addProperty("dogum", dogum);                                  //도금코드
        obj.addProperty("cnt", tv_weight.getText().toString());           //중량
        obj.addProperty("location", tv_location.getText().toString());    //위치
        obj.addProperty("p_user_id", userID);                             //로그인자
        list.add(obj);
        json.add("detail", list);

        Utils.Log("new Gson().toJson(json) ==> : " + new Gson().toJson(json));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(json));

        Call<ResultModel> call = service.postLabelSave(body);

        call.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                if (response.isSuccessful()) {
                    ResultModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            bt_add.setEnabled(true);
                            mOneBtnPopup = new OneBtnPopup(getActivity(), "등록되었습니다.", R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        mOneBtnPopup.hideDialog();
                                        getActivity().finish();

                                    }
                                }
                            });

                        } else {
                            bt_add.setEnabled(true);
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
                    bt_add.setEnabled(true);
                    mTwoBtnPopup = new TwoBtnPopup(getActivity(), "등록을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                request_label_save();
                                mTwoBtnPopup.hideDialog();


                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResultModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                bt_add.setEnabled(true);
                mTwoBtnPopup = new TwoBtnPopup(getActivity(), "등록을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            request_label_save();
                            mTwoBtnPopup.hideDialog();


                        }
                    }
                });
            }
        });

    }//Close bt_send



}//Close Fragment