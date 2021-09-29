package kr.co.ssis.wms.menu.popup;

import android.app.Activity;
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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import kr.co.siss.wms.R;
import kr.co.ssis.wms.common.Utils;
import kr.co.ssis.wms.model.ItmListModel;
import kr.co.ssis.wms.model.ResultModel;
import kr.co.ssis.wms.model.WhModel;
import kr.co.ssis.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationWhSearchPopup {

    Activity mActivity;
    Dialog dialog;
    //List<CustomerInfoModel.CustomerInfo> mList = null;
    List<WhModel.Item> mList = null;
    Handler mHandler;

    TextView date_edit;
    EditText et_cust = null;
    ListAdapter mAdapter = null;

    public LocationWhSearchPopup(Activity activity, int title, Handler handler) {
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

        dialog.setContentView(R.layout.popup_whlist_search);

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


        dialog.findViewById(R.id.bt_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String date = date_edit.getText().toString();
               /* if(et_cust.getText().length() < 2){
                    Toast.makeText(mActivity,"2자 이상 입력해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }*/
                requestDeliveryOrderList(et_cust.getText().toString());
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



    /**
     * 창고조회
     */
    private void requestDeliveryOrderList(String custNm) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<WhModel> call = service.WhList("sp_pda_mst_wh_list", custNm);

        call.enqueue(new Callback<WhModel>() {
            @Override
            public void onResponse(Call<WhModel> call, Response<WhModel> response) {
                if(response.isSuccessful()){
                    WhModel model = response.body();
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
            public void onFailure(Call<WhModel> call, Throwable t) {
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
        public WhModel.Item getItem(int position){
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            final ViewHolder holder;
            if (v == null) {
                holder = new ViewHolder();
                v = mInflater.inflate(R.layout.cell_pop_location, null);
                v.setTag(holder);

                holder.tv_code = v.findViewById(R.id.tv_code);
                holder.tv_name = v.findViewById(R.id.tv_name);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            final WhModel.Item data = mList.get(position);
            holder.tv_code.setText(data.getWh_code());
            holder.tv_name.setText(data.getWh_name());

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
            TextView tv_code;
            TextView tv_name;
        }
    }
}
