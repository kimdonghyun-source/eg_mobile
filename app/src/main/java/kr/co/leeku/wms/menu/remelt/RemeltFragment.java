package kr.co.leeku.wms.menu.remelt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.co.leeku.wms.R;
import kr.co.leeku.wms.common.SharedData;
import kr.co.leeku.wms.common.Utils;
import kr.co.leeku.wms.custom.CommonFragment;
import kr.co.leeku.wms.honeywell.AidcReader;
import kr.co.leeku.wms.menu.popup.OneBtnPopup;
import kr.co.leeku.wms.menu.popup.TwoBtnPopup;
import kr.co.leeku.wms.menu.ship.ShipAdapter;
import kr.co.leeku.wms.menu.ship.ShipFragment;
import kr.co.leeku.wms.model.OsrDetailModel;
import kr.co.leeku.wms.model.RemeltModel;
import kr.co.leeku.wms.model.ResultModel;
import kr.co.leeku.wms.model.ShipScanModel;
import kr.co.leeku.wms.network.ApiClientService;
import kr.co.leeku.wms.network.MyDatabaseHelper;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.WIFI_SERVICE;

public class RemeltFragment extends CommonFragment {

    Context mContext;
    TextView item_date;
    List<String> mIncode;
    private SoundPool sound_pool;
    int soundId;
    MediaPlayer mediaPlayer;
    DatePickerDialog.OnDateSetListener callbackMethod;
    EditText et_serial;
    ImageButton bt_serial, btn_next;
    RecyclerView remelt_listview;
    String barcodeScan, beg_barcode;
    RemeltModel mRemeltModel;
    List<RemeltModel.Item> mRemeltList;
    OneBtnPopup mOneBtnPopup;
    TwoBtnPopup mTwoBtnPopup;
    ListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mIncode = new ArrayList<>();


    }//Close onCreate


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_remelt, container, false);

        item_date = v.findViewById(R.id.item_date);
        et_serial = v.findViewById(R.id.et_serial);
        bt_serial = v.findViewById(R.id.bt_serial);
        remelt_listview = v.findViewById(R.id.remelt_listview);
        btn_next = v.findViewById(R.id.btn_next);

        item_date.setOnClickListener(onClickListener);
        bt_serial.setOnClickListener(onClickListener);
        btn_next.setOnClickListener(onClickListener);

        int year1 = Integer.parseInt(yearFormat.format(currentTime));
        int month1 = Integer.parseInt(monthFormat.format(currentTime));
        int day1 = Integer.parseInt(dayFormat.format(currentTime));

        remelt_listview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ListAdapter(getActivity());
        remelt_listview.setAdapter(mAdapter);

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


        sound_pool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundId = sound_pool.load(mContext, R.raw.beepum, 1);


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
            switch (view.getId()){
                case R.id.item_date:

                    int c_year = Integer.parseInt(item_date.getText().toString().substring(0, 4));
                    int c_month = Integer.parseInt(item_date.getText().toString().substring(5, 7));
                    int c_day = Integer.parseInt(item_date.getText().toString().substring(8, 10));

                    DatePickerDialog dialog = new DatePickerDialog(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, callbackMethod, c_year, c_month - 1, c_day);
                    dialog.show();
                    break;

                case R.id.bt_serial:
                    if (et_serial.getText().toString().equals("")){
                        Utils.Toast(mContext, "시리얼번호를 입력해주세요.");
                        break;
                    }else {
                        barcodeScan = et_serial.getText().toString();
                        if (barcodeScan.substring(0, 1).equals("*")) {
                            barcodeScan = et_serial.getText().toString().replace("*", "");
                        } else {
                            barcodeScan = et_serial.getText().toString();
                        }

                        if (mIncode != null) {
                            if (mIncode.contains(barcodeScan)) {
                                Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                                sound_pool.play(soundId, 1f, 1f, 0, 1, 1f);
                                mediaPlayer = MediaPlayer.create(mContext, R.raw.beepum);
                                mediaPlayer.start();
                                return;
                            }
                        }

                        if (beg_barcode != null) {
                            if (beg_barcode.equals(barcodeScan)) {
                                Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                                sound_pool.play(soundId, 1f, 1f, 0, 1, 1f);
                                mediaPlayer = MediaPlayer.create(mContext, R.raw.beepum);
                                mediaPlayer.start();
                                return;
                            }
                        }
                        Remelt_plan_List(barcodeScan);
                    }
                    break;

                case R.id.btn_next:
                    btn_next.setEnabled(false);
                    if (mAdapter.getItemCount() <= 0){
                        Utils.Toast(mContext, "등록할 제품이 없습니다.");
                        btn_next.setEnabled(true);
                        break;
                    }else {

                        mTwoBtnPopup = new TwoBtnPopup(getActivity(), "제품재용해 등록을 진행하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {
                                    request_remelt_save();
                                    mTwoBtnPopup.hideDialog();


                                }
                            }
                        });

                    }

                    break;

            }
        }
    };//Close onClick

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
                    if (barcodeScan.substring(0, 1).equals("*")) {
                        barcodeScan = barcode.replace("*", "");
                    } else {
                        barcodeScan = barcode;
                    }

                    if (mIncode != null) {
                        if (mIncode.contains(barcodeScan)) {
                            Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                            sound_pool.play(soundId, 1f, 1f, 0, 1, 1f);
                            mediaPlayer = MediaPlayer.create(mContext, R.raw.beepum);
                            mediaPlayer.start();
                            return;
                        }
                    }

                    if (beg_barcode != null) {
                        if (beg_barcode.equals(barcodeScan)) {
                            Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                            sound_pool.play(soundId, 1f, 1f, 0, 1, 1f);
                            mediaPlayer = MediaPlayer.create(mContext, R.raw.beepum);
                            mediaPlayer.start();
                            return;
                        }
                    }

                    Remelt_plan_List(barcodeScan);
                    et_serial.setText(barcodeScan);

                }
            }
        });

    }//Close onResume


    /**
     * 바코드확인
     */
    private void Remelt_plan_List(String bar) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        String m_date = item_date.getText().toString().replace("-", "");
        Call<RemeltModel> call = service.RemeltList("sp_api_remelt_plan_list", "BARCODE_CHECK", m_date, bar);

        call.enqueue(new Callback<RemeltModel>() {
            @Override
            public void onResponse(Call<RemeltModel> call, Response<RemeltModel> response) {
                if (response.isSuccessful()) {
                    mRemeltModel = response.body();
                    final RemeltModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mRemeltModel != null) {
                        if (mRemeltModel.getFlag() == ResultModel.SUCCESS || mRemeltModel.getFlag() == -2) {
                            if (mRemeltModel.getFlag() == -2) {
                                sound_pool.play(soundId, 1f, 1f, 0, 1, 1f);
                                mediaPlayer = MediaPlayer.create(mContext, R.raw.beepum);
                                mediaPlayer.start();
                                mOneBtnPopup = new OneBtnPopup(getActivity(), model.getMSG(), R.drawable.popup_title_alert, new Handler() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        if (msg.what == 1) {
                                            mOneBtnPopup.hideDialog();

                                        }
                                    }
                                });
                            }


                            if (model.getItems().size() > 0) {

                                /*if (mOsrDetailModel.getItems().get(0).getFlag() == -1){
                                    Utils.Toast(mContext, mOsrDetailModel.getItems().get(0).getMsg());
                                    return;
                                }*/


                                mRemeltList = model.getItems();
                                for (int i = 0; i < model.getItems().size(); i++) {

                                    RemeltModel.Item item = (RemeltModel.Item) model.getItems().get(i);
                                    mAdapter.addData(item);

                                }
                                mAdapter.notifyDataSetChanged();
                                remelt_listview.setAdapter(mAdapter);
                                mIncode.add(barcodeScan);
                            }

                        } else {
                            Utils.Toast(mContext, model.getMSG());
                            sound_pool.play(soundId, 1f, 1f, 0, 1, 1f);
                            mediaPlayer = MediaPlayer.create(mContext, R.raw.beepum);
                            mediaPlayer.start();

                        }
                    }
                } else {
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code() + " : " + response.message());
                }
            }


            @Override
            public void onFailure(Call<RemeltModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close barcode check

    /**
     * 제품재용해등록
     */
    private void request_remelt_save() {
        int cnt = 0;
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        JsonObject json = new JsonObject();
        //로그인 ID
        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");
        String fac_code = (String) SharedData.getSharedData(mContext, SharedData.UserValue.FAC_CODE.name(), "");
        String m_date = item_date.getText().toString().replace("-", "");
        //List<ShipListModel.ShipItem> items = (List<ShipListModel.ShipItem>) mShipModel.getItems();
        List<RemeltModel.Item> items = mAdapter.getData();
        StringBuffer buffer = new StringBuffer();
        for (RemeltModel.Item item : items) {
            cnt++;
            if (cnt == mAdapter.itemsList.size()){
                buffer.append(item.getLbl_id());
            }else{
                buffer.append(item.getLbl_id() + ";");
            }
        }

        JsonObject obj = new JsonObject();
        JsonArray list = new JsonArray();

        obj.addProperty("p_remelt_date", m_date);                           //일자
        obj.addProperty("p_fac_code", fac_code);                            //창고코드
        obj.addProperty("p_lbl_list", buffer.toString());                     //바코드번호
        obj.addProperty("p_user_id", userID);                               //유저아이디
        list.add(obj);
        json.add("detail", list);

        Utils.Log("new Gson().toJson(json) ==> : " + new Gson().toJson(json));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(json));

        Call<ResultModel> call = service.postRemeltSave(body);

        call.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                if (response.isSuccessful()) {
                    ResultModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {

                            mOneBtnPopup = new OneBtnPopup(getActivity(), "등록되었습니다.", R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        mOneBtnPopup.hideDialog();
                                        getActivity().finish();
                                        btn_next.setEnabled(true);
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

                    mTwoBtnPopup = new TwoBtnPopup(getActivity(), "등록을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                request_remelt_save();
                                mTwoBtnPopup.hideDialog();
                                btn_next.setEnabled(true);

                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResultModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                mTwoBtnPopup = new TwoBtnPopup(getActivity(), "등록을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            request_remelt_save();
                            mTwoBtnPopup.hideDialog();
                            btn_next.setEnabled(true);

                        }
                    }
                });
            }
        });

    }//Close bt_send




    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
        float c_cnt = 0;
        ShipScanModel.Item mModel;
        List<RemeltModel.Item> itemsList;
        Activity mActivity;
        Handler mHandler = null;


        public ListAdapter(Activity context) {
            mActivity = context;
        }

        public void setData(List<RemeltModel.Item> list) {
            itemsList = list;
        }


        public void clearData() {
            if (itemsList != null) itemsList.clear();
        }

        public void setRetHandler(Handler h) {
            this.mHandler = h;
        }

        public List<RemeltModel.Item> getData() {
            return itemsList;
        }

        public void addData(RemeltModel.Item item) {
            if (itemsList == null) itemsList = new ArrayList<>();
            itemsList.add(item);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int z) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_remelt_list, viewGroup, false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final RemeltModel.Item item = itemsList.get(position);


            holder.tv_fg_name.setText(item.getFg_name());
            //holder.tv_scan_qty.setText(Float.toString(item.getScan_qty()));
            holder.tv_lbl_id.setText(item.getLbl_id() + "          " + Float.toString(item.getScan_qty()));

            holder.bt_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (beg_barcode != null) {
                        if (beg_barcode.equals(mAdapter.itemsList.get(position).getLbl_id())) {
                            beg_barcode = "";
                        }

                    }
                    mIncode.remove(mAdapter.itemsList.get(position).getLbl_id());
                    itemsList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, itemsList.size());
                    mAdapter.notifyDataSetChanged();
                }
            });


        }

        @Override
        public int getItemCount() {
            return (null == itemsList ? 0 : itemsList.size());
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_fg_name;            //품목명
            //TextView tv_scan_qty;           //스캔수량
            TextView tv_lbl_id;             //바코드값
            ImageButton bt_delete;          //삭제버튼

            public ViewHolder(View view) {
                super(view);

                tv_fg_name = view.findViewById(R.id.tv_fg_name);
                //tv_scan_qty = view.findViewById(R.id.tv_scan_qty);
                tv_lbl_id = view.findViewById(R.id.tv_lbl_id);
                bt_delete = view.findViewById(R.id.bt_delete);


            }
        }
    }//Close Adapter



}//Close Fragment
