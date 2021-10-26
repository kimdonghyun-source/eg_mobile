package kr.co.leeku.wms.menu.ship;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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

import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.co.leeku.wms.R;
import kr.co.leeku.wms.common.Define;
import kr.co.leeku.wms.common.SharedData;
import kr.co.leeku.wms.common.Utils;
import kr.co.leeku.wms.custom.CommonFragment;
import kr.co.leeku.wms.honeywell.AidcReader;
import kr.co.leeku.wms.menu.main.BaseActivity;
import kr.co.leeku.wms.menu.popup.LocationShipCustomList;
import kr.co.leeku.wms.menu.popup.LocationShipWhList;
import kr.co.leeku.wms.menu.popup.OneBtnPopup;
import kr.co.leeku.wms.menu.popup.TwoBtnPopup;
import kr.co.leeku.wms.menu.popup.TwoBtnShipPopup;
import kr.co.leeku.wms.model.ResultModel;
import kr.co.leeku.wms.model.ShipCustomListModel;
import kr.co.leeku.wms.model.ShipListModel;
import kr.co.leeku.wms.model.ShipScanModel;
import kr.co.leeku.wms.model.ShipWhListModel;
import kr.co.leeku.wms.network.ApiClientService;
import kr.co.leeku.wms.network.DBHelper;
import kr.co.leeku.wms.network.MyDatabaseHelper;
import kr.co.leeku.wms.network.SQLiteControl;
import kr.co.leeku.wms.network.SQLiteHelper;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.WIFI_SERVICE;

public class ShipFragment extends CommonFragment {
    Context mContext;
    DatePickerDialog.OnDateSetListener callbackMethod;
    EditText et_plt_no, et_cst, et_wh;
    RecyclerView ship_listview, ship_scan_listview;
    TextView item_date, et_scan_qty;
    ImageButton bt_cst, bt_wh, btn_next;

    LocationShipWhList mLocationWhListPopup;
    List<ShipWhListModel.Item> mWhList;
    ShipWhListModel.Item mWhModel;

    LocationShipCustomList mLocationCustomListPopup;
    List<ShipCustomListModel.Item> mCustomList;
    ShipCustomListModel.Item mCustomModel;

    List<ShipListModel.Item> mShipList;
    ShipListModel mShipModel;

    List<ShipScanModel.Item> mShipScanList;
    ShipScanModel mShipScanModel;

    ShipAdapter mAdapter;
    ShipScanAdapter mScanAdapter;

    String wh_code, cst_code, barcodeScan = null, beg_barcode, cst_name, mac, deli_code;

    private SoundPool sound_pool;
    int soundId;
    MediaPlayer mediaPlayer;

    List<String> mIncode;
    OneBtnPopup mOneBtnPopup;
    TwoBtnPopup mTwoBtnPopup;
    TwoBtnShipPopup mTwoBtnShipPopup;

    List list = new ArrayList<>();

