package kr.co.ajcc.wms.menu.popup;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.common.Utils;
import kr.co.ajcc.wms.spinner.SpinnerPopupAdapter;

public class OutProductListPopup {

    Activity mActivity;
    Dialog dialog;
    ArrayList<String> mList;
    Handler mHandler;
    Spinner mSpinner;
    int mSpinnerSelect = 0;
    TextView date_edit, text_out;

    DatePickerDialog.OnDateSetListener callbackMethod;
    Date currentTime = Calendar.getInstance().getTime();
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
    SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
    SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());

    public OutProductListPopup(Activity activity, ArrayList<String> list, int title, Handler handler) {
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
        WindowManager.LayoutParams wip = window.getAttributes();

        wip.gravity = Gravity.CENTER;
        window.setAttributes(wip);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView iv_title = dialog.findViewById(R.id.iv_title);
        iv_title.setBackgroundResource(title);

        List<String> list = new ArrayList<>();
        list.add("거래처1");
        list.add("거래처2");
        list.add("거래처3");
        list.add("거래처4");
        list.add("거래처5");

        mSpinner = dialog.findViewById(R.id.spinner);

        SpinnerPopupAdapter spinnerAdapter = new SpinnerPopupAdapter(activity, list, mSpinner);
        mSpinner.setAdapter(spinnerAdapter);
        mSpinner.setOnItemSelectedListener(onItemSelectedListener);
        ListView listView = dialog.findViewById(R.id.list);

        ListAdapter adapter = new ListAdapter();
        listView.setAdapter(adapter);


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

        dialog.show();


        dialog.findViewById(R.id.date_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int year = Integer.parseInt(yearFormat.format(currentTime));
                int month = Integer.parseInt(monthFormat.format(currentTime));
                int day = Integer.parseInt(dayFormat.format(currentTime));

                DatePickerDialog dialog = new DatePickerDialog(mActivity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, callbackMethod, year, month - 1, day);
                dialog.show();

            }

        });

        InitializeListener();

    }

    public void InitializeListener() {
        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear1, int dayOfMonth1) {

                int month = monthOfYear1 + 1;
                String formattedMonth = "" + month;
                String formattedDayOfMonth = "" + dayOfMonth1;

                if (month < 10) {

                    formattedMonth = "0" + month;
                }
                if (dayOfMonth1 < 10) {

                    formattedDayOfMonth = "0" + dayOfMonth1;
                }

                Utils.Toast(mActivity, year+"-"+formattedMonth+"-"+formattedDayOfMonth);
                date_edit = dialog.findViewById(R.id.date_edit);
                if (date_edit != null){
                    date_edit.setText(year + "-" + formattedMonth + "-" + formattedDayOfMonth);
                }


            }
        };
    }




    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mSpinnerSelect = position;

            String item = (String) mSpinner.getSelectedItem();
            Utils.Toast(mActivity, item + " 선택");

            String data = item;
            Message msg = mHandler.obtainMessage();
            msg.what = 2;
            msg.obj = data;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

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
        public String getItem(int position){
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

            final String data = mList.get(position);
            holder.tv_date.setText("03/05-0"+(position+1));
            holder.tv_name.setText(data);
            holder.tv_qty.setText("50"+(position+1)+"BOX");

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