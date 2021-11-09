package kr.co.leeku.wms.menu.ship;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.leeku.wms.R;
import kr.co.leeku.wms.common.Utils;
import kr.co.leeku.wms.model.ShipListModel;

public class ShipAdapter extends RecyclerView.Adapter<ShipAdapter.ViewHolder> {

    List<ShipListModel.Item> itemsList;
    Activity mActivity;
    Handler mHandler = null;

    public ShipAdapter(Activity context) {
        mActivity = context;
    }

    public void setData(List<ShipListModel.Item> list) {
        itemsList = list;
    }

    public void clearData() {
        if (itemsList != null) itemsList.clear();
    }

    public void setRetHandler(Handler h) {
        this.mHandler = h;
    }

    public List<ShipListModel.Item> getData() {
        return itemsList;
    }

    public void addData(ShipListModel.Item item) {
        if (itemsList == null) itemsList = new ArrayList<>();
        itemsList.add(item);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int z) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_ship_list, viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final ShipListModel.Item item = itemsList.get(position);

        holder.tv_name.setText(item.getFg_name());
        holder.et_sp_qty.setText(Float.toString(item.getSp_qty()));
        holder.et_stock_qty.setText(Float.toString(item.getStock_qty()));
        holder.et_scan_qty.setText(Float.toString(item.getScan_qty()));

        for (int i = 0; i < itemsList.size(); i++) {
            if (itemsList.get(position).getScan_qty() > itemsList.get(position).getSp_qty()) {
                holder.et_scan_qty.setBackgroundColor(Color.parseColor("#9bbb59"));
            }
        }

    }

    @Override
    public int getItemCount() {
        return (null == itemsList ? 0 : itemsList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        TextView et_sp_qty;
        TextView et_stock_qty;
        TextView et_scan_qty;
        TextView et_plt_no;
        TextView tv_barcode;
        TextView tv_scan_qty;


        public ViewHolder(View view) {
            super(view);

            tv_name = view.findViewById(R.id.tv_name);
            et_sp_qty = view.findViewById(R.id.et_sp_qty);
            et_stock_qty = view.findViewById(R.id.et_stock_qty);
            et_scan_qty = view.findViewById(R.id.et_scan_qty);


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
}

