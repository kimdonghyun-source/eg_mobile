package kr.co.ajcc.wms.menu.material_out;

import android.app.Activity;
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
import kr.co.ajcc.wms.common.Utils;
import kr.co.ajcc.wms.model.MaterialOutDetailModel;

public class MaterialOutAdapter extends RecyclerView.Adapter<MaterialOutAdapter.ViewHolder> {

    List<MaterialOutDetailModel.Items> itemsList;
    Activity mActivity;

    public MaterialOutAdapter(Activity context) {
        mActivity = context;
    }

    public void setData(List<MaterialOutDetailModel.Items> list){
        itemsList = list;
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

        //피킹에서 입력받기 때문에 0으로 셋팅하고 enable = false
        holder.et_count.setText("0");
        holder.et_count.setEnabled(false);

        //흐르는 text 구현을 위해 selected 처리
        holder.tv_product.setSelected(true);

        holder.et_count.addTextChangedListener(new TextWatcher(){
            DecimalFormat df = new DecimalFormat("###,###");
            String result="";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() > 0 && !s.toString().equals(result)) {     // StackOverflow를 막기위해,
                    result = df.format(Utils.stringToFloat(s.toString().replaceAll(",", "")));   // 에딧텍스트의 값을 변환하여, result에 저장.
                    holder.et_count.setText(result);    // 결과 텍스트 셋팅.
                    holder.et_count.setSelection(result.length());     // 커서를 제일 끝으로 보냄.

                    float cnt = Utils.stringToFloat(s.toString());
                    //입력된 수량을 list에 넣어줌
                    itemsList.get(holder.getAdapterPosition()).setReq_qty(cnt);
                }
            }
        });
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
                    Utils.Toast(mActivity, itemsList.get(getAdapterPosition()).getItm_name()+" 피킹 페이지 이동");
                }
            });
        }
    }
}
