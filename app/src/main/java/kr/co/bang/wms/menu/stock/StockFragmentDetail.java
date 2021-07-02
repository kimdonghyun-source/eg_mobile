package kr.co.bang.wms.menu.stock;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import java.util.ArrayList;
import java.util.List;

import kr.co.bang.wms.R;
import kr.co.bang.wms.common.SharedData;
import kr.co.bang.wms.common.Utils;
import kr.co.bang.wms.custom.CommonFragment;
import kr.co.bang.wms.honeywell.AidcReader;
import kr.co.bang.wms.menu.house_new_move.HouseNewMoveDetailFragment;
import kr.co.bang.wms.menu.popup.OneBtnPopup;
import kr.co.bang.wms.menu.popup.TwoBtnPopup;
import kr.co.bang.wms.model.InvenModel;
import kr.co.bang.wms.model.MatOutDetailModel;
import kr.co.bang.wms.model.ResultModel;
import kr.co.bang.wms.model.StockDetailModel;
import kr.co.bang.wms.model.StockModel;
import kr.co.bang.wms.network.ApiClientService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockFragmentDetail extends CommonFragment {

    Context mContext;
    TextView tv_empty, tv_stk_remark, tv_stk_date, tv_stk_wh_code, tv_list_cnt;
    StockModel mStockmodel;
    StockDetailModel mStockDetailmodel;
    List<StockDetailModel.stockDetailModel> mStockDetailList;
    int mPosition = -1;
    StockModel.stockModel mOrder;
    ListAdapter mAdapter;
    ListView stockDetail_listView;
    String barcode, beg_barcode = null;
    EditText et_from;
    OneBtnPopup mOneBtnPopup;
    TwoBtnPopup mTwoBtnPopup;
    ImageButton bt_next;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();


    }//Close onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_stock_datail, container, false);

        tv_empty = v.findViewById(R.id.tv_empty);
        tv_stk_date = v.findViewById(R.id.tv_stk_date);
        tv_stk_remark = v.findViewById(R.id.tv_stk_remark);
        tv_stk_wh_code = v.findViewById(R.id.tv_stk_wh_code);
        stockDetail_listView = v.findViewById(R.id.stockDetail_listView);
        et_from = v.findViewById(R.id.et_from);
        bt_next = v.findViewById(R.id.bt_next);
        tv_list_cnt = v.findViewById(R.id.tv_list_cnt);
        mAdapter = new ListAdapter();
        stockDetail_listView.setAdapter(mAdapter);

        bt_next.setOnClickListener(onClickListener);

        Bundle arguments = getArguments();

        mStockmodel = (StockModel) arguments.getSerializable("model");
        mPosition = arguments.getInt("position");
        mOrder = mStockmodel.getItems().get(mPosition);
        tv_stk_date.setText(mOrder.getStk_date());
        tv_stk_wh_code.setText(mOrder.getWh_name());
        tv_stk_remark.setText(mOrder.getRemark());

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
                    barcode = event.getBarcodeData();
                    tv_empty.setVisibility(View.GONE);
                    et_from.setText(barcode);

                    if (beg_barcode != null) {
                        if (beg_barcode.equals(barcode)) {
                            Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                            return;
                        }
                    }


                    if (mStockDetailList != null) {
                        if (mStockDetailList.get(0).getLot_no().equals(barcode)) {
                            Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                            return;
                        }
                    }
                    StockListSearch();
                    beg_barcode = barcode;
                }
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_next:
                    bt_next.setEnabled(false);
                    if (mStockDetailList != null) {
                        bt_next.setEnabled(false);
                        stk_scan_save();
                    } else {
                        bt_next.setEnabled(true);
                        Utils.Toast(mContext, "스캔을 진행해주세요.");
                        return;
                    }
            }

        }
    };

    /**
     * 재고조사 리스트 조회
     */
    private void StockListSearch() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<StockDetailModel> call = service.stk_serial_list("sp_pda_stk_scan", barcode, mOrder.getStk_date(), mOrder.getWh_code(), String.valueOf(mOrder.getStk_no1()));

        call.enqueue(new Callback<StockDetailModel>() {
            @Override
            public void onResponse(Call<StockDetailModel> call, Response<StockDetailModel> response) {
                if (response.isSuccessful()) {
                    mStockDetailmodel = response.body();
                    final StockDetailModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mStockDetailmodel != null) {
                        if (mStockDetailmodel.getFlag() == ResultModel.SUCCESS) {

                            if (model.getItems().size() > 0) {

                                for (int i = 0; i < model.getItems().size(); i++) {
                                    StockDetailModel.stockDetailModel item = (StockDetailModel.stockDetailModel) model.getItems().get(i);
                                    mAdapter.addData(item);

                                }

                                mAdapter.notifyDataSetChanged();
                            }

                            if (mStockDetailmodel != null){
                                tv_list_cnt.setText(String.valueOf(mStockDetailList.size()));
                            }

                        } else {
                            Utils.Toast(mContext, model.getMSG());
                            if (mStockDetailList != null) {
                                mStockDetailList.clear();
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else {
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code() + " : " + response.message());
                }
            }


            @Override
            public void onFailure(Call<StockDetailModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }

    /**
     * 재고실사저장
     */
    private void stk_scan_save() {

        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        JsonObject json = new JsonObject();
        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");
        JsonArray list = new JsonArray();
        List<StockDetailModel.stockDetailModel> items = mAdapter.getData();


        for (StockDetailModel.stockDetailModel item : items) {
            JsonObject obj = new JsonObject();

            obj.addProperty("itm_code", item.getItm_code());
            obj.addProperty("serial_no", item.getLot_no());
            obj.addProperty("wh_code", item.getWh_code());
            obj.addProperty("serial_qty", item.getInv_qty());
            list.add(obj);

        }

        json.addProperty("p_corp_code", mOrder.getCorp_code());
        json.addProperty("p_stk_date", mOrder.getStk_date());
        json.addProperty("p_stk_no1", mOrder.getStk_no1());
        json.addProperty("p_user_id", userID);
        json.add("detail", list);

        Utils.Log("new Gson().toJson(json) ==> : " + new Gson().toJson(json));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(json));

        Call<StockDetailModel> call = service.stockSave(body);

        call.enqueue(new Callback<StockDetailModel>() {
            @Override
            public void onResponse(Call<StockDetailModel> call, Response<StockDetailModel> response) {
                if (response.isSuccessful()) {
                    StockDetailModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == StockDetailModel.SUCCESS) {
                            mOneBtnPopup = new OneBtnPopup(getActivity(), "처리되었습니다.", R.drawable.popup_title_alert, new Handler() {
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
                    mTwoBtnPopup = new TwoBtnPopup(getActivity(), "전송을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                stk_scan_save();
                                mTwoBtnPopup.hideDialog();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<StockDetailModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                mTwoBtnPopup = new TwoBtnPopup(getActivity(), "전송을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            stk_scan_save();
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
            return (null == mStockDetailList ? 0 : mStockDetailList.size());
        }

        public void addData(StockDetailModel.stockDetailModel item) {
            if (mStockDetailList == null) mStockDetailList = new ArrayList<>();
            mStockDetailList.add(item);
        }

        public void clearData() {
            mStockDetailList.clear();
        }

        public List<StockDetailModel.stockDetailModel> getData() {
            return mStockDetailList;
        }

        @Override
        public int getCount() {
            if (mStockDetailList == null) {
                return 0;
            }

            return mStockDetailList.size();
        }


        @Override
        public StockDetailModel.stockDetailModel getItem(int position) {
            return mStockDetailList.get(position);
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
                v = inflater.inflate(R.layout.cell_stock_detail, null);

                holder.itm_name = v.findViewById(R.id.tv_itm_name);
                holder.lot_no = v.findViewById(R.id.tv_lot_no);
                //holder.wh_code = v.findViewById(R.id.tv_wh_code);
                holder.inv_qty = v.findViewById(R.id.tv_inv_qty);

                v.setTag(holder);

            } else {
                holder = (ViewHolder) v.getTag();
            }

            final StockDetailModel.stockDetailModel data = mStockDetailList.get(position);
            holder.itm_name.setText(data.getItm_name());
            holder.lot_no.setText(data.getLot_no());
            //holder.wh_code.setText(data.getWh_code());
            holder.inv_qty.setText(Integer.toString(data.getInv_qty()));



            /*v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.obj = data;
                    mHandler.sendMessage(msg);
                    DetailScanList(position);
                }
            });*/


            return v;
        }

        public class ViewHolder {
            TextView itm_name;
            TextView lot_no;
            TextView wh_code;
            TextView inv_qty;


        }


    }//Close Adapter


}//Close Activity
