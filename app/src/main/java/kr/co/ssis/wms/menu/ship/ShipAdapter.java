package kr.co.ssis.wms.menu.ship;

import android.app.Activity;
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

import kr.co.siss.wms.R;
import kr.co.ssis.wms.common.Utils;
import kr.co.ssis.wms.model.ShipListModel;
import kr.co.ssis.wms.model.ShipOkModel;

public class ShipAdapter extends RecyclerView.Adapter<ShipAdapter.ViewHolder> {

    List<ShipListModel.ShipItem> itemsList;
    Activity mActivity;
    Handler mHandler = null;

    public ShipAdapter(Activity context) {
        mActivity = context;
    }

    public void setData(List<ShipListModel.ShipItem> list){
        itemsList = list;
    }

    public void clearData(){
        if(itemsList != null)itemsList.clear();
    }

    public void setRetHandler(Handler h){
        this.mHandler = h;
    }

    public List<ShipListModel.ShipItem> getData(){
        return itemsList;
    }

    public void addData(ShipListModel.ShipItem item) {
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

        final ShipListModel.ShipItem item = itemsList.get(position);

        holder.itm_code.setText(item.getItm_code());
        holder.itm_name.setText(item.getItm_name());
        holder.itm_size.setText(item.getItm_size());
        holder.c_name.setText(item.getC_name());
        holder.ship_qty.setText(Integer.toString(item.getShip_qty()));
        holder.tv_no.setText(Integer.toString(item.getShip_no2()));
 /*       if (itemsList.getItems().get(position).getSet_scan_qty() > 0 && mAdapter.getCount() > 0 && mShipModel != null) {
            holder.scan_qty.setText(Integer.toString(mShipModel.getItems().get(position).getSet_scan_qty()));
        }*/

        //enable = false
        float cnt = 0;
        if(item.getItems() != null){
            for (ShipOkModel.Item data : item.getItems()) {
                cnt += data.getWrk_qty();
            }
        }
        holder.scan_qty.setText(Utils.setComma(cnt));
        holder.scan_qty.setEnabled(false);

    }

    @Override
    public int getItemCount() {
        return (null == itemsList ? 0 : itemsList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itm_code;
        TextView itm_name;
        TextView itm_size;
        TextView c_name;
        TextView ship_qty;
        TextView scan_qty;
        TextView tv_no;

        public ViewHolder(View view) {
            super(view);

            itm_code = view.findViewById(R.id.tv_itm_code);
            itm_name = view.findViewById(R.id.tv_itm_name);
            itm_size = view.findViewById(R.id.tv_itm_size);
            c_name = view.findViewById(R.id.tv_c_name);
            ship_qty = view.findViewById(R.id.tv_ship_qty);
            scan_qty = view.findViewById(R.id.tv_scan_qty);
            tv_no = view.findViewById(R.id.tv_no);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg = new Message();
                    msg.obj = itemsList.get(getAdapterPosition());
                    msg.what= getAdapterPosition();
                    mHandler.sendMessage(msg);
                }
            });
        }
    }
}

