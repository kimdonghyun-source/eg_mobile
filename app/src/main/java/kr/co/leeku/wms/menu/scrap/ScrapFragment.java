package kr.co.leeku.wms.menu.scrap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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
import kr.co.leeku.wms.menu.main.MainActivity;
import kr.co.leeku.wms.menu.osr.OsrFragment;
import kr.co.leeku.wms.menu.popup.TwoBtnPopup;
import kr.co.leeku.wms.menu.remelt.RemeltFragment;
import kr.co.leeku.wms.model.OsrListModel;
import kr.co.leeku.wms.model.ResultModel;
import kr.co.leeku.wms.model.ScrapListModel;
import kr.co.leeku.wms.model.ShipScanModel;
import kr.co.leeku.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScrapFragment extends CommonFragment {

    Context mContext;
    TextView item_date, item_date1;
    Button bt_label, bt_search;
    RecyclerView scrap_listview;
    DatePickerDialog.OnDateSetListener callbackMethod;
    DatePickerDialog.OnDateSetListener callbackMethod1;
    ScrapListModel mScrapModel;
    List<ScrapListModel.Item> mScrapList;
    ListAdapter mAdapter;
    TwoBtnPopup mTwoBtnPopup = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

    }//Close onCreate


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_scrap, container, false);

        item_date = v.findViewById(R.id.item_date);
        item_date1 = v.findViewById(R.id.item_date1);
        bt_label = v.findViewById(R.id.bt_label);
        bt_search = v.findViewById(R.id.bt_search);
        scrap_listview = v.findViewById(R.id.scrap_listview);

        item_date.setOnClickListener(onClickListener);
        item_date1.setOnClickListener(onClickListener);
        bt_label.setOnClickListener(onClickListener);
        bt_search.setOnClickListener(onClickListener);

        scrap_listview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ListAdapter(getActivity());
        scrap_listview.setAdapter(mAdapter);

        int year1 = Integer.parseInt(yearFormat1.format(currentTime));
        int month1 = Integer.parseInt(monthFormat1.format(currentTime));
        int day1 = Integer.parseInt(dayFormat1.format(currentTime));

        String formattedMonth = "" + month1;
        String formattedDayOfMonth = "" + day1;
        if (month1 < 10) {

            formattedMonth = "0" + month1;
        }
        if (day1 < 10) {
            formattedDayOfMonth = "0" + day1;
        }

        item_date.setText(year1 + "-" + formattedMonth + "-" + formattedDayOfMonth);
        item_date1.setText(year1 + "-" + formattedMonth + "-" + formattedDayOfMonth);

        this.InitializeListener();
        this.InitializeListener1();

        mAdapter.setRetHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what >= 0) {
                    ScrapLabelList(msg.what);
                }
            }
        });

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

    public void InitializeListener1() {
        callbackMethod1 = new DatePickerDialog.OnDateSetListener() {
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

    SimpleDateFormat yearFormat1 = new SimpleDateFormat("yyyy", Locale.getDefault());
    SimpleDateFormat dayFormat1 = new SimpleDateFormat("dd", Locale.getDefault());
    SimpleDateFormat monthFormat1 = new SimpleDateFormat("MM", Locale.getDefault());


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

                case R.id.item_date1:
                    int c_year1 = Integer.parseInt(item_date1.getText().toString().substring(0, 4));
                    int c_month1 = Integer.parseInt(item_date1.getText().toString().substring(5, 7));
                    int c_day1 = Integer.parseInt(item_date1.getText().toString().substring(8, 10));

                    DatePickerDialog dialog1 = new DatePickerDialog(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, callbackMethod, c_year1, c_month1 - 1, c_day1);
                    dialog1.show();
                    break;

                case R.id.bt_label:
                    Intent intent = new Intent(getActivity(), BaseActivity.class);
                    intent.putExtra("menu", Define.MENU_LABEL_INSERT);
                    startActivityForResult(intent, 100);
                    break;

                case R.id.bt_search:
                    ScrapListSearch();
                    break;

            }
        }
    };





    private void ScrapLabelList(int position) {
        List<ScrapListModel.Item> datas = new ArrayList<>();

        Intent intent = new Intent(getActivity(), BaseActivity.class);
        intent.putExtra("menu", Define.MENU_SCRAP_DETAIL);

        Bundle extras = new Bundle();
        extras.putSerializable("model", mScrapModel);
        extras.putSerializable("position", position);
        intent.putExtra("args", extras);
        startActivityForResult(intent, 100);
    }


    /**
     * 스크랩재고현황 조회
     */
    private void ScrapListSearch() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        final String m_date = item_date.getText().toString().replace("-", "");
        final String m_date1 = item_date1.getText().toString().replace("-", "");
        Call<ScrapListModel> call = service.ScrapList("sp_api_scrap_list", m_date, m_date1);

        call.enqueue(new Callback<ScrapListModel>() {
            @Override
            public void onResponse(Call<ScrapListModel> call, Response<ScrapListModel> response) {
                if (response.isSuccessful()) {
                    mScrapModel = response.body();
                    final ScrapListModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mScrapModel != null) {
                        if (mScrapModel.getFlag() == ResultModel.SUCCESS) {

                            if (model.getItems().size() > 0) {
                                if (mAdapter.itemsList != null){
                                    mAdapter.clearData();
                                    mAdapter.itemsList.clear();
                                    mAdapter.notifyDataSetChanged();
                                }

                                mScrapList = model.getItems();
                                for (int i = 0; i < model.getItems().size(); i++) {

                                    ScrapListModel.Item item = (ScrapListModel.Item) model.getItems().get(i);
                                    mAdapter.addData(item);

                                }
                                mAdapter.notifyDataSetChanged();
                                scrap_listview.setAdapter(mAdapter);
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
            public void onFailure(Call<ScrapListModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close



    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

        List<ScrapListModel.Item> itemsList;
        Activity mActivity;
        Handler mHandler = null;

        public ListAdapter(Activity context) {
            mActivity = context;
        }

        public void setData(List<ScrapListModel.Item> list) {
            itemsList = list;
        }

        public void clearData() {
            if (itemsList != null) itemsList.clear();
        }

        public void setRetHandler(Handler h) {
            this.mHandler = h;
        }

        public List<ScrapListModel.Item> getData() {
            return itemsList;
        }

        public void addData(ScrapListModel.Item item) {
            if (itemsList == null) itemsList = new ArrayList<>();
            itemsList.add(item);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int z) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_scrap_list, viewGroup, false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final ScrapListModel.Item item = itemsList.get(position);

            holder.tv_no.setText(Integer.toString(position + 1));
            holder.tv_scrap_no.setText(item.getScrap_no());
            holder.tv_scrap_dt.setText(item.getScrap_dt());
            holder.tv_dogum.setText(Integer.toString(item.getDogum()));
            holder.tv_cnt.setText(Integer.toString(item.getCnt()));
            holder.tv_location.setText(item.getLocation());
            holder.tv_cmp_nm.setText(item.getCmp_nm());

        }

        @Override
        public int getItemCount() {
            return (null == itemsList ? 0 : itemsList.size());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_no;
            TextView tv_scrap_no;
            TextView tv_scrap_dt;
            TextView tv_dogum;
            TextView tv_cnt;
            TextView tv_location;
            TextView tv_cmp_nm;

            public ViewHolder(View view) {
                super(view);

                tv_no = view.findViewById(R.id.tv_no);
                tv_scrap_no = view.findViewById(R.id.tv_scrap_no);
                tv_scrap_dt = view.findViewById(R.id.tv_scrap_dt);
                tv_dogum = view.findViewById(R.id.tv_dogum);
                tv_cnt = view.findViewById(R.id.tv_cnt);
                tv_location = view.findViewById(R.id.tv_location);
                tv_cmp_nm = view.findViewById(R.id.tv_cmp_nm);


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


















}//Close Fragment
