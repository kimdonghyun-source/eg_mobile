package kr.co.ssis.wms.menu.inout_search;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.co.siss.wms.R;
import kr.co.ssis.wms.common.Utils;
import kr.co.ssis.wms.custom.CommonFragment;
import kr.co.ssis.wms.menu.popup.LocationDisTypeListPopup;
import kr.co.ssis.wms.menu.popup.LocationItmListPopup;
import kr.co.ssis.wms.menu.popup.LocationItmSearchPopup;
import kr.co.ssis.wms.menu.ship.ShipFragment;
import kr.co.ssis.wms.model.DisTypeModel;
import kr.co.ssis.wms.model.ItmListModel;
import kr.co.ssis.wms.model.LogQtySearchModel;
import kr.co.ssis.wms.model.LogSearchModel;
import kr.co.ssis.wms.model.ResultModel;
import kr.co.ssis.wms.model.ShipListModel;
import kr.co.ssis.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InOutSearchFragment extends CommonFragment {

    Context mContext;
    TextView item_date, item_date1, tv_itm_code, tv_itm_name, tv_itm_size, tv_c_name, tv_qty;
    DatePickerDialog.OnDateSetListener callbackMethod;
    DatePickerDialog.OnDateSetListener callbackMethod1;
    ImageButton bt_from, btn_next;
    LocationItmSearchPopup mlocationItmListPopup;
    ItmListModel.Item mItmModel;
    List<ItmListModel.Item> mItmListModel;
    ItmListModel mItmSearchModel = null;
    String itm_code;
    EditText et_from;
    ListView Out_listView;
    ListAdapter mAdapter;
    LogSearchModel mLogSearchModel;
    List<LogSearchModel.Item> mLogSearchListModel;

    LogQtySearchModel mLogSearchQtyModel;
    List<LogQtySearchModel.Item> mLogSearchQtyListModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();


    }//Close onCreate


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_in_out_search, container, false);

        tv_itm_code = v.findViewById(R.id.tv_itm_code);
        tv_itm_name = v.findViewById(R.id.tv_itm_name);
        tv_itm_size = v.findViewById(R.id.tv_itm_size);
        tv_c_name = v.findViewById(R.id.tv_c_name);
        tv_qty = v.findViewById(R.id.tv_qty);
        item_date = v.findViewById(R.id.item_date);
        item_date1 = v.findViewById(R.id.item_date1);
        bt_from = v.findViewById(R.id.bt_from);
        et_from = v.findViewById(R.id.et_from);
        btn_next = v.findViewById(R.id.btn_next);
        Out_listView = v.findViewById(R.id.Out_listView);

        mAdapter = new ListAdapter();
        Out_listView.setAdapter(mAdapter);

        int year1 = Integer.parseInt(yearFormat.format(currentTime));
        int month1 = Integer.parseInt(monthFormat.format(currentTime));
        int day1 = Integer.parseInt(dayFormat.format(currentTime));

        int year = Integer.parseInt(yearFormat1.format(currentTime));
        int month = Integer.parseInt(monthFormat1.format(currentTime));
        int day = Integer.parseInt(dayFormat1.format(currentTime));

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

        String formattedMonth1 = "" + month;
        String formattedDayOfMonth1 = "" + day;
        if (month < 10) {

            formattedMonth1 = "0" + month;
        }
        if (day < 10) {
            formattedDayOfMonth1 = "0" + day;
        }


        item_date1.setText(year + "-" + formattedMonth1 + "-" + formattedDayOfMonth1);
        this.InitializeListener1();

        item_date.setOnClickListener(onClickListener);
        item_date1.setOnClickListener(onClickListener);
        bt_from.setOnClickListener(onClickListener);
        btn_next.setOnClickListener(onClickListener);



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

    public void InitializeListener1() {
        callbackMethod1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                int month = monthOfYear + 1;
                String formattedMonth = "" + month;
                String formattedDayOfMonth = "" + dayOfMonth;

                if (month < 10) {

                    formattedMonth = "0" + month;
                }
                if (dayOfMonth < 10) {

                    formattedDayOfMonth = "0" + dayOfMonth;
                }

                item_date1.setText(year + "-" + formattedMonth + "-" + formattedDayOfMonth);

            }
        };
    }

    SimpleDateFormat yearFormat1 = new SimpleDateFormat("yyyy", Locale.getDefault());
    SimpleDateFormat dayFormat1 = new SimpleDateFormat("dd", Locale.getDefault());
    SimpleDateFormat monthFormat1 = new SimpleDateFormat("MM", Locale.getDefault());

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
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

                    DatePickerDialog dialog1 = new DatePickerDialog(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, callbackMethod1, c_year1, c_month1 - 1, c_day1);
                    dialog1.show();
                    break;

                case R.id.bt_from:
                    mlocationItmListPopup = new LocationItmSearchPopup(getActivity(), R.drawable.popup_title_searchloc, new Handler(){
                        @Override
                        public void handleMessage(Message msg){
                            if (msg.what ==1 ){
                                mlocationItmListPopup.hideDialog();
                                ItmListModel.Item order = (ItmListModel.Item)msg.obj;
                                tv_itm_code.setText(order.getItm_code());
                                tv_itm_name.setText(order.getItm_name());
                                tv_itm_size.setText(order.getItm_size());
                                tv_c_name.setText(order.getC_name());
                                et_from.setText("[" + order.getItm_code() + "] " + order.getItm_name());
                                bt_from.setSelected(true);

                                requesItmlist(order.getItm_code());
                            }
                        }
                    });
                    break;

                case R.id.btn_next:
                    LogList();
                    LogQtyList();
                    break;

            }
        }
    };


    /**
     * 품목종류
     */
    private void requesItmlist(final String param) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<ItmListModel> call = service.ItmList("sp_pda_itm_list", param);

        call.enqueue(new Callback<ItmListModel>() {
            @Override
            public void onResponse(Call<ItmListModel> call, Response<ItmListModel> response) {
                if (response.isSuccessful()) {
                    ItmListModel model = response.body();
                    //Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            /*ItmListModel.Item o = mItmSearchModel.getItems().get(0);
                            et_from.setText("[" + o.getItm_code() + "] " + o.getItm_name());
                            //mAdapter.notifyDataSetChanged();
                            itm_code = o.getItm_code();
                            mlocationItmListPopup.hideDialog();
                            bt_from.setSelected(true);
                            tv_itm_code.setText(o.getItm_code());
                            tv_itm_name.setText(o.getItm_name());
                            tv_itm_size.setText(o.getItm_size());
                            tv_c_name.setText(o.getC_name());*/


                            mItmListModel = model.getItems();


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
            public void onFailure(Call<ItmListModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 이력조회(수량)
     */
    private void LogQtyList() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        String m_date = item_date.getText().toString().replace("-", "");
        String m_date1 = item_date1.getText().toString().replace("-", "");

        Call<LogQtySearchModel> call = service.LogSearchQtyList("sp_pda_itm_inv_qty", m_date, m_date1, tv_itm_code.getText().toString());

        call.enqueue(new Callback<LogQtySearchModel>() {
            @Override
            public void onResponse(Call<LogQtySearchModel> call, Response<LogQtySearchModel> response) {
                if (response.isSuccessful()) {
                    mLogSearchQtyModel = response.body();
                    final LogQtySearchModel model = response.body();
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {

                            if (model.getItems().size() > 0) {
                                tv_qty.setText(Integer.toString(mLogSearchQtyModel.getItems().get(0).getInv_qty()));
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
            public void onFailure(Call<LogQtySearchModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 이력조회(이월, 입출)
     */
    private void LogList() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        String m_date = item_date.getText().toString().replace("-", "");
        String m_date1 = item_date1.getText().toString().replace("-", "");

        Call<LogSearchModel> call = service.LogSearchList("sp_pda_itm_his_list", m_date, m_date1, tv_itm_code.getText().toString());

        call.enqueue(new Callback<LogSearchModel>() {
            @Override
            public void onResponse(Call<LogSearchModel> call, Response<LogSearchModel> response) {
                if (response.isSuccessful()) {
                    mLogSearchModel = response.body();
                    final LogSearchModel model = response.body();
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            if (mAdapter.getCount() > 0) {
                                mAdapter.clearData();
                            }
                            if (model.getItems().size() > 0) {
                                for (int i = 0; i < model.getItems().size(); i++) {
                                    LogSearchModel.Item item = (LogSearchModel.Item) model.getItems().get(i);

                                    mAdapter.addData(item);

                                }
                                mAdapter.notifyDataSetChanged();
                                Out_listView.setAdapter(mAdapter);
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
            public void onFailure(Call<LogSearchModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    class ListAdapter extends BaseAdapter {
        LayoutInflater mInflater;
        //List<MaterialLocAndLotModel.Items> itemsList;
        List<LogSearchModel.Item> itemsList;
        Handler mHandler = null;

        public ListAdapter() {
            mInflater = LayoutInflater.from(mContext);
        }

        public int getItemCount() {
            return (null == mLogSearchListModel ? 0 : mLogSearchListModel.size());
        }

        public void addData(LogSearchModel.Item item) {
            if (mLogSearchListModel == null) mLogSearchListModel = new ArrayList<>();
            mLogSearchListModel.add(item);
        }

        public void clearData() {
            mLogSearchListModel.clear();
        }

        public void setRetHandler(Handler h) {
            this.mHandler = h;
        }

        public List<LogSearchModel.Item> getData() {
            return mLogSearchListModel;
        }

        @Override
        public int getCount() {
            if (mLogSearchListModel == null) {
                return 0;
            }

            return mLogSearchListModel.size();
        }

        public void setData(List<LogSearchModel.Item> item) {
            itemsList = item;
        }


        @Override
        public LogSearchModel.Item getItem(int position) {
            return mLogSearchListModel.get(position);
            //return itemsList.get(position);
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
                v = inflater.inflate(R.layout.cell_inout_search, null);

                //final ShipListModel.ShipItem item = itemsList.get(position);

                holder.tv_date = v.findViewById(R.id.tv_date);
                holder.tv_in_qty = v.findViewById(R.id.tv_in_qty);
                holder.tv_out_qty = v.findViewById(R.id.tv_out_qty);


                v.setTag(holder);

            } else {
                holder = (ViewHolder) v.getTag();
            }


            final LogSearchModel.Item data = mLogSearchListModel.get(position);
            holder.tv_date.setText(data.getItm_date());
            holder.tv_in_qty.setText(Integer.toString(data.getIn_qty()));
            holder.tv_out_qty.setText(Integer.toString(data.getOut_qty()));




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
            TextView tv_date;
            TextView tv_in_qty;
            TextView tv_out_qty;



        }


    }//Close Adapter





}//Close Activity