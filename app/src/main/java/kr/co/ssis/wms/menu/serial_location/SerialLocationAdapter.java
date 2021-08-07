package kr.co.ssis.wms.menu.serial_location;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


import kr.co.siss.wms.R;
import kr.co.ssis.wms.model.SerialLocationModel;


public class SerialLocationAdapter extends RecyclerView.Adapter<SerialLocationAdapter.ViewHolder> {

    List<SerialLocationModel.Item> itemsList;
    Activity mActivity;
    Handler mHandler = null;

    public SerialLocationAdapter(Activity context) {
        mActivity = context;
        itemsList = new ArrayList<>();
    }

    public void setData(List<SerialLocationModel.Item> item){
        itemsList = item;
    }

    public void addData(SerialLocationModel.Item item) {
        if (itemsList == null) itemsList = new ArrayList<>();
        itemsList.add(item);
    }

    public void clearData(){
        itemsList.clear();
    }

    public void setSumHandler(Handler h){
        this.mHandler = h;
    }

    public List<SerialLocationModel.Item> getData(){
        return itemsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_serial_location_list, viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final SerialLocationModel.Item item = itemsList.get(position);

        holder.itm_name.setText(item.getItm_name());
        holder.wh_name.setText(item.getWh_name());


    }

    @Override
    public int getItemCount() {
        return (null == itemsList ? 0 : itemsList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itm_name;
        TextView wh_name;

        public ViewHolder(View view) {
            super(view);

//            tv_lot = view.findViewById(R.id.tv_lot);
//            tv_count = view.findViewById(R.id.tv_count);
//            tv_loc = view.findViewById(R.id.tv_loc);
//            et_count = view.findViewById(R.id.et_count);


            itm_name = view.findViewById(R.id.tv_itm_name);
            wh_name = view.findViewById(R.id.tv_wh_name);


        }
    }
}