    MyDatabaseHelper myDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mIncode = new ArrayList<>();
        WifiManager mng = (WifiManager) mContext.getSystemService(WIFI_SERVICE);
        WifiInfo info = mng.getConnectionInfo();
        mac = info.getMacAddress();
        Log.d("MAC주소", mac);

    }//Close onCreate


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_ship, container, false);

        et_scan_qty = v.findViewById(R.id.et_scan_qty);
        et_plt_no = v.findViewById(R.id.et_plt_no);
        ship_listview = v.findViewById(R.id.ship_listview);
        ship_scan_listview = v.findViewById(R.id.ship_scan_listview);
        item_date = v.findViewById(R.id.item_date);
        bt_wh = v.findViewById(R.id.bt_wh);
        bt_cst = v.findViewById(R.id.bt_cst);
        et_wh = v.findViewById(R.id.et_wh);
        et_cst = v.findViewById(R.id.et_cst);
        btn_next = v.findViewById(R.id.btn_next);

        et_plt_no.setText("1");

        ship_listview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ShipAdapter(getActivity());
        ship_listview.setAdapter(mAdapter);

        ship_scan_listview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mScanAdapter = new ShipScanAdapter(getActivity());
        ship_scan_listview.setAdapter(mScanAdapter);


        myDB = new MyDatabaseHelper(mContext);

        item_date.setOnClickListener(onClickListener);
        bt_wh.setOnClickListener(onClickListener);
        bt_cst.setOnClickListener(onClickListener);
        btn_next.setOnClickListener(onClickListener);

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

        viewAll();

        this.InitializeListener();

        mAdapter.setRetHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what >= 0) {
                    ShipScanList(msg.what);
                }
            }
        });

        sound_pool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundId = sound_pool.load(mContext, R.raw.beepum, 1);

        return v;

    }//Close onCreateView

    //데이터베이스 추가하기
    public void AddData(int pos, String bar, String plt, float qty, String fg, String mac, int wg) {

        boolean isInserted = myDB.insertData(pos, bar, plt, qty, fg, mac, wg);

        /*boolean isInserted = myDB.insertData(1, barcodeScan, et_plt_no.getText().toString(), mShipScanModel.getItems().get(0).getScan_qty(),
                mShipModel.getItems().get(1).getFg_name(), "111");*/

        if (isInserted == true)
            Log.d("데이터추가여부?", "OK");
        else
            Log.d("데이터추가여부?", "NO");
    }


    // 데이터베이스 읽어오기
    public void viewAll() {
        Cursor res = myDB.getAllData();
        if (res.getCount() == 0) {
            Log.d("실패", "데이터를 찾을 수 없습니다.");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("s_pltno: " + res.getString(0) + ", ");
            buffer.append("s_barcode: " + res.getString(1) + ", ");
            buffer.append("s_scanqty: " + res.getString(2) + ", ");
            buffer.append("s_fgname: " + res.getString(3) + ", ");
            buffer.append("s_mac: " + res.getString(4) + ", ");
            buffer.append("s_position: " + res.getString(5) + ", ");
            buffer.append("s_wg: " + res.getString(6) + "");
        }
        Log.d("데이터", buffer.toString());

    }

    // 데이터베이스 삭제하기
    public void deleteDatas() {
        Integer deleteRows = myDB.deleteDatas();
        if (deleteRows > 0)
            //Toast.makeText(mContext,"데이터 삭제 성공",Toast.LENGTH_LONG ).show();
            Log.d("삭제여부", "OK!");
        else
            //Toast.makeText(mContext,"데이터 삭제 실패", Toast.LENGTH_LONG ).show();
            Log.d("삭제여부?", "NO!");
    }


    private void ShipScanList(int position) {
        List<ShipScanModel.Item> datas = new ArrayList<>();

        Intent intent = new Intent(getActivity(), BaseActivity.class);
        intent.putExtra("menu", Define.MENU_SHIP_OK);

        Bundle extras = new Bundle();
        extras.putSerializable("model", mShipModel);
        extras.putSerializable("model1", mShipScanModel);
        extras.putString("cst_code", cst_code);
        extras.putSerializable("position", position);
        intent.putExtra("args", extras);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                int cnt = 0;
                mShipModel = (ShipListModel) data.getSerializableExtra("model");
                mAdapter.setData(mShipModel.getItems());
                mAdapter.notifyDataSetChanged();
                Cursor res = myDB.getAllData();
                cnt = res.getCount();
                et_scan_qty.setText(Integer.toString(cnt) + " 건");

            }
        }
    }//Close onActivityResult


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

                    if (mIncode != null) {
                        if (mIncode.contains(barcode)) {
                            Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                            return;
                        }
                    }

                    if (wh_code == null) {
                        Utils.Toast(mContext, "출고처를 골라주세요.");
                        return;
                    }

                    if (cst_code == null) {
                        Utils.Toast(mContext, "거래처를 골라주세요.");
                        return;
                    }

                    if (mAdapter.getItemCount() < 0) {
                        Utils.Toast(mContext, "조회된 리스트가 없습니다.");
                        return;
                    }

                    if (et_plt_no.getText().toString().length() == 0) {
                        Utils.Toast(mContext, "PLTNo 를 입력해주세요.");
                        return;
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
            switch (view.getId()) {
                case R.id.item_date:

                    if (mAdapter.getItemCount() > 0) {
                        mTwoBtnPopup = new TwoBtnPopup(getActivity(), "작업 내용이 취소됩니다.\n 다시 조회하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {
                                    mTwoBtnPopup.hideDialog();
                                    mAdapter.clearData();
                                    mAdapter.notifyDataSetChanged();
                                    cst_code = null;
                                    cst_name = null;
                                    deli_code = null;
                                    //wh_code = null;
                                    //et_wh.setText("");
                                    et_cst.setText("");
                                    mIncode.clear();
                                    et_scan_qty.setText("");
                                    deleteDatas();

                                    int c_year = Integer.parseInt(item_date.getText().toString().substring(0, 4));
                                    int c_month = Integer.parseInt(item_date.getText().toString().substring(5, 7));
                                    int c_day = Integer.parseInt(item_date.getText().toString().substring(8, 10));

                                    DatePickerDialog dialog = new DatePickerDialog(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, callbackMethod, c_year, c_month - 1, c_day);
                                    dialog.show();
                                }
                            }
                        });
                    } else {

                        int c_year = Integer.parseInt(item_date.getText().toString().substring(0, 4));
                        int c_month = Integer.parseInt(item_date.getText().toString().substring(5, 7));
                        int c_day = Integer.parseInt(item_date.getText().toString().substring(8, 10));

                        DatePickerDialog dialog = new DatePickerDialog(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, callbackMethod, c_year, c_month - 1, c_day);
                        dialog.show();
                    }

                    break;

                case R.id.bt_wh:
                    if (mAdapter.getItemCount() > 0) {
                        mTwoBtnPopup = new TwoBtnPopup(getActivity(), "작업 내용이 취소됩니다.\n 다시 조회하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {
                                    mTwoBtnPopup.hideDialog();
                                    mAdapter.clearData();
                                    mAdapter.notifyDataSetChanged();
                                    cst_code = null;
                                    cst_name = null;
                                    deli_code = null;
                                    wh_code = null;
                                    et_wh.setText("");
                                    et_cst.setText("");
                                    mIncode.clear();
                                    et_scan_qty.setText("");
                                    deleteDatas();

                                    requestWhlist();
                                }
                            }
                        });
                    } else {
                        requestWhlist();
                    }


                    break;

                case R.id.bt_cst:
                    if (mAdapter.getItemCount() > 0) {
                        mTwoBtnPopup = new TwoBtnPopup(getActivity(), "작업 내용이 취소됩니다.\n 다시 조회하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {
                                    mTwoBtnPopup.hideDialog();
                                    mAdapter.clearData();
                                    mAdapter.notifyDataSetChanged();
                                    cst_code = null;
                                    cst_name = null;
                                    deli_code = null;
                                    //wh_code = null;
                                    //et_wh.setText("");
                                    et_cst.setText("");
                                    mIncode.clear();
                                    et_scan_qty.setText("");
                                    deleteDatas();
                                    requestCstlist();
                                }
                            }
                        });
                    }else {

                        if (wh_code == null) {
                            Utils.Toast(mContext, "출고처를 골라주세요");
                            return;
                        } else {
                            requestCstlist();
                        }
                    }
                    break;

                case R.id.btn_next:
                    final String m_date = item_date.getText().toString().replace("-", "");
                    mTwoBtnShipPopup = new TwoBtnShipPopup(getActivity(), "PLT의 중량을 변경 합니다.\n'아니오' 처리시 PLT중량은\n 30KG으로 자동 출하등록 됩니다. ", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                mTwoBtnShipPopup.hideDialog();
                                Intent intent = new Intent(getActivity(), BaseActivity.class);
                                intent.putExtra("menu", Define.MENU_SHIP_CHANGE);
                                Bundle extras = new Bundle();
                                extras.putString("cst_name", cst_name);
                                extras.putString("date", m_date);
                                extras.putString("cst_code", cst_code);
                                extras.putString("wh_code", wh_code);
                                intent.putExtra("args", extras);
                                startActivityForResult(intent, 100);

                            } else {
                                request_ship_save();
                            }
                        }
                    });
                    break;
            }

        }
    };

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


    /**
     * 창고리스트
     */
    private void requestWhlist() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        String m_date = item_date.getText().toString().replace("-", "");
        String fac_code = (String) SharedData.getSharedData(mContext, SharedData.UserValue.FAC_CODE.name(), "");
        Call<ShipWhListModel> call = service.WhList("sp_api_shipment_plan_list", "WH_LIST", fac_code, m_date);

        call.enqueue(new Callback<ShipWhListModel>() {
            @Override
            public void onResponse(Call<ShipWhListModel> call, Response<ShipWhListModel> response) {
                if (response.isSuccessful()) {
                    ShipWhListModel model = response.body();
                    //Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mLocationWhListPopup = new LocationShipWhList(getActivity(), model.getItems(), R.drawable.popup_title_whlist, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    ShipWhListModel.Item item = (ShipWhListModel.Item) msg.obj;
                                    mWhModel = item;
                                    et_wh.setText("[" + mWhModel.getWh_code() + "] " + mWhModel.getWh_name());
                                    //mAdapter.notifyDataSetChanged();
                                    wh_code = mWhModel.getWh_code();
                                    mLocationWhListPopup.hideDialog();


                                }
                            });
                            mWhList = model.getItems();


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
            public void onFailure(Call<ShipWhListModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 거래처리스트
     */
    private void requestCstlist() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        String m_date = item_date.getText().toString().replace("-", "");
        String fac_code = (String) SharedData.getSharedData(mContext, SharedData.UserValue.FAC_CODE.name(), "");
        Call<ShipCustomListModel> call = service.CustomList("sp_api_shipment_plan_list", "CUSTOM_LIST", fac_code, m_date, mWhModel.getWh_code());

        call.enqueue(new Callback<ShipCustomListModel>() {
            @Override
            public void onResponse(Call<ShipCustomListModel> call, Response<ShipCustomListModel> response) {
                if (response.isSuccessful()) {
                    ShipCustomListModel model = response.body();
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mLocationCustomListPopup = new LocationShipCustomList(getActivity(), model.getItems(), R.drawable.popup_title_cstlist, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    ShipCustomListModel.Item item = (ShipCustomListModel.Item) msg.obj;
                                    mCustomModel = item;
                                    if (!mCustomModel.getDeli_place().equals("")) {
                                        et_cst.setText("[" + mCustomModel.getCst_code() + "] " + mCustomModel.getCst_name() + " / " + mCustomModel.getDeli_place());
                                    }else{
                                        et_cst.setText("[" + mCustomModel.getCst_code() + "] " + mCustomModel.getCst_name());
                                    }
                                    //mAdapter.notifyDataSetChanged();
                                    cst_code = mCustomModel.getCst_code();
                                    cst_name = mCustomModel.getCst_name();
                                    deli_code = mCustomModel.getDeli_place();
                                    mLocationCustomListPopup.hideDialog();
                                    if (mAdapter != null) {
                                        mAdapter.clearData();
                                        mAdapter.notifyDataSetChanged();
                                        et_scan_qty.setText("");
                                    }
                                    ShipListSearch();

                                }
                            });
                            mCustomList = model.getItems();


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
            public void onFailure(Call<ShipCustomListModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 출하등록 조회
     */
    private void ShipListSearch() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        String fac_code = (String) SharedData.getSharedData(mContext, SharedData.UserValue.FAC_CODE.name(), "");
        String m_date = item_date.getText().toString().replace("-", "");
        Call<ShipListModel> call = service.ShipList("sp_api_shipment_plan_list", "FG_LIST", fac_code, m_date, wh_code, cst_code, deli_code);

        call.enqueue(new Callback<ShipListModel>() {
            @Override
            public void onResponse(Call<ShipListModel> call, Response<ShipListModel> response) {
                if (response.isSuccessful()) {
                    mShipModel = response.body();
                    final ShipListModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mShipModel != null) {
                        if (mShipModel.getFlag() == ResultModel.SUCCESS) {


                            if (model.getItems().size() > 0) {

                                mShipList = model.getItems();
                                for (int i = 0; i < model.getItems().size(); i++) {

                                    ShipListModel.Item item = (ShipListModel.Item) model.getItems().get(i);
                                    mAdapter.addData(item);

                                }
                                mAdapter.notifyDataSetChanged();
                                ship_listview.setAdapter(mAdapter);
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
            public void onFailure(Call<ShipListModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 출하등록 바코드스캔
     */
    private void pdaSerialScan() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        String fac_code = (String) SharedData.getSharedData(mContext, SharedData.UserValue.FAC_CODE.name(), "");
        String m_date = item_date.getText().toString().replace("-", "");
        Call<ShipScanModel> call = service.ShipBarcodeScan("sp_api_shipment_plan_list", "BARCODE_CHECK", fac_code, m_date, wh_code, cst_code, "", barcodeScan);

        call.enqueue(new Callback<ShipScanModel>() {
            @Override
            public void onResponse(Call<ShipScanModel> call, Response<ShipScanModel> response) {
                if (response.isSuccessful()) {
                    mShipScanModel = response.body();
                    final ShipScanModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mShipScanModel != null) {
                        if (mShipScanModel.getFlag() == ResultModel.SUCCESS) {
                            float c_cnt = 0;
                            if (model.getItems().size() > 0) {
                                mShipScanList = model.getItems();
                                for (int i = 0; i < model.getItems().size(); i++) {
                                    ShipScanModel.Item item = (ShipScanModel.Item) model.getItems().get(i);
                                    mScanAdapter.addData(item);
                                }
                                mScanAdapter.notifyDataSetChanged();
                                viewAll();

                            }
                            for (int k = 0; k < mAdapter.getItemCount(); k++) {
                                if (mShipList.get(k).getFg_name().equals(mShipScanModel.getItems().get(0).getFg_name())) {
                                    if (mAdapter.itemsList.get(k).getScan_qty() > mAdapter.itemsList.get(k).getSp_qty()) {
                                        Utils.Toast(mContext, "의뢰수량을 초과했습니다.");
                                        return;
                                    }
                                    c_cnt = mShipList.get(k).getScan_qty() + mShipScanModel.getItems().get(0).getScan_qty();
                                    mShipList.get(k).setScan_qty(c_cnt);
                                }

                            }
                            mScanAdapter.notifyDataSetChanged();
                            mAdapter.notifyDataSetChanged();
                            mIncode.add(barcodeScan);
                            et_scan_qty.setText(mScanAdapter.getItemCount() + " 건");


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
            public void onFailure(Call<ShipScanModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    public class ShipScanAdapter extends RecyclerView.Adapter<ShipScanAdapter.ViewHolder> {

        ShipScanModel.Item mModel;
        List<ShipScanModel.Item> itemsList;
        Activity mActivity;
        Handler mHandler = null;
        int pos = -1;


        public ShipScanAdapter(Activity context) {
            mActivity = context;
        }

        public void setData(List<ShipScanModel.Item> list) {
            itemsList = list;
        }


        public void clearData() {
            if (itemsList != null) itemsList.clear();
        }

        public void setRetHandler(Handler h) {
            this.mHandler = h;
        }

        public List<ShipScanModel.Item> getData() {
            return itemsList;
        }

        public void addData(ShipScanModel.Item item) {
            if (itemsList == null) itemsList = new ArrayList<>();
            itemsList.add(item);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int z) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_shipok_list, viewGroup, false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final ShipScanModel.Item item = itemsList.get(position);

            //포지션, PLTNO, 스캔수량, 바코트NO, 스캔총합
            holder.m_position.setText(Integer.toString(position));
            holder.msg.setText(item.getMsg());
            holder.scan_qty.setText(Float.toString(item.getScan_qty()));
            pos++;

            AddData(pos, barcodeScan, et_plt_no.getText().toString(), mShipScanModel.getItems().get(0).getScan_qty(), mShipScanModel.getItems().get(0).getFg_name(), mac, 30);
            Log.d("값:", String.valueOf(mShipScanModel.getItems().get(0).getScan_qty()) + ",  " + mShipScanModel.getItems().get(0).getFg_name());
            viewAll();

        }

        @Override
        public int getItemCount() {
            return (null == itemsList ? 0 : itemsList.size());
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView stock_qty;     //잔여수량
            TextView scan_qty;      //스캔수량
            TextView msg;           //바코드
            TextView m_position;    //포지션
            TextView m_pltno;       //pltno
            TextView scan_tot_qty;      //스캔총수량

            public ViewHolder(View view) {
                super(view);

                scan_qty = view.findViewById(R.id.tv_plt_no);
                msg = view.findViewById(R.id.tv_barcode);
                m_position = view.findViewById(R.id.tv_scan_qty);


                /*view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message msg = new Message();
                        msg.obj = itemsList.get(getAdapterPosition());
                        msg.what = getAdapterPosition();
                        mHandler.sendMessage(msg);
                    }
                });*/
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Integer deleteRows = myDB.deleteDatas();
        deleteDatas();
    }

    /**
     * 출하등록
     */
    private void request_ship_save() {
        int cnt = 0, c_cnt = 0;
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        JsonObject json = new JsonObject();
        //로그인 ID
        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");
        String m_date = item_date.getText().toString().replace("-", "");
        String fac_code = (String) SharedData.getSharedData(mContext, SharedData.UserValue.FAC_CODE.name(), "");
        //List<ShipListModel.ShipItem> items = (List<ShipListModel.ShipItem>) mShipModel.getItems();

        Cursor res = myDB.getwg();
        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            cnt++;
            if (cnt == res.getCount()) {
                buffer.append(res.getString(0));
            } else {
                buffer.append(res.getString(0) + ";");
            }


        }
        Log.d("중량값마지막제외::", buffer.toString());

        Cursor res1 = myDB.getbarplt();
        //Cursor res1 = myDB.getplt();
        StringBuffer buffer1 = new StringBuffer();

        while (res1.moveToNext()) {
            c_cnt++;
            if (c_cnt == res1.getCount()) {
                buffer1.append(res1.getString(0) + ";" + res1.getString(1));
                //buffer.append("s_barcode: " + res.getString(0));
            } else {
                buffer1.append(res1.getString(0) + ";" + res1.getString(1) + ";");
                //buffer.append("s_barcode: " + res.getString(0) + ";");
            }
        }

        Log.d("바코드plt::", buffer1.toString());

        JsonObject obj = new JsonObject();
        JsonArray list = new JsonArray();

        obj.addProperty("p_ship_date", m_date);                    //일자
        obj.addProperty("p_fac_code", fac_code);                   //공장코드
        obj.addProperty("p_ship_no", "");                    //ship_no
        obj.addProperty("p_wh_code", wh_code);                     //창고코드
        obj.addProperty("p_cst_code", cst_code);                   //거래처코드
        obj.addProperty("p_deli_place", "");                 //deli_place
        obj.addProperty("p_plt_wgt", buffer.toString());           //중량
        obj.addProperty("p_lbl_list", buffer1.toString());         //바코드+PLTNO
        obj.addProperty("p_user_id", userID);    //로그인ID

        list.add(obj);
        json.add("detail", list);

        Utils.Log("new Gson().toJson(json) ==> : " + new Gson().toJson(json));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(json));

        Call<ResultModel> call = service.postShipSave(body);

        call.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                if (response.isSuccessful()) {
                    ResultModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {

                            mOneBtnPopup = new OneBtnPopup(getActivity(), "출하등록 되었습니다.", R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
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

                    mTwoBtnPopup = new TwoBtnPopup(getActivity(), "출하등록을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                request_ship_save();
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
                mTwoBtnPopup = new TwoBtnPopup(getActivity(), "출하등록을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            request_ship_save();
                            mTwoBtnPopup.hideDialog();
                            btn_next.setEnabled(true);

                        }
                    }
                });
            }
        });

    }//Close


}//onClose
