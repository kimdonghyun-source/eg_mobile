package kr.co.ssis.wms.menu.ship;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.honeywell.aidc.BarcodeReadEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kr.co.siss.wms.R;
import kr.co.ssis.wms.common.Define;
import kr.co.ssis.wms.common.Utils;
import kr.co.ssis.wms.custom.CommonFragment;
import kr.co.ssis.wms.honeywell.AidcReader;
import kr.co.ssis.wms.menu.main.BaseActivity;
import kr.co.ssis.wms.menu.popup.TwoBtnPopup;
import kr.co.ssis.wms.model.ResultModel;
import kr.co.ssis.wms.model.ShipListModel;
import kr.co.ssis.wms.model.ShipOkModel;
import kr.co.ssis.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipOkFragment extends CommonFragment {

    Context mContext;

    ShipListModel mShipModel = null;
    ShipListModel.ShipItem order = null;
    int mPosition = -1;

    TextView tv_itm_code;
    TextView tv_itm_name;
    TextView tv_itm_size;
    TextView tv_c_name;
    TextView tv_ship_qty;
    TextView tv_pickin_qty;
    ListView ship_ok_listview;
    List<ShipOkModel.Item> mOkListModel;
    ShipOkModel.Item mOrderModel;
    ShipOkModel mOkModel;
    ListAdapter mAdapter;
    String barcodeScan, beg_barcode = null;
    int count = 0;
    ImageButton btn_next_ok;
    List<String> mBarcode;
    String mOrderNo;
    TwoBtnPopup mPopup;
    Activity mActivity;
    ArrayList<ShipOkModel.Item> listViewItemList = new ArrayList<ShipOkModel.Item>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mBarcode = new ArrayList<>();

    }//Close onCreate


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_ship_ok, container, false);

        Bundle arguments = getArguments();
        mShipModel = (ShipListModel) arguments.getSerializable("model");
        mPosition = arguments.getInt("position");
        order = mShipModel.getItems().get(mPosition);

        tv_itm_code = v.findViewById(R.id.tv_itm_code);
        tv_itm_name = v.findViewById(R.id.tv_itm_name);
        tv_itm_size = v.findViewById(R.id.tv_itm_size);
        tv_c_name = v.findViewById(R.id.tv_c_name);
        tv_ship_qty = v.findViewById(R.id.tv_ship_qty);
        tv_pickin_qty = v.findViewById(R.id.tv_pickin_qty);
        ship_ok_listview = v.findViewById(R.id.ship_ok_listview);
        btn_next_ok = v.findViewById(R.id.btn_next_ok);


        mAdapter = new ListAdapter();
        ship_ok_listview.setAdapter(mAdapter);


        tv_itm_code.setText(order.getItm_code());
        tv_itm_name.setText(order.getItm_name());
        tv_itm_size.setText(order.getItm_size());
        tv_c_name.setText(order.getC_name());
        tv_ship_qty.setText(Integer.toString(order.getShip_qty()));

        btn_next_ok.setOnClickListener(onClickListener);


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
                            return;
                        }
                    }

                    if (mOkListModel != null) {
                        for (int i = 0; i < mAdapter.getCount(); i++) {
                            if (mOkListModel.get(i).getLot_no().equals(barcodeScan)) {
                                Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                                return;
                            }
                        }
                    }

                    if (order.getShip_qty() == count) {
                        Utils.Toast(mContext, "피킹 수량이 의뢰수량을 초과하였습니다.");
                        return;
                    } else {
                        pdaSerialScan();
                        mOrderNo = barcode;

                    }
                    beg_barcode = barcodeScan;
                }
            }
        });

    }//Close onResume

    Comparator<ShipOkModel.Item> noDesc = new Comparator<ShipOkModel.Item>() {

        @Override
        public int compare(ShipOkModel.Item item1, ShipOkModel.Item item2) {
            int ret = 0;

            if (item1.getNo() < item2.getNo()) {
                ret = 1;
            } else if (item1.getNo() == item2.getNo()) {
                ret = 0;
            } else {
                ret = -1;
            }
            return ret;
        }
    };



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.btn_next_ok:

                    if (mAdapter.getCount() > 0) {
                        List<ShipOkModel.Item> datas = new ArrayList<>();
                        List<ShipOkModel.Item> itms = mAdapter.getData();
                        int count = 0;
                        for (int i = 0; i < itms.size(); i++) {
                            ShipOkModel.Item itm = itms.get(i);
                            if (itm.getWrk_qty() > 0) {
                                datas.add(itm);
                                count += itm.getWrk_qty();
                                order.setScan_qty(count);
                            }
                        }

                        if (count > order.getShip_qty()) {
                            Utils.Toast(mContext, "피킹수량이 의뢰수량보다 많습니다.");
                            return;
                        }

                        mShipModel.getItems().get(mPosition).setSet_scan_qty(count);
                        mShipModel.getItems().get(mPosition).setItems(datas);
                        Intent i = new Intent();
                        i.putExtra("model", mShipModel);
                        getActivity().setResult(Activity.RESULT_OK, i);
                        getActivity().finish();
                        break;
                    } else {
                        Utils.Toast(mContext, "스캔 내역이 없습니다.");
                    }
            }

        }
    };


    private void ShipScanList() {
        List<ShipOkModel.Item> datas = new ArrayList<>();

        Intent intent = new Intent(getActivity(), BaseActivity.class);
        intent.putExtra("menu", Define.MENU_SHIP);
        Bundle extras = new Bundle();

        extras.putSerializable("model", mOkModel);
        extras.putSerializable("model1", mShipModel);
        intent.putExtra("args", extras);
        getActivity().setResult(Activity.RESULT_OK, intent);
        //startActivityForResult(intent, 100);
        getActivity().finish();
    }


    /**
     * 출하피킹 바코드스캔
     */
    private void pdaSerialScan() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<ShipOkModel> call = service.shipOkSerialScan("sp_pda_ship_scan", barcodeScan, order.getItm_code());

        call.enqueue(new Callback<ShipOkModel>() {
            @Override
            public void onResponse(Call<ShipOkModel> call, Response<ShipOkModel> response) {
                if (response.isSuccessful()) {
                    mOkModel = response.body();
                    final ShipOkModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mOkModel != null) {
                        if (mOkModel.getFlag() == ResultModel.SUCCESS) {

                            List<ShipOkModel.Item> itms = mAdapter.getData();
                            if (model.getItems().size() > 0) {
                                int c_cnt = 0;

                                for (int i = 0; i < model.getItems().size(); i++) {
                                    ShipOkModel.Item item = (ShipOkModel.Item) model.getItems().get(i);
                                    mAdapter.addData(item);

                                }
                                //mAdapter.notifyDataSetChanged();
                                ship_ok_listview.setAdapter(mAdapter);
                                mBarcode.add(mOrderNo);
                                //mAdapter.notifyDataSetChanged();
                                //Collections.sort(strList, noDesc);
                                //Collections.sort(listViewItemList, noDesc);

                                Collections.sort(mOkListModel, noDesc);
                                Collections.reverse(mOkListModel);

                                mAdapter.notifyDataSetChanged();

                                for (int j = 0; j < mAdapter.getCount(); j++) {
                                    c_cnt += mOkListModel.get(j).getWrk_qty();

                                }
                                tv_pickin_qty.setText(Utils.setComma(c_cnt));
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
            public void onFailure(Call<ShipOkModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close


    class ListAdapter extends BaseAdapter {
        LayoutInflater mInflater;
        Handler mHandler = null;


        public ListAdapter() {
            mInflater = LayoutInflater.from(mContext);
            /*if (strList == null) {
                listViewItemList = new ArrayList<ShipOkModel.Item>() ;
            } else {
                listViewItemList = strList ;
            }*/
        }

        public int getItemCount() {
            return (null == mOkListModel ? 0 : mOkListModel.size());
        }

        public void addData(ShipOkModel.Item item) {
            if (mOkListModel == null) mOkListModel = new ArrayList<>();
            mOkListModel.add(item);
        }

        public void clearData() {
            mOkListModel.clear();
        }

        public List<ShipOkModel.Item> getData() {
            return mOkListModel;
        }

        @Override
        public int getCount() {
            if (mOkListModel == null) {
                return 0;
            }

            return mOkListModel.size();
        }

        public void setSumHandler(Handler h) {
            this.mHandler = h;
        }


        @Override
        public ShipOkModel.Item getItem(int position) {
            return mOkListModel.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            final ViewHolder holder;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (v == null) {
                holder = new ViewHolder();
                v = inflater.inflate(R.layout.cell_ship_ok, null);

                holder.lot_no = v.findViewById(R.id.tv_lot_no);
                holder.inv_qty = v.findViewById(R.id.tv_qty);
                holder.tv_ea = v.findViewById(R.id.tv_ea);
                holder.bt_delete = v.findViewById(R.id.bt_delete);
                holder.tv_no = v.findViewById(R.id.tv_no);

                holder.inv_qty.addTextChangedListener(new TextWatcher() {
                    String result = "";

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d("JeLib", "---------------------------");
                        if (s.toString().length() > 0 && !s.toString().equals(result)) {     // StackOverflow를 막기위해,
                            result = s.toString();   // 에딧텍스트의 값을 변환하여, result에 저장.

                            int cnt = Utils.stringToInt(result);

                            holder.inv_qty.setText(result);    // 결과 텍스트 셋팅.
                            holder.inv_qty.setSelection(result.length());     // 커서를 제일 끝으로 보냄.

                            //입력된 수량을 list에 넣어줌
                            //mOkListModel.get(position).setTin_dtl_qty(cnt);
                            mOkListModel.get(position).setWrk_qty(cnt);
                        }
                   /* if(Utils.isEmpty(s.toString())){
                        itemsList.get(holder.getAdapterPosition()).setInput_qty(0);
                    }*/
                        //mHandler.sendEmptyMessage(1);
                    }
                });

                holder.bt_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mOkListModel.remove(position);
                        mAdapter.notifyDataSetChanged();

                        if (mAdapter.getCount() > 0) {
                            count = 0;
                            for (int i = 0; i < mAdapter.getCount(); i++) {
                                count += mOkListModel.get(i).getWrk_qty();

                            }
                            tv_pickin_qty.setText(Utils.setComma(count));
                        } else {
                            tv_pickin_qty.setText("");
                        }

                        if (beg_barcode.equals(barcodeScan)) {
                            beg_barcode = "";
                        }
                    }
                });

                v.setTag(holder);

            } else {
                holder = (ViewHolder) v.getTag();
            }

            final ShipOkModel.Item data = mOkListModel.get(position);
            holder.lot_no.setText(data.getLot_no());
            holder.tv_ea.setText(order.getC_name());
            holder.inv_qty.setText(Integer.toString(data.getWrk_qty()));
            holder.tv_no.setText(Integer.toString(position));
            data.setNo(position);
            listViewItemList.add(data);


            return v;
        }


        public class ViewHolder {
            TextView lot_no;
            EditText inv_qty;
            TextView tv_ea;
            ImageButton bt_delete;
            TextView tv_no;


        }


    }//Close Adapter


}//Close Activity
