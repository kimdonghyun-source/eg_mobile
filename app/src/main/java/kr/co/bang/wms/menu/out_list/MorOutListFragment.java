package kr.co.bang.wms.menu.out_list;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.honeywell.aidc.BarcodeReadEvent;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.co.bang.wms.GlobalApplication;
import kr.co.bang.wms.R;
import kr.co.bang.wms.common.Define;
import kr.co.bang.wms.common.Utils;
import kr.co.bang.wms.custom.CommonFragment;
import kr.co.bang.wms.honeywell.AidcReader;
import kr.co.bang.wms.menu.main.BaseActivity;
import kr.co.bang.wms.menu.material_out.MaterialPickingAdapter;
import kr.co.bang.wms.menu.popup.OneBtnPopup;
import kr.co.bang.wms.model.MaterialLocAndLotModel;
import kr.co.bang.wms.model.MaterialOutDetailModel;
import kr.co.bang.wms.model.MorListModel;
import kr.co.bang.wms.model.ResultModel;
import kr.co.bang.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class MorOutListFragment extends CommonFragment {
    Context mContext;
    ImageButton ib_member, ib_store, bt_next;
    Button btn_store_join, btn_gogo;
    LinearLayout ll_date_list, ll_member_edit, img_order_text;
    TextView item_date, gubun;
    EditText et_merge_1;

    DatePickerDialog.OnDateSetListener callbackMethod;
    String mOrderNo;
    String slip_type, mor_list_date;
    Handler mHandler;

    ListView mlistview, store_listview;
    ListAdapter mAdapter;
    StoreListAdapter sotreAdapter;
    List<MorListModel.Items> mMorList;
    MorListModel mmorlistmodel;
    MorListModel.Items next_itm;
    int mSelect = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_mor_list, container, false);

        ib_member = v.findViewById(R.id.ib_member);
        ib_store = v.findViewById(R.id.ib_store);
        ll_date_list = v.findViewById(R.id.ll_date_list);
        ll_member_edit = v.findViewById(R.id.ll_member_edit);
        img_order_text = v.findViewById(R.id.img_order_text);
        item_date = v.findViewById(R.id.item_date);
        bt_next = v.findViewById(R.id.bt_next);
        btn_store_join = v.findViewById(R.id.btn_store_join);
        et_merge_1 = v.findViewById(R.id.et_merge_1);
        mlistview = v.findViewById(R.id.listview);
        this.store_listview = v.findViewById(R.id.store_listview);
        gubun = v.findViewById(R.id.gubun);
        btn_gogo = v.findViewById(R.id.btn_gogo);
        mAdapter = new ListAdapter();
        sotreAdapter = new StoreListAdapter();
        mlistview.setAdapter(mAdapter);
        store_listview.setAdapter(sotreAdapter);
        mHandler = handler;

        ib_member.setOnClickListener(onClickListener);
        ib_store.setOnClickListener(onClickListener);
        item_date.setOnClickListener(onClickListener);
        btn_store_join.setOnClickListener(onClickListener);
        btn_gogo.setOnClickListener(onClickListener);

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

        setTab(1);

        return v;
    }//onCreateView Close

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
                    if (barcode.length() == 12){
                        mOrderNo = barcode;
                    }else {
                        String s_barcode = barcode.substring(0, 11);
                        String s_type = barcode.substring(12, 13);
                        String s_date = barcode.substring(14, 22);
                        et_merge_1.setText(s_barcode);
                        mOrderNo = s_barcode;
                        slip_type = s_type;
                        mor_list_date = s_date;
                    }
                    if (gubun.getText().toString().equals("C")) {
                        requestMorListMember();
                    } else if (gubun.getText().toString().equals("A")) {
                        goMorStore();
                    }


                }
            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
        AidcReader.getInstance().release();
        AidcReader.getInstance().setListenerHandler(null);
    }

    private void setTab(int idx) {
        if (idx == 1) {
            ib_member.setSelected(true);
            ib_store.setSelected(false);
            ll_member_edit.setVisibility(View.VISIBLE);
            img_order_text.setVisibility(View.VISIBLE);
            ll_date_list.setVisibility(View.GONE);
            store_listview.setVisibility(View.GONE);
            mlistview.setVisibility(View.VISIBLE);
            gubun.setText("C");

        } else if (idx == 2) {
            ib_member.setSelected(false);
            ib_store.setSelected(true);
            ll_date_list.setVisibility(View.VISIBLE);
            mlistview.setVisibility(View.GONE);
            store_listview.setVisibility(View.VISIBLE);
            gubun.setText("A");


        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ib_member: {
                    if (mAdapter.getItemCount() == 0) {

                    } else {
                        mAdapter.clearData();
                        mAdapter.notifyDataSetChanged();
                    }
                    setTab(1);
                    break;
                }
                case R.id.ib_store: {
                    if (mAdapter.getItemCount() == 0) {

                    } else {
                        mAdapter.clearData();
                        mAdapter.notifyDataSetChanged();
                    }
                    setTab(2);
                    break;
                }
                case R.id.item_date: {
                    int c_year = Integer.parseInt(item_date.getText().toString().substring(0, 4));
                    int c_month = Integer.parseInt(item_date.getText().toString().substring(5, 7));
                    int c_day = Integer.parseInt(item_date.getText().toString().substring(8, 10));

                    DatePickerDialog dialog = new DatePickerDialog(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, callbackMethod, c_year, c_month - 1, c_day);
                    dialog.show();
                    break;
                }
                case R.id.btn_store_join: {
                    requestMorListStore();
                    break;
                }
                case R.id.btn_gogo: {
                    Intent intent = new Intent(mContext, BaseActivity.class);
                    intent.putExtra("menu", Define.MENU_PRODUCTION_DETAIL);
                    startActivity(intent);
                }

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
     * 주문자재출고 리스트 회원
     */
    private void requestMorListMember() {
        SimpleDateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        String TODAY = dfdate.format(new Date());
        String d_today = TODAY.replace("-", "");

        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<MorListModel> call = service.morlist("sp_pda_dis_mor_list", "C", mor_list_date, mOrderNo, slip_type);

        call.enqueue(new Callback<MorListModel>() {
            @Override
            public void onResponse(Call<MorListModel> call, Response<MorListModel> response) {
                if (response.isSuccessful()) {
                    mmorlistmodel = response.body();
                    final MorListModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mmorlistmodel != null) {
                        if (mmorlistmodel.getFlag() == ResultModel.SUCCESS) {
                            mMorList = model.getItems();
                            mAdapter.notifyDataSetChanged();
                            mlistview.setAdapter(mAdapter);

                            goMorMember();
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
            public void onFailure(Call<MorListModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }

    private void goMorMember() {
        MorListModel.Items o = mmorlistmodel.getItems().get(0);
        Intent intent = new Intent(mContext, BaseActivity.class);
        intent.putExtra("menu", Define.MENU_PRODUCTION_DETAIL);
        Bundle args = new Bundle();

        args.putString("TYPE", gubun.getText().toString());     //회원: C, 대리점: A
        args.putString("GUBUN", o.getSlip_type());              //전표타입: 주문: O, AS: A
        args.putString("SLIPNO", o.getSlip_no());               //전표번호
        args.putString("NAME", o.getCst_name());                //회원명
        args.putString("QTY", String.valueOf(o.getMor_qty()));  //수량
        intent.putExtra("args", args);
        startActivity(intent);
    }

    private void goMorStore() {
        Intent intent = new Intent(mContext, BaseActivity.class);
        intent.putExtra("menu", Define.MENU_PRODUCTION_DETAIL);
        Bundle args = new Bundle();
        args.putString("SLIPNO", mOrderNo);         //전표번호
        args.putString("TYPE", "A");              //회원 대리점 구분

        intent.putExtra("args", args);
        startActivity(intent);
    }


    /**
     * 주문자재출고 리스트 대리점 전체조회
     */
    private void requestMorListStore() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        String m_date = item_date.getText().toString().replace("-", "");
        Call<MorListModel> call = service.morlist("sp_pda_dis_mor_list", "A", m_date, mOrderNo, slip_type);

        call.enqueue(new Callback<MorListModel>() {
            @Override
            public void onResponse(Call<MorListModel> call, Response<MorListModel> response) {
                if (response.isSuccessful()) {
                    mmorlistmodel = response.body();
                    final MorListModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mmorlistmodel != null) {
                        if (mmorlistmodel.getFlag() == ResultModel.SUCCESS) {
                            mMorList = model.getItems();
                            sotreAdapter.notifyDataSetChanged();
                            store_listview.setAdapter(sotreAdapter);


                        } else {
                            Utils.Toast(mContext, model.getMSG());
                            mMorList.clear();
                            sotreAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code() + " : " + response.message());
                }
            }

            @Override
            public void onFailure(Call<MorListModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }


    class ListAdapter extends BaseAdapter {
        LayoutInflater mInflater;

        public ListAdapter() {
            mInflater = LayoutInflater.from(mContext);
        }

        public int getItemCount() {
            return (null == mMorList ? 0 : mMorList.size());
        }

        public void addData(MorListModel.Items item) {
            if (mMorList == null) mMorList = new ArrayList<>();
            mMorList.add(item);
        }

        public void clearData() {
            mMorList.clear();
        }

        public List<MorListModel.Items> getData() {
            return mMorList;
        }

        @Override
        public int getCount() {
            if (mMorList == null) {
                return 0;
            }

            return mMorList.size();
        }

        @Override
        public MorListModel.Items getItem(int position) {
            return mMorList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            if (v == null) {
                holder = new ViewHolder();
                v = mInflater.inflate(R.layout.cell_mor_list, null);

                v.setTag(holder);

                holder.tv_date = v.findViewById(R.id.tv_date);
                holder.tv_name = v.findViewById(R.id.tv_name);
                holder.tv_slip_no = v.findViewById(R.id.tv_slip_no);
                holder.tv_qty = v.findViewById(R.id.tv_qty);

            } else {
                holder = (ListAdapter.ViewHolder) v.getTag();
            }

            final MorListModel.Items data = mMorList.get(position);
            holder.tv_date.setText(data.getMor_date());
            holder.tv_name.setText(data.getCst_name());
            holder.tv_slip_no.setText(data.getSlip_no());
            holder.tv_qty.setText(Integer.toString(data.getMor_qty())); //인트

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.obj = data;
                    mHandler.sendMessage(msg);
                }
            });

            return v;
        }

        public class ViewHolder {
            TextView tv_date;
            TextView tv_name;
            TextView tv_slip_no;
            TextView tv_qty;
        }
    }


    class StoreListAdapter extends BaseAdapter {
        LayoutInflater mInflater;

        public StoreListAdapter() {
            mInflater = LayoutInflater.from(mContext);
        }

        public int getItemCount() {
            return (null == mMorList ? 0 : mMorList.size());
        }

        public void addData(MorListModel.Items item) {
            if (mMorList == null) mMorList = new ArrayList<>();
            mMorList.add(item);
        }

        public void clearData() {
            mMorList.clear();
        }

        public List<MorListModel.Items> getData() {
            return mMorList;
        }

        @Override
        public int getCount() {
            if (mMorList == null) {
                return 0;
            }

            return mMorList.size();
        }

        @Override
        public MorListModel.Items getItem(int position) {
            return mMorList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            if (v == null) {
                holder = new ViewHolder();
                v = mInflater.inflate(R.layout.cell_mor_list_store, null);

                v.setTag(holder);

                holder.tv_date = v.findViewById(R.id.tv_date);
                holder.tv_name = v.findViewById(R.id.tv_name);
                holder.tv_qty = v.findViewById(R.id.tv_qty);

            } else {
                holder = (ViewHolder) v.getTag();
            }

            final MorListModel.Items data = mMorList.get(position);
            holder.tv_date.setText(data.getOrd_date());
            holder.tv_name.setText(data.getCst_name());
            holder.tv_qty.setText(Integer.toString(data.getMor_qty())); //인트


            store_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    List<MorListModel.Items> itms = mAdapter.getData();

                    MorListModel.Items o = mmorlistmodel.getItems().get(position);
                    Intent intent = new Intent(mContext, BaseActivity.class);
                    intent.putExtra("menu", Define.MENU_PRODUCTION_DETAIL);
                    Bundle args = new Bundle();

                    args.putString("TYPE", gubun.getText().toString());               //회원: C, 대리점: A
                    args.putString("GUBUN", o.getSlip_type());                        //전표타입: 주문: O, AS: A
                    args.putString("SLIPNO", o.getSlip_no());                         //전표번호
                    args.putString("NAME", o.getCst_name());                          //회원명
                    args.putString("QTY", String.valueOf(o.getMor_qty()));            //수량


                    intent.putExtra("args", args);
                    startActivity(intent);
                }
            });

            /*//특정 데이터시 텍스트 색 변경
            for (int i =0; i<sotreAdapter.getCount(); i++) {
                if (mMorList.get(i).getCst_name() == null) {
                    Log.d("돌아~~", "null");

                } else {
                    Log.d("돌아~~", "있어");
                    if (mMorList.get(i).getCst_name().equals("시티 스포츠(ADS)")) {
                        Log.d("돌아~~", "시티스포츠");
                        Log.d("돌아~~몇번", String.valueOf(i));
                        if (position == i) {
                            holder.tv_name.setTextColor(Color.RED);
                        }
                    }
                }
            }*/

            return v;
        }


        public class ViewHolder {
            TextView tv_date;
            TextView tv_name;
            TextView tv_slip_no;
            TextView tv_qty;
        }


    }

}//Class close