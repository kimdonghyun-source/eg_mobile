package kr.co.ajcc.wms.menu.popup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.menu.common.Util;

public class LocationListPopup {
    Activity mActivity;

    Dialog dialog;
    ArrayList<String> mList;
    Handler mHandler;

    public LocationListPopup(Activity activity, ArrayList<String> list, int title, Handler handler){
        mActivity = activity;
        mList = list;
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
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.popup_location_list);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        //팝업을 맨 위로 올려야 함.
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView iv_title = dialog.findViewById(R.id.iv_title);
        iv_title.setBackgroundResource(title);

        TextView tv_warehouse =  dialog.findViewById(R.id.tv_warehouse);
        tv_warehouse.setText("선택된 창고");

        ListView listView = dialog.findViewById(R.id.list);

        /*View header = activity.getLayoutInflater().inflate(R.layout.header_blank, null, false);
        header.setPadding(0, Util.getDpToPixel(activity, 13.33f), 0, 0);
        listView.addHeaderView(header);
        View footer = activity.getLayoutInflater().inflate(R.layout.header_blank, null, false);
        footer.setPadding(0, Util.getDpToPixel(activity, 6.67f), 0, 0);
        listView.addFooterView(footer);*/

        ListAdapter adapter = new ListAdapter();
        listView.setAdapter(adapter);

        dialog.findViewById(R.id.bt_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.Toast(mActivity, "선택");
            }
        });

        dialog.findViewById(R.id.bt_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.Toast(mActivity, "검색");
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
        public String getItem(int position) {
            return mList.get(position);
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

            final String data = mList.get(position);
            holder.tv_code.setText("code "+(position+1));
            holder.tv_name.setText(data);

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
