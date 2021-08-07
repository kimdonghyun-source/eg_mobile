package kr.co.ssis.wms.menu.house_new_move;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import kr.co.ssis.wms.common.Define;
import kr.co.ssis.wms.common.Utils;
import kr.co.ssis.wms.custom.CommonFragment;
import kr.co.ssis.wms.menu.main.BaseActivity;
import kr.co.ssis.wms.model.MatOutListModel;
import kr.co.ssis.wms.model.ResultModel;
import kr.co.ssis.wms.network.ApiClientService;
import kr.co.siss.wms.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HouseNewMoveFragment extends CommonFragment {

    Context mContext;
    TextView item_date;
    Button btn_search;
    DatePickerDialog.OnDateSetListener callbackMethod;
    ListAdapter mAdapter;
    ListView move_new_listView;
    Handler mHandler;
    MatOutListModel moveModel;
    List<MatOutListModel.Item> moveList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

    }//Close onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_house_new_move, container, false);
        item_date = v.findViewById(R.id.item_date);
        move_new_listView = v.findViewById(R.id.move_new_listView);
        btn_search = v.findViewById(R.id.btn_search);
        mAdapter = new ListAdapter();
        move_new_listView.setAdapter(mAdapter);
        mHandler = handler;

        item_date.setOnClickListener(onClickListener);
        btn_search.setOnClickListener(onClickListener);


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

        return v;
    }//Close onCreateView

    @Override
    public void onResume() {
        super.onResume();
        MatListSearch();
    }

    private void goMoveDetail(int position){

        Intent intent = new Intent(getActivity(), BaseActivity.class);
        intent.putExtra("menu", Define.MENU_HOUSE_MOVE_DATAIL);
        Bundle extras = new Bundle();
        extras.putSerializable("model", moveModel);
        extras.putSerializable("position", position);
        intent.putExtra("args", extras);

        startActivityForResult(intent, 100);
    }

    View.OnClickListener onClickListener  = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_date :
                    int c_year = Integer.parseInt(item_date.getText().toString().substring(0, 4));
                    int c_month = Integer.parseInt(item_date.getText().toString().substring(5, 7));
                    int c_day = Integer.parseInt(item_date.getText().toString().substring(8, 10));

                    DatePickerDialog dialog = new DatePickerDialog(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, callbackMethod, c_year, c_month - 1, c_day);
                    dialog.show();
                    break;

                case R.id.btn_search :
                    MatListSearch();
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
     * 창고이동 시리얼스캔
     */
    private void MatListSearch() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        String m_date = item_date.getText().toString().replace("-", "");
        Call<MatOutListModel> call = service.matlist("sp_pda_mat_out_list", m_date);

        call.enqueue(new Callback<MatOutListModel>() {
            @Override
            public void onResponse(Call<MatOutListModel> call, Response<MatOutListModel> response) {
                if (response.isSuccessful()) {
                    moveModel = response.body();
                    final MatOutListModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (moveModel != null) {
                        if (moveModel.getFlag() == ResultModel.SUCCESS) {

                            if (model.getItems().size() > 0) {
                                //for (int i = 0; i < model.getItems().size(); i++) {

                                    //MatOutListModel.Item item = (MatOutListModel.Item) model.getItems().get(i);
                                    //mAdapter.addData(item);
                                    moveList = model.getItems();
                                    mAdapter.notifyDataSetChanged();
                                //}


                            }

                        } else {
                            Utils.Toast(mContext, model.getMSG());
                            if (moveList != null){
                                moveList.clear();
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
            public void onFailure(Call<MatOutListModel> call, Throwable t) {
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
            return (null == moveList ? 0 : moveList.size());
        }

        public void addData(MatOutListModel.Item item) {
            if (moveList == null) moveList = new ArrayList<>();
            moveList.add(item);
        }

        public void clearData() {
            moveList.clear();
        }

        public List<MatOutListModel.Item> getData() {
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
        public MatOutListModel.Item getItem(int position) {
            return moveList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ListAdapter.ViewHolder holder;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (v == null) {
                holder = new ListAdapter.ViewHolder();
                v = inflater.inflate(R.layout.cell_out_list, null);

                holder.req_code = v.findViewById(R.id.req_mat_code);
                holder.req_date = v.findViewById(R.id.req_mat_date);
                holder.dpt_name = v.findViewById(R.id.dpt_name);
                holder.emp_name = v.findViewById(R.id.emp_name);


                v.setTag(holder);

            } else {
                holder = (ListAdapter.ViewHolder) v.getTag();
            }

            final MatOutListModel.Item data = moveList.get(position);
            holder.req_code.setText(data.getReq_mat_code());
            holder.req_date.setText(data.getReq_mat_date());
            holder.dpt_name.setText(data.getDpt_name());
            holder.emp_name.setText(data.getEmp_name());

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.obj = data;
                    mHandler.sendMessage(msg);
                    goMoveDetail(position);
                }
            });

            return v;
        }

        public class ViewHolder {
            TextView req_code;
            TextView req_date;
            TextView dpt_name;
            TextView emp_name;

        }


    }











}//Closed Activity
