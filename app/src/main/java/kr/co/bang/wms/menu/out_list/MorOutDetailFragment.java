package kr.co.bang.wms.menu.out_list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.co.bang.wms.R;
import kr.co.bang.wms.common.Define;
import kr.co.bang.wms.common.Utils;
import kr.co.bang.wms.custom.CommonFragment;
import kr.co.bang.wms.menu.main.BaseActivity;
import kr.co.bang.wms.model.MorListModel;
import kr.co.bang.wms.model.ResultModel;
import kr.co.bang.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MorOutDetailFragment extends CommonFragment {

    Context mContext;
    ImageButton bt_item_out;
    TextView mor_qty, cst_name;
    EditText et_merge_1;
    ListView mlistview;
    ListAdapter mAdapter;
    List<MorListModel.Items> mMorList ;
    MorListModel mmorlistmodel;
    Handler mHandler;
    String m_type, s_gubun;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_mor_detail, container, false);
        bt_item_out = v.findViewById(R.id.bt_item_out);
        cst_name = v.findViewById(R.id.cst_name);
        mor_qty = v.findViewById(R.id.mor_qty);
        et_merge_1 = v.findViewById(R.id.et_merge_1);
        mlistview = v.findViewById(R.id.listview);
        mAdapter = new ListAdapter();
        mlistview.setAdapter(mAdapter);
        mHandler = handler;

        //bt_item_out.setOnClickListener(onClickListener);

        Bundle args = getArguments();
        if (args!=null){
           final String TYPE = args.getString("TYPE");      //회원 / 대리점
           final String GUBUN = args.getString("GUBUN");    //전표타입
           final String SLIPNO = args.getString("SLIPNO");
           final String NAME = args.getString("NAME");
           final String QTY = args.getString("QTY");

            mor_qty.setText(QTY);       //주문수량
            cst_name.setText(NAME);     //거래처명
            et_merge_1.setText(SLIPNO); //전표번호
            s_gubun = GUBUN;        //전표타입(회원) O=주문, A=AS
            m_type = TYPE;          //회원 / 대리점 구분

            requestMorListDetail();

        }

        return v;
    }//onCreateView Close

    private void requestMorListDetail() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<MorListModel> call = service.mordetail("sp_pda_dis_mor_detail", m_type, et_merge_1.getText().toString(), s_gubun);

        call.enqueue(new Callback<MorListModel>() {
            @Override
            public void onResponse(Call<MorListModel> call, Response<MorListModel> response) {
                if (response.isSuccessful()) {
                    mmorlistmodel = response.body();
                    final MorListModel model = response.body();

                    if (mmorlistmodel != null) {
                        if (mmorlistmodel.getFlag() == ResultModel.SUCCESS) {
                            Utils.Log("model ==> ??:" + new Gson().toJson(mmorlistmodel));
                            mMorList = mmorlistmodel.getItems();
                            mAdapter.notifyDataSetChanged();
                            mlistview.setAdapter(mAdapter);

                        } else {
                            Utils.Toast(mContext, model.getMSG());
                        }
                    }
                } else {
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code() + " : " + response.message());
                }
            }

            @Override
            public void onFailure(Call<MorListModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }



    class ListAdapter extends BaseAdapter {
        LayoutInflater mInflater;
        List<MorListModel.Items> itemsList;

        public ListAdapter() {
            mInflater = LayoutInflater.from(mContext);
        }

        public void addData(MorListModel.Items item) {
            if (mMorList == null) mMorList = new ArrayList<>();
            mMorList.add(item);
        }

        @Override
        public int getCount() {
            if (mMorList == null) {
                return 0;
            }

            return mMorList.size();
        }

        public List<MorListModel.Items> getData(){
            return itemsList;
        }

        @Override
        public MorListModel.Items getItem(int position) {
            return mMorList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            if (v == null) {
                holder = new ViewHolder();
                v = mInflater.inflate(R.layout.cell_mor_detail, null);

                v.setTag(holder);

                holder.itm_name = v.findViewById(R.id.tv_product);
                holder.itm_size = v.findViewById(R.id.tv_size);
                holder.mor_qty = v.findViewById(R.id.tv_qty);
                holder.h_name = v.findViewById(R.id.tv_head);
                holder.mor_h_qty = v.findViewById(R.id.tv_h_count);
                holder.s_name = v.findViewById(R.id.tv_sharft);
                holder.mor_s_qty = v.findViewById(R.id.tv_s_count);


            } else {
                holder = (ListAdapter.ViewHolder) v.getTag();
            }

            final MorListModel.Items data = mMorList.get(position);
            holder.itm_name.setText(data.getItm_name());
            holder.itm_size.setText(data.getItm_size());
            holder.mor_qty.setText(Integer.toString(data.getMor_qty()));
            holder.h_name.setText(data.getH_name());
            holder.mor_h_qty.setText(Integer.toString(data.getMor_h_qty()));
            holder.s_name.setText(data.getS_name());
            holder.mor_s_qty.setText(Integer.toString(data.getMor_s_qty()));


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.obj = data;
                    mHandler.sendMessage(msg);
                    goMorItem();
                }
            });


            return v;
        }

        public class ViewHolder {
            TextView itm_name;
            TextView itm_size;

            //헤드쪽
            TextView h_code;
            TextView h_name;
            TextView h_color_code;
            TextView h_color_name;
            TextView h_loft_code;
            TextView h_loft_name;
            TextView h_direc_code;
            TextView h_direc_name;
            TextView h_head_code;
            TextView h_head_name;
            TextView h_weight_code;
            TextView h_weight_name;


            //샤프트쪽
            TextView s_code;
            TextView s_name;
            TextView s_color_code;
            TextView s_color_name;
            TextView s_strong_code;
            TextView s_strong_name;


            TextView mor_qty;
            TextView mor_h_qty;
            TextView mor_s_qty;



        }
    }

    private void goMorItem(){
        List<MorListModel.Items> itms = mAdapter.getData();
        Intent intent = new Intent(mContext, BaseActivity.class);
        intent.putExtra("menu", Define.MENU_PRODUCTION_OUT);
        Bundle args = new Bundle();
        for (int i = 0; i < mmorlistmodel.getItems().size(); i++){
            MorListModel.Items o = mmorlistmodel.getItems().get(i);

            args.putString("CORPCODE", o.getCorp_code());                            //사업장번호
            args.putString("PRODUCT", o.getItm_name());                              //품묵정보
            args.putString("QTY", String.valueOf(o.getMor_qty()));                   //주문수량
            args.putString("H_COUNT", String.valueOf(o.getMor_h_qty()));             //헤드수량
            args.putString("S_COUNT", String.valueOf(o.getMor_s_qty()));             //샤프트수량
            args.putString("mor_date", o.getMor_date());                             //사업장번호
            args.putString("mor_no1", String.valueOf(o.getMor_no1()));               //자재출고순번
            args.putString("mor_h_qty", String.valueOf(o.getMor_h_qty()));           //헤드요청수량
            args.putString("mor_s_qty", String.valueOf(o.getMor_s_qty()));           //샤프트요청수량
            args.putString("TYPE", m_type);                                          //회원/대리점 구분
            args.putString("GUBUN", s_gubun);                                        //전표타입

            //헤드
            args.putString("HAEDCODE", o.getH_code());                               //헤드코드
            args.putString("HAEDNAME", o.getH_name());                               //헤드명
            args.putString("HEADCOLOR_C", o.getH_color_code());                      //헤드색상코드
            args.putString("HEADCOLOR_NM", o.getH_color_name());                     //헤드색상명
            args.putString("HEADLOFT_C", o.getH_loft_code());                        //헤드각도코드
            args.putString("HEADLOFT_NM", o.getH_loft_name());                       //헤드각도명
            args.putString("HEADDIREC_C", o.getHaed_direc_code());                   //헤드방향코드
            args.putString("HEADDIREC_NM", o.getHaed_direc_name());                  //헤드방향명
            args.putString("HEADHEAD_C", o.getHead_code());                          //헤드헤드코드
            args.putString("HEADHEAD_NM", o.getHead_code_name());                    //헤드헤드명
            args.putString("HEADWEIGHT_C", o.getH_weight_code());                    //헤드무게코드
            args.putString("HEADWEIGHT_NM", o.getH_weight_name());                   //헤드무게명

            //샤프트
            args.putString("SHAFTCODE", o.getS_code());                              //샤프트코드
            args.putString("SHAFTNAME", o.getS_name());                              //샤프트명
            args.putString("SHAFTCOLOR_C", o.getS_color_code());                     //샤프트색상코드
            args.putString("SHAFTCOLOR_NM", o.getS_color_name());                    //샤프트색상명
            args.putString("SHAFTSTRONG_C", o.getS_strong_code());                   //샤프트각도코드
            args.putString("SHAFTSTRONG_NM", o.getS_strong_name());                  //샤프트각도명


        }
        intent.putExtra("args",args);
        startActivity(intent);
    }



}//Class close
