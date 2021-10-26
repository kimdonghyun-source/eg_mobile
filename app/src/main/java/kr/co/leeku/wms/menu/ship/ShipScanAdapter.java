/*
package kr.co.leeku.wms.menu.ship;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.leeku.wms.R;
import kr.co.leeku.wms.model.ShipScanModel;

public class ShipScanAdapter extends RecyclerView.Adapter<ShipScanAdapter.ViewHolder> {

    ShipScanModel.Item mModel;
    List<ShipScanModel.Item> itemsList;
    Activity mActivity;
    Handler mHandler = null;


    public ShipScanAdapter(Activity context) {
        mActivity = context;
    }

    public void setData(List<ShipScanModel.Item> list) {
        itemsList = list;
    }


    public void clearData() {
        if (itemsList != null) itemsList.clear();
    }

    public void setRetHandler(Handler h) {
        this.mHandler = h;
    }

    public List<ShipScanModel.Item> getData() {
        return itemsList;
    }

    public void addData(ShipScanModel.Item item) {
        if (itemsList == null) itemsList = new ArrayList<>();
        itemsList.add(item);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int z) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_shipok_list, viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ShipScanModel.Item item = itemsList.get(position);
        //포지션, PLTNO, 스캔수량, 바코트NO, 스캔총합
        holder.m_position.setText(Integer.toString(position));
        holder.msg.setText(item.getMsg());
        holder.scan_qty.setText(Float.toString(item.getScan_qty()));


    }

    @Override
    public int getItemCount() {
        return (null == itemsList ? 0 : itemsList.size());
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView stock_qty;     //잔여수량
        TextView scan_qty;      //스캔수량
        TextView msg;           //바코드
        TextView m_position;    //포지션
        TextView m_pltno;       //pltno
        TextView scan_tot_qty;      //스캔총수량

        public ViewHolder(View view) {
            super(view);

            scan_qty = view.findViewById(R.id.tv_plt_no);
            msg = view.findViewById(R.id.tv_barcode);
            m_position = view.findViewById(R.id.tv_scan_qty);


                */
/*view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message msg = new Message();
                        msg.obj = itemsList.get(getAdapterPosition());
                        msg.what = getAdapterPosition();
                        mHandler.sendMessage(msg);
                    }
                });*//*

        }
    }
}*/
