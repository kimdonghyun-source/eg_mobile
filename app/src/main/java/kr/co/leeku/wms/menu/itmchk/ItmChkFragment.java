package kr.co.leeku.wms.menu.itmchk;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.honeywell.aidc.BarcodeReadEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kr.co.leeku.wms.R;
import kr.co.leeku.wms.common.SharedData;
import kr.co.leeku.wms.common.Utils;
import kr.co.leeku.wms.custom.CommonFragment;
import kr.co.leeku.wms.honeywell.AidcReader;
import kr.co.leeku.wms.menu.popup.OneBtnPopup;
import kr.co.leeku.wms.menu.popup.TwoBtnPopup;
import kr.co.leeku.wms.model.ItmChkBarcodeModel;
import kr.co.leeku.wms.model.ItmChkWhModel;
import kr.co.leeku.wms.model.RemeltModel;
import kr.co.leeku.wms.model.ResultModel;
import kr.co.leeku.wms.network.ApiClientService;
import kr.co.leeku.wms.spinner.SpinnerAdapter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItmChkFragment extends CommonFragment {

    Context mContext;
    DatePickerDialog.OnDateSetListener callbackMethod;
    TextView item_date;
    ListView itm_chk_listview;
    ImageButton btn_next, bt_serial;
    EditText et_serial;

    Spinner mSpinner;
    int mSpinnerSelect = 0;
    List<Map<String, Object>> spList;

    String barcodeScan, beg_barcode;
    ArrayList<String> mIncode;

    private SoundPool sound_pool;
    int soundId;
    MediaPlayer mediaPlayer;

    List<ItmChkBarcodeModel.Item> mItmChkList;
    ItmChkBarcodeModel mItmChkModel;

    //ListAdapter mAdapter;
    private ListViewAdapter adapter;

    private ArrayList<ItmListViewItem> listViewItemList = new ArrayList<ItmListViewItem>(); //리스트뷰ItmChkBarcodeModel
    private ArrayList<ItmListViewItem> filteredItemList = listViewItemList; //리스트뷰 임시저장소

    int no = 0;

    OneBtnPopup mOneBtnPopup;
    TwoBtnPopup mTwoBtnPopup;

    String spinnerSelect;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        spList = new ArrayList<>();
        mIncode = new ArrayList<>();


    }//Close onCreate


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_itm_chk, container, false);

        item_date = v.findViewById(R.id.item_date);
        itm_chk_listview = v.findViewById(R.id.itm_chk_listview);
        btn_next = v.findViewById(R.id.btn_next);
        mSpinner = v.findViewById(R.id.spinner);
        et_serial = v.findViewById(R.id.et_serial);
        bt_serial = v.findViewById(R.id.bt_serial);

        btn_next.setOnClickListener(onClickListener);
        item_date.setOnClickListener(onClickListener);
        bt_serial.setOnClickListener(onClickListener);


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

        String m_date = item_date.getText().toString().replace("-", "");
        requestWarehouse(m_date);

        sound_pool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundId = sound_pool.load(mContext, R.raw.beepum, 1);

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

                    if (beg_barcode != null) {
                        if (beg_barcode.equals(barcodeScan)) {
                            Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                            sound_pool.play(soundId, 1f, 1f, 0, 1, 1f);
                            mediaPlayer = MediaPlayer.create(mContext, R.raw.beepum);
                            mediaPlayer.start();
                            return;
                        }
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
                    no ++;
                    //ItmChkBarcodeScan();
                    adapter = new ListViewAdapter();
                    adapter.addItem(no + 1, barcodeScan);
                    //Collections.reverse(listViewItemList);
                    adapter.notifyDataSetChanged();
                    itm_chk_listview.setAdapter(adapter);
                    mIncode.add(barcodeScan);
                    beg_barcode = barcodeScan;

                    if (listViewItemList.size() >= 100){
                        AidcReader.getInstance().release(); //스캐너 죽이기
                        request_itm_chk_save();
                    }
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
                String m_date = item_date.getText().toString().replace("-", "");
                requestWarehouse(m_date);
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
                    btn_next.setEnabled(false);
                        if (listViewItemList.size() <= 0){
                            Utils.Toast(mContext, "데이터가 없습니다.");
                            btn_next.setEnabled(true);
                            return;
                        }else {
                            request_itm_chk_save();
                        }

                        break;

                    case R.id.item_date:
                        int c_year = Integer.parseInt(item_date.getText().toString().substring(0, 4));
                        int c_month = Integer.parseInt(item_date.getText().toString().substring(5, 7));
                        int c_day = Integer.parseInt(item_date.getText().toString().substring(8, 10));

                        DatePickerDialog dialog = new DatePickerDialog(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, callbackMethod, c_year, c_month - 1, c_day);
                        dialog.show();
                        //requestWarehouse();
                        break;

                    case R.id.bt_serial:

                        if (et_serial.getText().toString().equals("")){
                            Utils.Toast(mContext, "값을 입력해주세요.");
                            return;
                        }

                        barcodeScan = et_serial.getText().toString();

                        if (beg_barcode != null) {
                            if (beg_barcode.equals(barcodeScan)) {
                                Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                                sound_pool.play(soundId, 1f, 1f, 0, 1, 1f);
                                mediaPlayer = MediaPlayer.create(mContext, R.raw.beepum);
                                mediaPlayer.start();
                                return;
                            }
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
                        no ++;
                        //ItmChkBarcodeScan();
                        adapter = new ListViewAdapter();
                        adapter.addItem(no + 1, barcodeScan);
                        //Collections.reverse(listViewItemList);
                        adapter.notifyDataSetChanged();
                        itm_chk_listview.setAdapter(adapter);
                        mIncode.add(barcodeScan);
                        beg_barcode = barcodeScan;
                        et_serial.setText("");

                        if (listViewItemList.size() >= 100){
                            AidcReader.getInstance().release(); //스캐너 죽이기
                            request_itm_chk_save();
                        }
                    break;
            }

        }
    };


    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mSpinnerSelect = mSpinner.getSelectedItemPosition();
            spinnerSelect = mSpinner.getSelectedItem().toString().substring(1, mSpinner.getSelectedItem().toString().indexOf("]"));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /**
     * 창고 검색
     */
    private void requestWarehouse(String date) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        String fac_code = (String) SharedData.getSharedData(mContext, SharedData.UserValue.FAC_CODE.name(), "");

        Call<ItmChkWhModel> call = service.ItmChkWhList("sp_api_stockopname_list", "WH_LIST", date, fac_code);

        call.enqueue(new Callback<ItmChkWhModel>() {
            @Override
            public void onResponse(Call<ItmChkWhModel> call, Response<ItmChkWhModel> response) {
                if (response.isSuccessful()) {
                    ItmChkWhModel model = response.body();
                    List<String> list = new ArrayList<>();
                    List<String> list1 = new ArrayList<>();

                    for (ItmChkWhModel.Item itm : model.getItems()) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", itm.getWh_name());
                        map.put("value", itm.getWh_code());
                        spList.add(map);
                        list.add("["+itm.getWh_code()+"] " + itm.getWh_name());
                        list1.add(itm.getWh_code());

                    }

                    SpinnerAdapter spinnerAdapter = new SpinnerAdapter(mContext, list, mSpinner, 0);
                    mSpinner.setAdapter(spinnerAdapter);
                    mSpinner.setOnItemSelectedListener(onItemSelectedListener);
                    mSpinner.setSelection(mSpinnerSelect);




                } else {
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code() + " : " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ItmChkWhModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }

    public class ListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listViewItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final Context context = parent.getContext();
            final ViewHolder holder;

            // "listview_item" Layout을 inflate하여 convertView 참조 획득.
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.cell_itmchk_list, parent, false);

                holder.list_no = (TextView) convertView.findViewById(R.id.tv_no);
                holder.barcode = (TextView) convertView.findViewById(R.id.tv_barcode);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.ref = position;

            // Data Set(filteredItemList)에서 position에 위치한 데이터 참조 획득
            final ItmListViewItem listViewItem = filteredItemList.get(position);

            holder.barcode.setText(listViewItem.getBarcode());
            holder.list_no.setText(Integer.toString(position+1));

            return convertView;

        }//getView Close

        public void addItem(int no, String barcode) {
            ItmListViewItem item = new ItmListViewItem();
            item.setNo(no);
            item.setBarcode(barcode);

            listViewItemList.add(item);
        }


    }//Close Adapter

    /**
     * 제품재용해등록
     */
    private void request_itm_chk_save() {
        int cnt = 0;
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        JsonObject json = new JsonObject();
        //로그인 ID
        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");
        String fac_code = (String) SharedData.getSharedData(mContext, SharedData.UserValue.FAC_CODE.name(), "");
        String m_date = item_date.getText().toString().replace("-", "");

        StringBuffer buffer = new StringBuffer();
        //for (RemeltModel.Item item : items) {
        for (int i = 0; i < listViewItemList.size(); i ++) {
            cnt++;
            if (cnt == listViewItemList.size()){
                buffer.append(listViewItemList.get(i).getBarcode());
            }else{
                buffer.append(listViewItemList.get(i).getBarcode() + ";");
            }
        }

        JsonObject obj = new JsonObject();
        JsonArray list = new JsonArray();

        obj.addProperty("p_st_date", m_date);                               //일자
        obj.addProperty("p_fac_code", fac_code);                            //창고코드
        obj.addProperty("p_wh_code", spinnerSelect);                             //선택한 창고코드
        obj.addProperty("p_lbl_list", buffer.toString());                   //바코드번호
        obj.addProperty("p_user_id", userID);                               //유저아이디
        list.add(obj);
        json.add("detail", list);

        Utils.Log("new Gson().toJson(json) ==> : " + new Gson().toJson(json));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(json));

        Call<ResultModel> call = service.postItmChkSave(body);

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
                                        listViewItemList.clear();
                                        adapter.notifyDataSetChanged();
                                        AidcReader.getInstance().claim(mContext);    //스캐너 다시 활성화
                                        //getActivity().finish();
                                        btn_next.setEnabled(true);
                                        mIncode.clear();
                                        beg_barcode = "";
                                        listViewItemList.clear();
                                    }
                                }
                            });

                        } else {
                            mOneBtnPopup = new OneBtnPopup(getActivity(), model.getMSG(), R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        mOneBtnPopup.hideDialog();
                                        AidcReader.getInstance().claim(mContext);    //스캐너 다시 활성화
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
                                request_itm_chk_save();
                                mTwoBtnPopup.hideDialog();
                                AidcReader.getInstance().claim(mContext);    //스캐너 다시 활성화
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
                            request_itm_chk_save();
                            mTwoBtnPopup.hideDialog();
                            AidcReader.getInstance().claim(mContext);    //스캐너 다시 활성화
                            btn_next.setEnabled(true);

                        }
                    }
                });
            }
        });

    }//Close





}//Close Fragment
