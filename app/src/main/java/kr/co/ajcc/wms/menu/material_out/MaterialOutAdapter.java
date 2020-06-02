package kr.co.ajcc.wms.menu.material_out;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.common.Define;
import kr.co.ajcc.wms.common.Utils;
import kr.co.ajcc.wms.custom.CommonFragment;
import kr.co.ajcc.wms.menu.main.BaseActivity;
import kr.co.ajcc.wms.model.MaterialLocAndLotModel;
import kr.co.ajcc.wms.model.MaterialOutDetailModel;

public class MaterialOutAdapter extends RecyclerView.Adapter<MaterialOutAdapter.ViewHolder> {

    List<MaterialOutDetailModel.Items> itemsList;
    Activity mActivity;
    Handler mHandler = null;

    public MaterialOutAdapter(Activity context) {
        mActivity = context;
    }

    public void setData(List<MaterialOutDetailModel.Items> list){
        itemsList = list;
    }

    public void clearData(){
        if(itemsList != null)itemsList.clear();
    }

    public void setRetHandler(Handler h){
        this.mHandler = h;
    }

    public List<MaterialOutDetailModel.Items> getData(){
        return itemsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_lot_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final MaterialOutDetailModel.Items item = itemsList.get(position);

        holder.tv_product.setText(item.getItm_name());
        holder.tv_standard.setText(item.getItm_size());
        holder.tv_count.setText(Utils.setComma(item.getReq_qty()));

        //enable = false
        float cnt = 0;
        if(item.getItems() != null){
            for (MaterialLocAndLotModel.Items data : item.getItems()) {
                cnt += data.getInput_qty();
            }
        }
        holder.et_count.setText(Utils.setComma(cnt));
        holder.et_count.setEnabled(false);

        //흐르는 text 구현을 위해 selected 처리
        holder.tv_product.setSelected(true);
    }

    @Override
    public int getItemCount() {
        return (null == itemsList ? 0 : itemsList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_product;
        TextView tv_standard;
        EditText et_count;
        TextView tv_count;

        public ViewHolder(View view) {
            super(view);

            tv_product = view.findViewById(R.id.tv_product);
            tv_standard = view.findViewById(R.id.tv_standard);
            et_count = view.findViewById(R.id.et_count);
            tv_count = view.findViewById(R.id.tv_count);

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
