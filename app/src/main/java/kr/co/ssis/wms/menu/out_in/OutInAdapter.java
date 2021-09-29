package kr.co.ssis.wms.menu.out_in;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.siss.wms.R;
import kr.co.ssis.wms.common.Utils;
import kr.co.ssis.wms.menu.popup.OneBtnPopup;
import kr.co.ssis.wms.menu.popup.TwoBtnPopup;
import kr.co.ssis.wms.model.OutInModel;

public class OutInAdapter extends RecyclerView.Adapter<OutInAdapter.ViewHolder> {

    List<OutInModel.Item> itemsList;
    Activity mActivity;
    Handler mHandler = null;
    TwoBtnPopup mPopup;
    OneBtnPopup mOneBtnPopup;
    TwoBtnPopup mTwoBtnPopup;

    public OutInAdapter(Activity context) {
        mActivity = context;
        itemsList = new ArrayList<>();
    }

    public void setRetHandler(Handler h){
        this.mHandler = h;
    }

    public void setData(List<OutInModel.Item> item){
        itemsList = item;
    }

    public void addData(OutInModel.Item item) {
        if (itemsList == null) itemsList = new ArrayList<>();
        itemsList.add(item);
    }

    public void clearData(){
        itemsList.clear();
    }

    public void setSumHandler(Handler h){
        this.mHandler = h;
    }

    public List<OutInModel.Item> getData(){
        return itemsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_out_in, viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final OutInModel.Item item = itemsList.get(position);

        holder.lot_no.setText(item.getLot_no());
        holder.tv_ea.setText(item.getC_name());
        holder.et_qty.setText(Integer.toString(item.getTin_dtl_qty()));

        holder.bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPopup = new TwoBtnPopup(mActivity, item.getLot_no() + " 취소하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
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


        holder.et_qty.addTextChangedListener(new TextWatcher(){
            String result="";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                Log.d("JeLib","---------------------------");
                if(s.toString().length() > 0 && !s.toString().equals(result)) {     // StackOverflow를 막기위해,
                    result = s.toString();   // 에딧텍스트의 값을 변환하여, result에 저장.

                    int cnt = Utils.stringToInt(result);

                  /*  //아이템 수량 초과시
                    if(cnt > item.getInv_qty_out()){
                        Utils.Toast(mActivity, mActivity.getString(R.string.error_location_cnt));
                        holder.et_count.setText("");
                        //cnt = item.getInv_qty_out();
                        //result = String.valueOf((int)item.getInv_qty_out());
                        return;
                    }*/

                    holder.et_qty.setText(result);    // 결과 텍스트 셋팅.
                    holder.et_qty.setSelection(result.length());     // 커서를 제일 끝으로 보냄.

                    //입력된 수량을 list에 넣어줌
                    itemsList.get(holder.getAdapterPosition()).setTin_dtl_qty(cnt);
                }
                   /* if(Utils.isEmpty(s.toString())){
                        itemsList.get(holder.getAdapterPosition()).setInput_qty(0);
                    }*/
                //mHandler.sendEmptyMessage(1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null == itemsList ? 0 : itemsList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView lot_no;
        ImageButton bt_delete;
        TextView tv_ea;
        EditText et_qty;

        public ViewHolder(View view) {
            super(view);

            lot_no = view.findViewById(R.id.tv_lot_no);
            bt_delete = view.findViewById(R.id.bt_delete);
            et_qty = view.findViewById(R.id.et_qty);
            tv_ea = view.findViewById(R.id.tv_ea);

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

