package kr.co.ssis.wms.menu.popup;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;


import kr.co.siss.wms.R;
import kr.co.ssis.wms.common.UtilDate;
import kr.co.ssis.wms.common.Utils;
import kr.co.ssis.wms.model.CustomerInfoModel;
import kr.co.ssis.wms.model.ResultModel;
import kr.co.ssis.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutProductListPopup {

    Activity mActivity;
    Dialog dialog;
    List<CustomerInfoModel.CustomerInfo> mList = null;
    Handler mHandler;

    TextView date_edit;
    EditText et_cust = null;
    ListAdapter mAdapter = null;
    /*
    DatePickerDialog.OnDateSetListener callbackMethod;
    Date currentTime = Calendar.getInstance().getTime();
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
    SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
    SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
*/
    public OutProductListPopup(Activity activity, int title, Handler handler) {
        mActivity = activity;
        mHandler = handler;
        showPopUpDialog(activity, title);

    }



    public void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();

        }
    }

    public boolean isShowDialog() {
        if (dialog != null && dialog.isShowing()) {
            return true;
        } else {
            return false;
        }
    }

    private void showPopUpDialog(Activity activity, int title) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.popup_outproduct_list);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        //팝업을 맨 위로 올려야 함.
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        ImageView iv_title = dialog.findViewById(R.id.iv_title);
        iv_title.setBackgroundResource(title);
        et_cust = dialog.findViewById(R.id.et_cust);

        ListView listView = dialog.findViewById(R.id.list);

        mAdapter = new ListAdapter();
        listView.setAdapter(mAdapter);

        date_edit = dialog.findViewById(R.id.date_edit);
        String date = UtilDate.getDateToString(new Date(System.currentTimeMillis()), "yyyy-MM-dd");
        date_edit.setText(date);

        date_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = date_edit.getText().toString();
                date = date.replace("-","");
                DatePickerDialog dialog = new DatePickerDialog(mActivity, dateListener, Utils.stringToInt(date.replace("-", "").substring(0, 4)), Utils.stringToInt(date.replace("-", "").substring(4, 6))-1, Utils.stringToInt(date.replace("-", "").substring(6, 8)));
                dialog.show();
            }
        });

        dialog.findViewById(R.id.bt_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = date_edit.getText().toString();
                date = date.replace("-","");
                if(et_cust.getText().length() < 2){
                    Toast.makeText(mActivity,"2자 이상 입력해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }
                requestDeliveryOrderList(date,et_cust.getText().toString());
            }
        });

        dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDialog();
            }
        });

        dialog.show();
    }

    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String date = String.format("%04d", year)+"-"+String.format("%02d", monthOfYear+1)+"-"+String.format("%02d", dayOfMonth);
            date_edit.setText(date);
        }
    };

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            CustomerInfoModel.CustomerInfo order = (CustomerInfoModel.CustomerInfo)mAdapter.getItem(position);

            Message msg = mHandler.obtainMessage();
            msg.what = 2;
            msg.obj = order;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /**
     * 출고지시서 상세
     */
    private void requestDeliveryOrderList(String date,String custNm) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<CustomerInfoModel> call = service.postShipReqList("sp_pda_ship_req_list", date,custNm);

        call.enqueue(new Callback<CustomerInfoModel>() {
            @Override
            public void onResponse(Call<CustomerInfoModel> call, Response<CustomerInfoModel> response) {
                if(response.isSuccessful()){
                    CustomerInfoModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if(model.getFlag() == ResultModel.SUCCESS) {
                            if(model.getItems().size() > 0) {
                                mList = model.getItems();
                                mAdapter.notifyDataSetChanged();
                                //mAdapter.setData(model.getItems());
                                //mAdapter.notifyDataSetChanged();
                            }
                        }else{
                            Utils.Toast(mActivity, model.getMSG());
                        }
                    }
                }else{
                    Utils.LogLine(response.message());
                    Utils.Toast(mActivity, response.code()+" : "+response.message());
                }
            }

            @Override
            public void onFailure(Call<CustomerInfoModel> call, Throwable t) {
                Utils.Log(t.getMessage());
                Utils.LogLine(t.getMessage());
                Utils.Toast(mActivity, mActivity.getString(R.string.error_network));
            }
        });
    }

    class ListAdapter extends BaseAdapter {
        LayoutInflater mInflater;

        public ListAdapter() {
            mInflater = LayoutInflater.from(mActivity);
        }

        @Override
        public int getCount() {
            if (mList == null) {
                return 0;
            }
            return mList.size();
        }


        @Override
        public CustomerInfoModel.CustomerInfo getItem(int position){
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            final ListAdapter.ViewHolder holder;
            if (v == null) {
                holder = new ListAdapter.ViewHolder();
                v = mInflater.inflate(R.layout.cell_pop_outlocation, null);
                v.setTag(holder);

                holder.tv_date = v.findViewById(R.id.tv_date);
                holder.tv_name = v.findViewById(R.id.tv_name);
                holder.tv_qty = v.findViewById(R.id.tv_qty);
            } else {
                holder = (ListAdapter.ViewHolder) v.getTag();
            }

            final CustomerInfoModel.CustomerInfo data = mList.get(position);
            String req_car_no = data.getReq_car_no();

            holder.tv_date.setText(data.getReq_car_no());
            holder.tv_name.setText(data.getCst_name());
            holder.tv_qty.setText(data.getBox_qty()+" BOX");

            holder.tv_name.setSelected(true);

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

        class ViewHolder {
            TextView tv_date;
            TextView tv_name;
            TextView tv_qty;
        }
    }
}