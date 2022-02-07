package kr.co.leeku.wms.menu.osr;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.honeywell.aidc.BarcodeReadEvent;

import java.util.ArrayList;
import java.util.List;

import kr.co.leeku.wms.R;
import kr.co.leeku.wms.common.SharedData;
import kr.co.leeku.wms.common.Utils;
import kr.co.leeku.wms.custom.CommonFragment;
import kr.co.leeku.wms.honeywell.AidcReader;
import kr.co.leeku.wms.menu.popup.OneBtnPopup;
import kr.co.leeku.wms.menu.popup.TwoBtnPopup;
import kr.co.leeku.wms.model.OsrDetailModel;
import kr.co.leeku.wms.model.OsrListModel;
import kr.co.leeku.wms.model.ResultModel;
import kr.co.leeku.wms.network.ApiClientService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OsrDetailFragment extends CommonFragment {

    Context mContext;
    OsrListModel mOsrModel = null;
    OsrListModel.Item order = null;
    int mPosition = -1;

    OsrDetailModel mOsrDetailModel;
    List<OsrDetailModel.Item> mOsrDetailList;

    TextView tv_cst_name, tv_fg_name, tv_dmd_wht, tv_scan_qty;
    RecyclerView osr_ok_listview;
    ImageButton btn_next_ok, bt_serial;
    String barcodeScan, beg_barcode, m_date = null, wh_code = null;
    ListAdapter mAdapter;
    List<String> mIncode;
    EditText et_serial;

    OneBtnPopup mOneBtnPopup;
    TwoBtnPopup mTwoBtnPopup;

    private SoundPool sound_pool;
    int soundId;
    MediaPlayer mediaPlayer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mIncode = new ArrayList<>();

    }//Close onCreate


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_osr_detail, container, false);

        Bundle arguments = getArguments();
        mOsrModel = (OsrListModel) arguments.getSerializable("model");
        mPosition = arguments.getInt("position");
        order = mOsrModel.getItems().get(mPosition);
        m_date = arguments.getString("m_date");
        wh_code = arguments.getString("wh_code");

        tv_cst_name = v.findViewById(R.id.tv_cst_name);
        tv_fg_name = v.findViewById(R.id.tv_fg_name);
        tv_dmd_wht = v.findViewById(R.id.tv_dmd_wht);
        tv_scan_qty = v.findViewById(R.id.tv_scan_qty);
        osr_ok_listview = v.findViewById(R.id.osr_ok_listview);
        btn_next_ok = v.findViewById(R.id.btn_next_ok);
        et_serial = v.findViewById(R.id.et_serial);
        bt_serial = v.findViewById(R.id.bt_serial);

        tv_cst_name.setText(order.getCst_name());
        tv_fg_name.setText(order.getFg_name());
        tv_dmd_wht.setText(Float.toString(order.getDmd_wht()));

        osr_ok_listview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ListAdapter(getActivity());
        osr_ok_listview.setAdapter(mAdapter);

        btn_next_ok.setOnClickListener(onClickListener);
        bt_serial.setOnClickListener(onClickListener);

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

                    beg_barcode = barcodeScan;
                    OsrDetailList(barcodeScan);

                }
            }
        });

    }//Close onResume

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_next_ok:
                    btn_next_ok.setEnabled(false);
                    if (mAdapter.getItemCount() <= 0) {
                        Utils.Toast(mContext, "SerialNo를 스캔해주세요.");
                        btn_next_ok.setEnabled(true);
                        return;
                    }else{
                        request_osr_save();
                    }
                    break;

                case R.id.bt_serial:
                    if (et_serial.getText().toString().equals("")) {
                        Utils.Toast(mContext, "SerialNo를 입력해주세요.");
                        return;
                    }

                    barcodeScan = et_serial.getText().toString();

                    if (mIncode != null) {
                        if (mIncode.contains(barcodeScan)) {
                            Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                            return;
                        }
                    }

                    if (beg_barcode != null) {
                        if (beg_barcode.equals(barcodeScan)) {
                            Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                            return;
                        }
                    }

                    beg_barcode = barcodeScan;
                    OsrDetailList(barcodeScan);
                    et_serial.setText("");
                    break;
            }
        }
    };


    /**
     * 외주출고 리스트
     */
    private void OsrDetailList(String bar) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<OsrDetailModel> call = service.OsrDetailList("sp_api_osr_plan_list", "BARCODE_CHECK", m_date, wh_code, order.getOod_dmd_no(), bar);

        call.enqueue(new Callback<OsrDetailModel>() {
            @Override
            public void onResponse(Call<OsrDetailModel> call, Response<OsrDetailModel> response) {
                if (response.isSuccessful()) {
                    mOsrDetailModel = response.body();
                    final OsrDetailModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mOsrDetailModel != null) {
                        if (mOsrDetailModel.getFlag() == ResultModel.SUCCESS || mOsrDetailModel.getFlag() == -2) {
                            if (mOsrDetailModel.getFlag() == -2) {
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


                                mOsrDetailList = model.getItems();
                                for (int i = 0; i < model.getItems().size(); i++) {

                                    OsrDetailModel.Item item = (OsrDetailModel.Item) model.getItems().get(i);
                                    mAdapter.addData(item);

                                }
                                mAdapter.notifyDataSetChanged();
                                osr_ok_listview.setAdapter(mAdapter);
                                mIncode.add(barcodeScan);
                                if (mAdapter.getItemCount() > 0) {
                                    float cnt = 0;
                                    for (int i = 0; i < mAdapter.getItemCount(); i++) {
                                        cnt += mAdapter.itemsList.get(i).getScan_qty();
                                    }
                                    tv_scan_qty.setText(Utils.setComma(cnt));
                                    //tv_scan_qty.setText(Float.toString(cnt));

                                }

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
            public void onFailure(Call<OsrDetailModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close


    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

        List<OsrDetailModel.Item> itemsList;
        Activity mActivity;
        Handler mHandler = null;


        public ListAdapter(Activity context) {
            mActivity = context;
        }

        public void setData(List<OsrDetailModel.Item> list) {
            itemsList = list;
        }

        public void clearData() {
            if (itemsList != null) itemsList.clear();
        }

        public void setRetHandler(Handler h) {
            this.mHandler = h;
        }

        public List<OsrDetailModel.Item> getData() {
            return itemsList;
        }

        public void addData(OsrDetailModel.Item item) {
            if (itemsList == null) itemsList = new ArrayList<>();
            itemsList.add(item);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int z) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_osr_datail_list, viewGroup, false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final OsrDetailModel.Item item = itemsList.get(position);

            holder.fg_name.setText("품명:  " + item.getFg_name() + "   " + "수량:  " + Float.toString(item.getScan_qty()));

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

                    if (mAdapter.getItemCount() > 0) {
                        float c_cnt = 0;

                        for (int i = 0; i < mAdapter.getItemCount(); i++) {
                            c_cnt += itemsList.get(i).getScan_qty();
                        }
                        tv_scan_qty.setText(Utils.setComma(c_cnt));
                        //tv_scan_qty.setText(Float.toString(c_cnt));
                    } else {
                        tv_scan_qty.setText("");
                    }
                }
            });


        }

        @Override
        public int getItemCount() {
            return (null == itemsList ? 0 : itemsList.size());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView fg_name;
            ImageButton bt_delete;

            public ViewHolder(View view) {
                super(view);

                fg_name = view.findViewById(R.id.tv_fg_name);
                bt_delete = view.findViewById(R.id.bt_delete);

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
    }//Close Adapter

    /**
     * 외주출고
     */
    private void request_osr_save() {

        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        JsonObject json = new JsonObject();
        //로그인 ID
        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");
        String fac_code = (String) SharedData.getSharedData(mContext, SharedData.UserValue.FAC_CODE.name(), "");
        //List<ShipListModel.ShipItem> items = (List<ShipListModel.ShipItem>) mShipModel.getItems();
        List<OsrDetailModel.Item> items = mAdapter.getData();
        StringBuffer buffer = new StringBuffer();
        for (OsrDetailModel.Item item : items) {
            buffer.append(item.getLbl_id() + ";");
        }

        JsonObject obj = new JsonObject();
        JsonArray list = new JsonArray();

        obj.addProperty("p_ood_date", m_date);                                   //일자
        obj.addProperty("p_ood_dmd_no", order.getOod_dmd_no());                  //전표번호
        obj.addProperty("p_lbl_list", buffer.toString());                        //바코드번호
        obj.addProperty("p_user_id", userID);                                    //로그인ID
        list.add(obj);
        json.add("detail", list);

        Utils.Log("new Gson().toJson(json) ==> : " + new Gson().toJson(json));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(json));

        Call<ResultModel> call = service.postOsrSave(body);

        call.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                if (response.isSuccessful()) {
                    ResultModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {

                            mOneBtnPopup = new OneBtnPopup(getActivity(), "출고등록 되었습니다.", R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        mOneBtnPopup.hideDialog();
                                        getActivity().finish();
                                        btn_next_ok.setEnabled(true);
                                    }
                                }
                            });

                        } else {
                            mOneBtnPopup = new OneBtnPopup(getActivity(), model.getMSG(), R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        mOneBtnPopup.hideDialog();
                                        btn_next_ok.setEnabled(true);

                                    }
                                }
                            });
                        }
                    }
                } else {
                    Utils.LogLine(response.message());

                    mTwoBtnPopup = new TwoBtnPopup(getActivity(), "출고등록을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                request_osr_save();
                                mTwoBtnPopup.hideDialog();
                                btn_next_ok.setEnabled(true);

                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResultModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                mTwoBtnPopup = new TwoBtnPopup(getActivity(), "출고등록을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            request_osr_save();
                            mTwoBtnPopup.hideDialog();
                            btn_next_ok.setEnabled(true);

                        }
                    }
                });
            }
        });

    }//Close


}//Close Fragment
