package kr.co.bang.wms.menu.house_move;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.honeywell.aidc.BarcodeReadEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

import kr.co.bang.wms.R;
import kr.co.bang.wms.common.SharedData;
import kr.co.bang.wms.common.Utils;
import kr.co.bang.wms.custom.CommonFragment;
import kr.co.bang.wms.honeywell.AidcReader;
import kr.co.bang.wms.menu.out_list.MorOutListFragment;
import kr.co.bang.wms.menu.popup.LocationListPopup;
import kr.co.bang.wms.menu.popup.OneBtnPopup;
import kr.co.bang.wms.menu.popup.TwoBtnPopup;
import kr.co.bang.wms.model.LocationModel;
import kr.co.bang.wms.model.MatMoveModel;
import kr.co.bang.wms.model.MaterialLocAndLotModel;
import kr.co.bang.wms.model.MorListModel;
import kr.co.bang.wms.model.ResultModel;
import kr.co.bang.wms.model.WarehouseModel;
import kr.co.bang.wms.network.ApiClientService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HouseMoveFragment extends CommonFragment {

    Context mContext;
    EditText et_from;
    TextView tv_empty;
    ImageButton bt_from, bt_next;
    String barcode_scan;
    MatMoveModel moveModel;
    List<MatMoveModel.Item> moveList;
    ListView mlistview;
    ListAdapter mAdapter;
    List<String> mBarcode;
    String mLocation;
    LocationListPopup mLocationListPopup;
    WarehouseModel.Items WareLocation;
    String wh_in_code;
    OneBtnPopup mOneBtnPopup;
    TwoBtnPopup mTwoBtnPopup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mBarcode = new ArrayList<>();

    }//Close onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_house_move, container, false);
        et_from = v.findViewById(R.id.et_from);
        tv_empty = v.findViewById(R.id.tv_empty);
        bt_next = v.findViewById(R.id.bt_next);
        bt_from = v.findViewById(R.id.bt_from);
        mlistview = v.findViewById(R.id.moveListView);
        mAdapter = new ListAdapter();
        mlistview.setAdapter(mAdapter);

        bt_from.setOnClickListener(onClickListener);
        bt_next.setOnClickListener(onClickListener);
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
                    tv_empty.setVisibility(View.GONE);
                    barcode_scan = barcode;

                    if (mBarcode.contains(barcode)) {
                        Utils.Toast(mContext, "동일한 SerialNo를 스캔하셨습니다.");
                        return;
                    }

                    mLocation = barcode;
                    pdaSerialScan();

                }
            }
        });

    }//Close onResume

    @Override
    public void onPause() {
        super.onPause();
        AidcReader.getInstance().release();
        AidcReader.getInstance().setListenerHandler(null);
    }//Close onPause


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_from: {
                    requestHouse();
                    break;
                }
                case R.id.bt_next: {

                    if(WareLocation == null) {
                        Utils.Toast(mContext, getString(R.string.error_location_move));
                        return;
                    }


                    if(mAdapter.getCount() <= 0) {
                        Utils.Toast(mContext, getString(R.string.error_location_scan));
                        return;
                    }

                    mTwoBtnPopup = new TwoBtnPopup(getActivity(), "이동처리 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                requestMatMove();
                                mTwoBtnPopup.hideDialog();
                            }
                        }
                    });

                    break;
                }
            }
        }
    };


    /**
     * 창고이동 시리얼스캔
     */
    private void pdaSerialScan() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<MatMoveModel> call = service.movescan("sp_pda_mat_move_serial_scan", barcode_scan);

        call.enqueue(new Callback<MatMoveModel>() {
            @Override
            public void onResponse(Call<MatMoveModel> call, Response<MatMoveModel> response) {
                if (response.isSuccessful()) {
                    moveModel = response.body();
                    final MatMoveModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (moveModel != null) {
                        if (moveModel.getFlag() == ResultModel.SUCCESS) {
                            //moveList = model.getItems();
                            if (model.getItems().size() > 0) {
                                for (int i = 0; i < model.getItems().size(); i++) {

                                    MatMoveModel.Item item = (MatMoveModel.Item) model.getItems().get(i);
                                    mAdapter.addData(item);
                                }
                                mAdapter.notifyDataSetChanged();
                                mBarcode.add(mLocation);
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
            public void onFailure(Call<MatMoveModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }

    /**
     * 창고검색
     */
    private void requestHouse() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<WarehouseModel> call = service.postWarehouse("sp_pda_mst_wh_list", "");

        call.enqueue(new Callback<WarehouseModel>() {
            @Override
            public void onResponse(Call<WarehouseModel> call, Response<WarehouseModel> response) {
                if (response.isSuccessful()) {
                    WarehouseModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mLocationListPopup = new LocationListPopup(getActivity(), model.getItems(), R.drawable.popup_title_searchloc, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        WarehouseModel.Items item = (WarehouseModel.Items)msg.obj;
                                       // mAdapter.clearData();

                                        mAdapter.notifyDataSetChanged();
                                        WareLocation = item;
                                        et_from.setText("[" +WareLocation.getWh_code() + "] " + WareLocation.getWh_name());
                                        String g_code = et_from.getText().toString();
                                        int idx = g_code.indexOf("]");
                                        wh_in_code = g_code.substring(1, idx);

                                        mLocationListPopup.hideDialog();
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
            public void onFailure(Call<WarehouseModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }

    /**
     * 창고이동처리
     * */
    private void requestMatMove(){
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        JsonObject json = new JsonObject();
        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");
        JsonArray list = new JsonArray();
        List<MatMoveModel.Item> items = mAdapter.getData();
        for (MatMoveModel.Item item : items) {
            JsonObject obj = new JsonObject();
            obj.addProperty("gbn", item.getGbn());
            obj.addProperty("serial_no", item.getSerial_no());
            obj.addProperty("itm_code", item.getItm_code());
            obj.addProperty("move_qty", item.getMove_qty());
            list.add(obj);
        }

        json.addProperty("p_wh_code_in", wh_in_code);
        json.addProperty("p_user_id", userID);
        json.add("detail", list);

        Utils.Log("new Gson().toJson(json) ==> : "+new Gson().toJson(json));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(json));

        Call<ResultModel> call = service.postSendMatMove(body);

        call.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                if(response.isSuccessful()){
                    ResultModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if(model.getFlag() == ResultModel.SUCCESS) {
                            mOneBtnPopup = new OneBtnPopup(getActivity(), "이동처리 되었습니다.", R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        getActivity().finish();
                                        mOneBtnPopup.hideDialog();
                                    }
                                }
                            });
                        }else{
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
                }else{
                    Utils.LogLine(response.message());
                    mTwoBtnPopup = new TwoBtnPopup(getActivity(), "이동 전송을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                requestMatMove();
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
                            requestMatMove();
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

        public int getItemCount() {
            return (null == moveList ? 0 : moveList.size());
        }

        public void addData(MatMoveModel.Item item) {
            if (moveList == null) moveList = new ArrayList<>();
            moveList.add(item);
        }

        public void clearData() {
            moveList.clear();
        }

        public List<MatMoveModel.Item> getData() {
            return moveList;
        }

        @Override
        public int getCount() {
            if (moveList == null) {
                return 0;
            }

            return moveList.size();
        }

        @Override
        public MatMoveModel.Item getItem(int position) {
            return moveList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (v == null) {
                holder = new ViewHolder();
                v = inflater.inflate(R.layout.cell_mat_move, null);

                holder.gbn = v.findViewById(R.id.tv_gbn);
                holder.serial_no = v.findViewById(R.id.tv_serial_no);
                holder.itm_code = v.findViewById(R.id.tv_itm_code);
                holder.itm_name = v.findViewById(R.id.tv_itm_name);
                holder.move_qty = v.findViewById(R.id.tv_qty);

                v.setTag(holder);

            } else {
                holder = (ListAdapter.ViewHolder) v.getTag();
            }

            final MatMoveModel.Item data = moveList.get(position);
            holder.gbn.setText(data.getGbn());
            holder.serial_no.setText(data.getSerial_no());
            holder.itm_code.setText(data.getItm_code());
            holder.itm_name.setText(data.getItm_name());
            holder.move_qty.setText(Integer.toString(data.getMove_qty()));

            /*v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.obj = data;
                    mHandler.sendMessage(msg);
                }
            });*/

            return v;
        }

        public class ViewHolder {
            TextView gbn;
            TextView serial_no;
            TextView itm_code;
            TextView itm_name;
            TextView move_qty;
        }


    }


}//Close Class
