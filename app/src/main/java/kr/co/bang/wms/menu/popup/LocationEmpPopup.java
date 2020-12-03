package kr.co.bang.wms.menu.popup;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.co.bang.wms.R;
import kr.co.bang.wms.common.Utils;
import kr.co.bang.wms.model.EmpModel;
import kr.co.bang.wms.model.LocationModel;
import kr.co.bang.wms.model.MorEmpModel;
import kr.co.bang.wms.model.ResultModel;
import kr.co.bang.wms.model.WarehouseModel;
import kr.co.bang.wms.network.ApiClientService;
import kr.co.bang.wms.spinner.SpinnerPopupAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationEmpPopup {
    Activity mActivity;

    Dialog dialog;
    List<EmpModel.Items> mEmpList;
    Handler mHandler;
    ListView mListView;
    ListAdapter mAdapter;

    public LocationEmpPopup(Activity activity, List<EmpModel.Items> list, int title, Handler handler){
        mActivity = activity;
        mEmpList = list;
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

        dialog.setContentView(R.layout.popup_emp_list);

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
        for (EmpModel.Items item : mEmpList)
            list.add(item.getName());

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

        requestEmp();
        dialog.show();

    }


    class ListAdapter extends BaseAdapter {
        LayoutInflater mInflater;

        public ListAdapter() {
            mInflater = LayoutInflater.from(mActivity);
        }

        @Override
        public int getCount() {
            if (mEmpList == null) {
                return 0;
            }

            return mEmpList.size();
        }

        @Override
        public EmpModel.Items getItem(int position) {
            return mEmpList.get(position);
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
                v = mInflater.inflate(R.layout.cell_emp_list, null);
                v.setTag(holder);

                holder.code = v.findViewById(R.id.tv_emp_code);
                holder.name = v.findViewById(R.id.tv_emp_name);

            } else {
                holder = (ViewHolder) v.getTag();
            }

            final EmpModel.Items item = mEmpList.get(position);
            holder.code.setText(item.getCode());
            holder.name.setText(item.getName());

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
            TextView code;
            TextView name;
        }
    }

    /**
     * 로케이션 검색
     */
    private void requestEmp() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<EmpModel> call = service.empList("usp_GetCodeInfo_Table", "EMPPITING_GBN");

        call.enqueue(new Callback<EmpModel>() {
            @Override
            public void onResponse(Call<EmpModel> call, Response<EmpModel> response) {
                if(response.isSuccessful()){
                    EmpModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if(model.getFlag() == ResultModel.SUCCESS) {
                            Log.d("돼?","ㅇㅇ");
                            mEmpList = model.getItems();
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
            public void onFailure(Call<EmpModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mActivity, mActivity.getString(R.string.error_network));
            }
        });
    }

}
