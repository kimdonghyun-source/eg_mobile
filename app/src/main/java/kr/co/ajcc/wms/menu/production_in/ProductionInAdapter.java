package kr.co.ajcc.wms.menu.production_in;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.common.Utils;
import kr.co.ajcc.wms.menu.popup.TwoBtnPopup;
import kr.co.ajcc.wms.model.PalletSnanModel;

public class ProductionInAdapter extends RecyclerView.Adapter<ProductionInAdapter.ViewHolder> {

    List<PalletSnanModel.Items> itemsList;
    Activity mActivity;

    TwoBtnPopup mPopup;

    public ProductionInAdapter(Activity context) {
        mActivity = context;
    }

    public void setData(List<PalletSnanModel.Items> list){
        itemsList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_production_in, viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final PalletSnanModel.Items item = itemsList.get(position);

        holder.tv_serial.setText(item.getItm_code());
        holder.tv_name.setText(item.getItm_name()+"_"+position);
        holder.tv_name.setSelected(true);
        holder.tv_box.setText(item.getPallet_qty());

        holder.bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopup = new TwoBtnPopup(mActivity, item.getItm_code()+" Pallet 적치를 취소하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            itemsList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, itemsList.size());
                            mPopup.hideDialog();
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null == itemsList ? 0 : itemsList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_serial;
        ImageButton bt_delete;
        TextView tv_name;
        TextView tv_box;

        public ViewHolder(View view) {
            super(view);

            tv_serial = view.findViewById(R.id.tv_serial);
            bt_delete = view.findViewById(R.id.bt_delete);
            tv_name = view.findViewById(R.id.tv_name);
            tv_box = view.findViewById(R.id.tv_box);
        }
    }
}
