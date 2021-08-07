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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.co.siss.wms.R;
import kr.co.ssis.wms.common.Utils;
import kr.co.ssis.wms.model.ResultModel;
import kr.co.ssis.wms.model.ShipCstModel;
import kr.co.ssis.wms.model.WarehouseModel;
import kr.co.ssis.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationCstListPopup {
    Activity mActivity;

    Dialog dialog;
    List<ShipCstModel.Item> mShipCstModel;
    Handler mHandler;
    ListView mListView;
    ListAdapter mAdapter;

    public LocationCstListPopup(Activity activity, List<ShipCstModel.Item> list, int title, Handler handler){
        mActivity = activity;
        mShipCstModel = list;
        mHandler = handler;
        showPopUpDialog(activity, title);
    }

    public void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public boolean isShowDialog(){
        if(dialog != null && dialog.isShowing()){
            return true;
        }else{
            return false;
        }
    }

    private void showPopUpDialog(Activity activity, int title){
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setContentView(R.layout.popup_location_list);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        //팝업을 맨 위로 올려야 함.
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        ImageView iv_title = dialog.findViewById(R.id.iv_title);
        iv_title.setBackgroundResource(title);

        List<String> list = new ArrayList<>();
        for (ShipCstModel.Item item : mShipCstModel)
            list.add(item.getCst_name());


        mListView = dialog.findViewById(R.id.list);
        mAdapter = new ListAdapter();
        mListView.setAdapter(mAdapter);

        dialog.findViewById(R.id.bt_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.Toast(mActivity, "검색");
            }
        });

        dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDialog();
            }
        });

        //requestLocation();
        dialog.show();
    }


    class ListAdapter extends BaseAdapter {
        LayoutInflater mInflater;

        public ListAdapter() {
            mInflater = LayoutInflater.from(mActivity);
        }

        @Override
        public int getCount() {
            if (mShipCstModel == null) {
                return 0;
            }

            return mShipCstModel.size();
        }

        @Override
        public ShipCstModel.Item getItem(int position) {
            return mShipCstModel.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ListAdapter.ViewHolder holder;
            if (v == null) {
                holder = new ListAdapter.ViewHolder();
                v = mInflater.inflate(R.layout.cell_pop_location, null);
                v.setTag(holder);

                holder.tv_code = v.findViewById(R.id.tv_code);
                holder.tv_name = v.findViewById(R.id.tv_name);
            } else {
                holder = (ListAdapter.ViewHolder) v.getTag();
            }

            final ShipCstModel.Item item = mShipCstModel.get(position);
            holder.tv_code.setText(item.getCst_code());
            holder.tv_name.setText(item.getCst_name());

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.obj = item;
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

    /**
     * 거래처 검색
     */
    private void requestLocation() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<ShipCstModel> call = service.shipCstList("sp_pda_ship_cst_list", "");

        call.enqueue(new Callback<ShipCstModel>() {
            @Override
            public void onResponse(Call<ShipCstModel> call, Response<ShipCstModel> response) {
                if(response.isSuccessful()){
                    ShipCstModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if(model.getFlag() == ResultModel.SUCCESS) {
                            mShipCstModel = model.getItems();
                            mAdapter.notifyDataSetChanged();
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
            public void onFailure(Call<ShipCstModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mActivity, mActivity.getString(R.string.error_network));
            }
        });
    }
}
