package kr.co.ssis.wms.menu.ok_lot;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.honeywell.aidc.BarcodeReadEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.co.siss.wms.R;
import kr.co.ssis.wms.common.SharedData;
import kr.co.ssis.wms.common.Utils;
import kr.co.ssis.wms.custom.CommonFragment;
import kr.co.ssis.wms.honeywell.AidcReader;
import kr.co.ssis.wms.menu.out_in.OutInAdapter;
import kr.co.ssis.wms.menu.popup.OneBtnPopup;
import kr.co.ssis.wms.menu.popup.TwoBtnPopup;
import kr.co.ssis.wms.model.InLotModel;
import kr.co.ssis.wms.model.OutInModel;
import kr.co.ssis.wms.model.ResultModel;
import kr.co.ssis.wms.network.ApiClientService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InLotFragment extends CommonFragment {

    Context mContext;
    TextView item_date, tv_itm_code, tv_itm_name, tv_itm_size, tv_c_name, tv_no, tv_cst, tv_qty;
    DatePickerDialog.OnDateSetListener callbackMethod;
    String barcodeScan, beg_barcode;
    EditText et_from;
    RecyclerView lot_listView;
    InLotAdapter mAdapter;
    OneBtnPopup mOneBtnPopup;
    TwoBtnPopup mTwoBtnPopup;
    ImageButton btn_next;

    List<InLotModel.Item> mInLotListModel;
    InLotModel mInLotModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();


    }//Close onCreate


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_lot, container, false);

        tv_itm_code = v.findViewById(R.id.tv_itm_code);
        tv_itm_name = v.findViewById(R.id.tv_itm_name);
        tv_itm_size = v.findViewById(R.id.tv_itm_size);
        tv_c_name = v.findViewById(R.id.tv_c_name);
        tv_no = v.findViewById(R.id.tv_no);
        tv_cst = v.findViewById(R.id.tv_cst);
        tv_qty = v.findViewById(R.id.tv_qty);
        item_date = v.findViewById(R.id.item_date);
        et_from = v.findViewById(R.id.et_from);
        lot_listView = v.findViewById(R.id.lot_listView);
        btn_next = v.findViewById(R.id.btn_next);

        lot_listView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new InLotAdapter(getActivity());
        lot_listView.setAdapter(mAdapter);

        int year1 = Integer.parseInt(yearFormat.format(currentTime));
        int month1 = Integer.parseInt(monthFormat.format(currentTime));
        int day1 = Integer.parseInt(dayFormat.format(currentTime));

        String formattedMonth = "" + month1;
        String formattedDayOfMonth = "" + day1;
        if (month1 < 10) {

            formattedMonth = "0" + month1;
        }
        if (day1 < 10) {
            formattedDayOfMonth = "0" + day1;
        }

        item_date.setText(year1 + "-" + formattedMonth + "-" + formattedDayOfMonth);

        this.InitializeListener();

        btn_next.setOnClickListener(onClickListener);
        item_date.setOnClickListener(onClickListener);

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

                    if (mInLotListModel != null) {
                        for (int i = 0; i < mAdapter.getItemCount(); i++) {
                            if (mInLotListModel.get(i).getLot_no().equals(barcodeScan)) {
                                Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                                return;
                            }
                        }
                    }

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

    public void InitializeListener() {
        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");

                int month = monthOfYear + 1;
                String formattedMonth = "" + month;
                String formattedDayOfMonth = "" + dayOfMonth;

                if (month < 10) {

                    formattedMonth = "0" + month;
                }
                if (dayOfMonth < 10) {

                    formattedDayOfMonth = "0" + dayOfMonth;
                }

                item_date.setText(year + "-" + formattedMonth + "-" + formattedDayOfMonth);

            }
        };
    }

    Date currentTime = Calendar.getInstance().getTime();
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
    SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
    SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_date:
                    int c_year = Integer.parseInt(item_date.getText().toString().substring(0, 4));
                    int c_month = Integer.parseInt(item_date.getText().toString().substring(5, 7));
                    int c_day = Integer.parseInt(item_date.getText().toString().substring(8, 10));

                    DatePickerDialog dialog = new DatePickerDialog(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, callbackMethod, c_year, c_month - 1, c_day);
                    dialog.show();
                    break;

                case R.id.btn_next:
                    if (mInLotModel == null){
                        Utils.Toast(mContext, "입고할 품목을 스캔해주세요.");
                    }else {
                        request_in_lot_save();
                    }
            }

        }
    };

    /**
     * 자재입고확인(LOT)
     */
    private void pdaSerialScan() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<InLotModel> call = service.InLotSerialScan("sp_pda_scm_list2", barcodeScan);

        call.enqueue(new Callback<InLotModel>() {
            @Override
            public void onResponse(Call<InLotModel> call, Response<InLotModel> response) {
                if (response.isSuccessful()) {
                    mInLotModel = response.body();
                    final InLotModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mInLotModel != null) {
                        if (mInLotModel.getFlag() == ResultModel.SUCCESS) {
                            if (model.getItems().size() > 0) {

                                for (int i = 0; i < model.getItems().size(); i++) {

                                    InLotModel.Item item = (InLotModel.Item) model.getItems().get(i);
                                    mAdapter.addData(item);
                                    tv_no.setText(item.getBor_code());
                                    tv_cst.setText(item.getCst_name());
                                    tv_itm_code.setText(item.getItm_code());
                                    tv_itm_name.setText(item.getItm_name());
                                    tv_itm_size.setText(item.getItm_size());
                                    tv_c_name.setText(item.getC_name());
                                    tv_qty.setText(Integer.toString(item.getTin_qty()));


                                }
                                mInLotListModel = model.getItems();
                                mAdapter.notifyDataSetChanged();
                                lot_listView.setAdapter(mAdapter);
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
            public void onFailure(Call<InLotModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 자재입고(LOT)
     */
    private void request_in_lot_save() {

        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        JsonObject json = new JsonObject();

        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");
        String m_date = item_date.getText().toString().replace("-", "");
        JsonArray list = new JsonArray();

        List<InLotModel.Item> items = mAdapter.getData();

        for (InLotModel.Item item : items) {
            JsonObject obj = new JsonObject();
            obj.addProperty("itm_code", item.getItm_code());
            obj.addProperty("lot_no", item.getLot_no());
            obj.addProperty("lot_qty", item.getTin_dtl_qty());
            list.add(obj);
        }


        json.addProperty("p_corp_code", mInLotModel.getItems().get(0).getCorp_code());    //사업장코드
        json.addProperty("p_scm_id", mInLotModel.getItems().get(0).getTin_id());       //내수구분
        json.addProperty("p_scm_date", mInLotModel.getItems().get(0).getTin_date());   //출하일자
        json.addProperty("p_scm_no1", mInLotModel.getItems().get(0).getTin_no1());     //출하순번1
        json.addProperty("p_scm_no2", mInLotModel.getItems().get(0).getTin_no2());     //출하순번2
        json.addProperty("p_scm_no3", mInLotModel.getItems().get(0).getTin_no2());     //출하순번3
        json.addProperty("p_make_date", m_date);     //입고일자
        json.addProperty("p_user_id", userID);    //로그인ID
        json.add("detail", list);

        Utils.Log("new Gson().toJson(json) ==> : " + new Gson().toJson(json));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(json));

        Call<ResultModel> call = service.postOutInSave(body);

        call.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                if (response.isSuccessful()) {
                    ResultModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {

                            mOneBtnPopup = new OneBtnPopup(getActivity(), "이동처리 되었습니다.", R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        getActivity().finish();
                                        mOneBtnPopup.hideDialog();

                                    }
                                }
                            });


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
                                request_in_lot_save();
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
                            request_in_lot_save();
                            mTwoBtnPopup.hideDialog();

                        }
                    }
                });
            }
        });

    }//Close

}//Close Activity
