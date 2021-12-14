package kr.co.leeku.wms.menu.osr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.co.leeku.wms.R;
import kr.co.leeku.wms.common.Define;
import kr.co.leeku.wms.common.SharedData;
import kr.co.leeku.wms.common.Utils;
import kr.co.leeku.wms.custom.CommonFragment;
import kr.co.leeku.wms.menu.main.BaseActivity;
import kr.co.leeku.wms.menu.popup.LocationShipWhList;
import kr.co.leeku.wms.model.OsrListModel;
import kr.co.leeku.wms.model.ResultModel;
import kr.co.leeku.wms.model.ShipWhListModel;
import kr.co.leeku.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OsrFragment extends CommonFragment {

    Context mContext;
    DatePickerDialog.OnDateSetListener callbackMethod;
    TextView item_date;
    OsrListModel mOsrModel;
    List<OsrListModel.Item> mOsrList;
    ListAdapter mAdapter;
    RecyclerView osr_listview;
    Button bt_search;
    ImageButton bt_wh;
    EditText et_wh;
    LocationShipWhList mLocationWhListPopup;
    List<ShipWhListModel.Item> mWhList;
    ShipWhListModel.Item mWhModel;
    String wh_code="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();


    }//Close onCreate


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_osr, container, false);

        item_date = v.findViewById(R.id.item_date);
        osr_listview = v.findViewById(R.id.osr_listview);
        bt_search = v.findViewById(R.id.bt_search);
        bt_wh = v.findViewById(R.id.bt_wh);
        et_wh = v.findViewById(R.id.et_wh);

        osr_listview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ListAdapter(getActivity());
        osr_listview.setAdapter(mAdapter);

        item_date.setOnClickListener(onClickListener);
        bt_search.setOnClickListener(onClickListener);
        bt_wh.setOnClickListener(onClickListener);
        et_wh.setOnClickListener(onClickListener);

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

        /*String m_date = item_date.getText().toString().replace("-", "");
        OsrList(m_date);*/


        mAdapter.setRetHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what >= 0) {
                    OsrDetailFragment(msg.what);
                }
            }
        });

        return v;

    }//Close onCreateView

    @Override
    public void onResume() {
        super.onResume();
        wh_code = "";
        String m_date = item_date.getText().toString().replace("-", "");
        if (wh_code.equals("")){
            Utils.Toast(mContext, "출고처를 골라주세요.");
            return;
        }else{
            OsrList(m_date);

        }

    }

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

    private void OsrDetailFragment(int position) {
        List<OsrListModel.Item> datas = new ArrayList<>();
        String m_date = item_date.getText().toString().replace("-", "");
        Intent intent = new Intent(getActivity(), BaseActivity.class);
        intent.putExtra("menu", Define.MENU_OSR_DETAIL);

        mAdapter.clearData();
        mAdapter.itemsList.clear();
        mAdapter.notifyDataSetChanged();
        et_wh.setText("");
        mWhList.clear();


        Bundle extras = new Bundle();
        extras.putSerializable("model", mOsrModel);
        extras.putString("m_date", m_date);
        extras.putString("wh_code", wh_code);
        extras.putSerializable("position", position);
        intent.putExtra("args", extras);
        startActivityForResult(intent, 100);
    }

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

                case R.id.bt_search:
                    String m_date = item_date.getText().toString().replace("-", "");

                    if (wh_code.equals("")){
                        Utils.Toast(mContext, "출고처를 골라주세요.");
                        return;
                    }else{
                        OsrList(m_date);

                    }
                    break;

                case R.id.bt_wh:
                    requestWhlist();
                    break;

                case R.id.et_wh:
                    requestWhlist();
                    break;
            }
        }
    };//Close onClick

    /**
     * 창고리스트
     */
    private void requestWhlist() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        String m_date = item_date.getText().toString().replace("-", "");
        String fac_code = (String) SharedData.getSharedData(mContext, SharedData.UserValue.FAC_CODE.name(), "");
        Call<ShipWhListModel> call = service.WhList1("sp_api_osr_plan_list", "WH_LIST", m_date);

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
                                    String m_date = item_date.getText().toString().replace("-", "");
                                    if (wh_code.equals("")){
                                        Utils.Toast(mContext, "출고처를 골라주세요.");
                                        return;
                                    }else{
                                        OsrList(m_date);

                                    }

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
     * 외주출고 리스트
     */
    private void OsrList(String date) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        String fac_code = (String) SharedData.getSharedData(mContext, SharedData.UserValue.FAC_CODE.name(), "");
        final String m_date = item_date.getText().toString().replace("-", "");
        Call<OsrListModel> call = service.OsrList("sp_api_osr_plan_list", "OOD_DMD_LIST", date);

        call.enqueue(new Callback<OsrListModel>() {
            @Override
            public void onResponse(Call<OsrListModel> call, Response<OsrListModel> response) {
                if (response.isSuccessful()) {
                    mOsrModel = response.body();
                    final OsrListModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mOsrModel != null) {
                        if (mOsrModel.getFlag() == ResultModel.SUCCESS) {

                            if (model.getItems().size() > 0) {
                                if (mAdapter.itemsList != null){
                                    mAdapter.clearData();
                                    mAdapter.itemsList.clear();
                                    mAdapter.notifyDataSetChanged();
                                }

                                mOsrList = model.getItems();
                                for (int i = 0; i < model.getItems().size(); i++) {

                                    OsrListModel.Item item = (OsrListModel.Item) model.getItems().get(i);
                                    mAdapter.addData(item);

                                }
                                mAdapter.notifyDataSetChanged();
                                osr_listview.setAdapter(mAdapter);
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
            public void onFailure(Call<OsrListModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

        List<OsrListModel.Item> itemsList;
        Activity mActivity;
        Handler mHandler = null;

        public ListAdapter(Activity context) {
            mActivity = context;
        }

        public void setData(List<OsrListModel.Item> list) {
            itemsList = list;
        }

        public void clearData() {
            if (itemsList != null) itemsList.clear();
        }

        public void setRetHandler(Handler h) {
            this.mHandler = h;
        }

        public List<OsrListModel.Item> getData() {
            return itemsList;
        }

        public void addData(OsrListModel.Item item) {
            if (itemsList == null) itemsList = new ArrayList<>();
            itemsList.add(item);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int z) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_osr_list, viewGroup, false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final OsrListModel.Item item = itemsList.get(position);

            holder.dvr_type.setText("유형:  "+item.getDvr_type() + "   " + "입고처:  "+item.getOod_name() + "   " + "거래처:  "+item.getCst_name());
            //holder.ood_name.setText("입고처: "+item.getOod_name());
            //holder.cst_name.setText("거래처: "+item.getCst_name());
            holder.fg_name.setText("품명:  "+item.getFg_name()+"   "+ "의뢰중량:  " + Float.toString(item.getDmd_wht()));
            holder.ood_dmd_etr.setText("요구사항:  "+item.getOod_dmd_etr());


        }

        @Override
        public int getItemCount() {
            return (null == itemsList ? 0 : itemsList.size());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView dvr_type;
            TextView fg_name;
            TextView ood_dmd_etr;

            public ViewHolder(View view) {
                super(view);

                dvr_type = view.findViewById(R.id.tv_dvr_type);
                fg_name = view.findViewById(R.id.tv_fg_name);
                ood_dmd_etr = view.findViewById(R.id.tv_ood_dmd_etr);


                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message msg = new Message();
                        msg.obj = itemsList.get(getAdapterPosition());
                        msg.what = getAdapterPosition();
                        mHandler.sendMessage(msg);
                    }
                });
            }
        }
    }//Close Adapter





}//Close Activity
