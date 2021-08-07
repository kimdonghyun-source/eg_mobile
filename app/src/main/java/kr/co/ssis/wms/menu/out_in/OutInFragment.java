package kr.co.ssis.wms.menu.out_in;

import android.app.Activity;
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
import android.widget.Toast;

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
import kr.co.ssis.wms.menu.popup.OneBtnPopup;
import kr.co.ssis.wms.menu.popup.TwoBtnPopup;
import kr.co.ssis.wms.model.OutInModel;
import kr.co.ssis.wms.model.ResultModel;
import kr.co.ssis.wms.network.ApiClientService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutInFragment extends CommonFragment {

    Context mContext;
    RecyclerView outin_listview;
    List<OutInModel.Item> outListModel;
    OutInModel outModel;
    OutInAdapter mAdapter;
    String barcodeScan, beg_barcode = null;
    TextView tv_bor_code, tv_itm_name, tv_itm_size, tv_c_name, tv_tin_qty;
    EditText et_from;
    TwoBtnPopup mPopup;
    Activity mActivity;
    OneBtnPopup mOneBtnPopup;
    TwoBtnPopup mTwoBtnPopup;
    ImageButton btn_next;
    TextView item_date;
    DatePickerDialog.OnDateSetListener callbackMethod;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();


    }//Close onCreate


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_out_in, container, false);

        outin_listview = v.findViewById(R.id.outin_listview);
        tv_tin_qty = v.findViewById(R.id.tv_tin_qty);
        tv_bor_code = v.findViewById(R.id.tv_bor_code);
        tv_itm_name = v.findViewById(R.id.tv_itm_name);
        tv_itm_size = v.findViewById(R.id.tv_itm_size);
        tv_c_name = v.findViewById(R.id.tv_c_name);
        et_from = v.findViewById(R.id.et_from);
        btn_next = v.findViewById(R.id.btn_next);
        item_date = v.findViewById(R.id.item_date);

        outin_listview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new OutInAdapter(getActivity());
        outin_listview.setAdapter(mAdapter);

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

        /*mAdapter.setRetHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what >= 0) {
                    Log.d("성공", String.valueOf(msg.what));
                    //pdaSerialRefresh(msg.what);
                }
            }
        });*/

        return v;

    }//Close onCreateView

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
                case R.id.btn_next:
                    if (outModel == null){
                        Utils.Toast(mContext, "입고할 품목을 스캔해주세요.");
                        return;
                    }else {
                        request_mat_out_save();
                    }
                    break;

                case R.id.item_date:
                    int c_year = Integer.parseInt(item_date.getText().toString().substring(0, 4));
                    int c_month = Integer.parseInt(item_date.getText().toString().substring(5, 7));
                    int c_day = Integer.parseInt(item_date.getText().toString().substring(8, 10));

                    DatePickerDialog dialog = new DatePickerDialog(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, callbackMethod, c_year, c_month - 1, c_day);
                    dialog.show();
                    break;

            }
        }
    };


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

                    if (outListModel != null) {
                        for (int i = 0; i < mAdapter.getItemCount(); i++) {
                            if (outListModel.get(i).getLot_no().equals(barcodeScan)) {
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


    /**
     * 외주품가입고 바코드스캔
     */
    private void pdaSerialScan() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<OutInModel> call = service.outinSerialScan("sp_pda_scm_list", barcodeScan);

        call.enqueue(new Callback<OutInModel>() {
            @Override
            public void onResponse(Call<OutInModel> call, Response<OutInModel> response) {
                if (response.isSuccessful()) {
                    outModel = response.body();
                    final OutInModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (outModel != null) {
                        if (outModel.getFlag() == ResultModel.SUCCESS) {
                            if (model.getItems().size() > 0) {

                                /*if (outListModel != null) {
                                    for (int i = 0; i < outListModel.size(); i++) {
                                        if (!outListModel.get(i).getBor_date().equals(outModel.getItems().get(0).getBor_date()) ||
                                                !outListModel.get(i).getBor_no1().equals(outModel.getItems().get(0).getBor_no1()) || !outModel.getItems().get(0).getBor_no2().equals(outListModel.get(i).getBor_no2())) {
                                            Utils.Toast(mContext, "품목이 다릅니다.");

                                            return;
                                        }
                                    }


                                }*/

                                for (int i = 0; i < model.getItems().size(); i++) {

                                    OutInModel.Item item = (OutInModel.Item) model.getItems().get(i);
                                    mAdapter.addData(item);
                                    tv_itm_name.setText(item.getItm_name());
                                    tv_itm_size.setText(item.getItm_size());
                                    tv_c_name.setText(item.getC_name());
                                    tv_tin_qty.setText(Integer.toString(item.getTin_qty()));
                                    tv_bor_code.setText(item.getBor_code());

                                }
                                //outListModel = model.getItems();
                                mAdapter.notifyDataSetChanged();
                                outin_listview.setAdapter(mAdapter);
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
            public void onFailure(Call<OutInModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 외주품가입고 리프레시
     */
    private void pdaSerialRefresh(final int position) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        Log.d("성공포지션", String.valueOf(position));

        Call<OutInModel> call = service.outinSerialScan("sp_pda_scm_list", outModel.getItems().get(position).getLot_no());

        call.enqueue(new Callback<OutInModel>() {
            @Override
            public void onResponse(Call<OutInModel> call, Response<OutInModel> response) {
                if (response.isSuccessful()) {

                    if (outModel != null) {
                        if (outModel.getFlag() == ResultModel.SUCCESS) {
                            Log.d("성공", "ㅇㅇㅇ");
                            tv_itm_name.setText("");
                            tv_itm_size.setText("");
                            tv_c_name.setText("");
                            tv_tin_qty.setText("");
                            tv_bor_code.setText("");
                            et_from.setText("");

                            /*for (int i = 0; i < outListModel.size(); i++) {
                                //OutInModel.Item item = (OutInModel.Item) model.getItems().get(position);
                                Log.d("성공position", String.valueOf(i));
                                Log.d("성공itmname", outModel.getItems().get(i).getItm_name());
                                tv_itm_name.setText(outModel.getItems().get(position).getItm_name());
                                tv_itm_size.setText(outModel.getItems().get(position).getItm_size());
                                tv_c_name.setText(outModel.getItems().get(position).getC_name());
                                tv_tin_qty.setText(Integer.toString(outModel.getItems().get(position).getTin_qty()));
                                tv_bor_code.setText(outModel.getItems().get(position).getBor_code());
                                et_from.setText(outModel.getItems().get(position).getLot_no());
                            }
                            mAdapter.notifyDataSetChanged();*/

                        } else {
                            //Utils.Toast(mContext, model.getMSG());
                        }
                    }
                } else {
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code() + " : " + response.message());
                }
            }


            @Override
            public void onFailure(Call<OutInModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close


    /**
     * 가입고 등록
     */
    private void request_mat_out_save() {

        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        JsonObject json = new JsonObject();
        String emp_code = (String) SharedData.getSharedData(mContext, "emp_code", "");
        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");
        String m_date = item_date.getText().toString().replace("-", "");

        JsonArray list = new JsonArray();

        //List<MatOutSerialScanModel.Item> items = scanAdapter.getData();
        List<OutInModel.Item> items = mAdapter.getData();

        for (OutInModel.Item item : items) {
            JsonObject obj = new JsonObject();
            obj.addProperty("itm_code", item.getItm_code());
            obj.addProperty("lot_no", item.getLot_no());
            obj.addProperty("lot_qty", item.getTin_dtl_qty());
            list.add(obj);
        }


        json.addProperty("p_corp_code", outModel.getItems().get(0).getCorp_code());    //사업장코드
        json.addProperty("p_scm_id", outModel.getItems().get(0).getTin_id());       //내수구분
        json.addProperty("p_scm_date", outModel.getItems().get(0).getTin_date());   //출하일자
        json.addProperty("p_scm_no1", outModel.getItems().get(0).getTin_no1());     //출하순번1
        json.addProperty("p_scm_no2", outModel.getItems().get(0).getTin_no2());     //출하순번2
        json.addProperty("p_scm_no3", outModel.getItems().get(0).getTin_no3());     //출하순번3
        json.addProperty("p_make_date", m_date);    //입고일자
        json.addProperty("p_user_id", userID);      //로그인ID
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
                                        btn_next.setEnabled(true);
                                    }
                                }
                            });
                        }
                    }
                } else {
                    Utils.LogLine(response.message());
                    btn_next.setEnabled(true);
                    mTwoBtnPopup = new TwoBtnPopup(getActivity(), "이동 전송을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                request_mat_out_save();
                                mTwoBtnPopup.hideDialog();

                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResultModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                btn_next.setEnabled(true);
                mTwoBtnPopup = new TwoBtnPopup(getActivity(), "이동 전송을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            request_mat_out_save();
                            mTwoBtnPopup.hideDialog();

                        }
                    }
                });
            }
        });

    }//Close


}//Close Activity
