package kr.co.leeku.wms.menu.ship;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import kr.co.leeku.wms.R;
import kr.co.leeku.wms.common.SharedData;
import kr.co.leeku.wms.common.Utils;
import kr.co.leeku.wms.custom.CommonFragment;
import kr.co.leeku.wms.menu.popup.OneBtnPopup;
import kr.co.leeku.wms.menu.popup.TwoBtnPopup;
import kr.co.leeku.wms.model.ResultModel;
import kr.co.leeku.wms.model.ShipScanData;
import kr.co.leeku.wms.network.ApiClientService;
import kr.co.leeku.wms.network.MyDatabaseHelper;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipChangeFragment extends CommonFragment {

    Context mContext;
    String cst_name, m_date, wh_code, cst_code, deli_code;
    TextView tv_cst_code, tv_edit_scan_cnt, tv_plt_cnt, tv_scan_cnt;
    MyDatabaseHelper myDB;
    ListViewAdapter mAdapter;
    ListView ship_change_listview;
    ImageButton btn_next_ok;
    OneBtnPopup mOneBtnPopup;
    TwoBtnPopup mTwoBtnPopup;
    int c_cnt = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();


    }//Close onCreate

    // 데이터베이스 읽어오기
    public void viewAll() {
        Cursor res = myDB.getAllData();
        if (res.getCount() == 0) {
            Log.d("실패", "데이터를 찾을 수 없습니다.");
            return;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_ship_change, container, false);

        Bundle arguments = getArguments();

        cst_name = arguments.getString("cst_name");
        cst_code = arguments.getString("cst_code");
        wh_code = arguments.getString("wh_code");
        deli_code = arguments.getString("deli_code");
        m_date = arguments.getString("date");

        tv_edit_scan_cnt = v.findViewById(R.id.tv_edit_scan_cnt);
        tv_plt_cnt = v.findViewById(R.id.tv_plt_cnt);
        tv_scan_cnt = v.findViewById(R.id.tv_scan_cnt);
        btn_next_ok = v.findViewById(R.id.btn_next_ok);
        ship_change_listview = v.findViewById(R.id.ship_change_listview);
        tv_cst_code = v.findViewById(R.id.tv_cst_code);
        tv_cst_code.setText(cst_name);

        myDB = new MyDatabaseHelper(mContext);
        Cursor res = myDB.getChangeWgt();
        mAdapter = new ListViewAdapter();
        ship_change_listview.setAdapter(mAdapter);

        while (res.moveToNext()) {
            //, res.getInt(2), res.getString(3), res.getInt(4)
            mAdapter.addItemToList(res.getString(0), res.getInt(1));
        }

        /*tv_plt_cnt.setText(res.getString(0));
        tv_scan_cnt.setText(res.getInt(1));
        tv_edit_scan_cnt.setText(res.getInt(2));*/

        btn_next_ok.setOnClickListener(onClickListener);

        return v;

    }//Close onCreateView

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_next_ok:
                    request_ship_save();
                    break;

            }

        }
    };

    public class ListViewAdapter extends BaseAdapter {

        ArrayList<ShipScanData> list = new ArrayList<ShipScanData>();
        ShipFragment.ShipScanAdapter mShipScanAdapter;

        @Override
        public int getCount() {
            return list.size(); //그냥 배열의 크기를 반환하면 됨
        }

        @Override
        public Object getItem(int i) {
            return list.get(i); //배열에 아이템을 현재 위치값을 넣어 가져옴
        }

        @Override
        public long getItemId(int i) {
            return i; //그냥 위치값을 반환해도 되지만 원한다면 아이템의 num 을 반환해도 된다.
        }


        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            final Context context = viewGroup.getContext();

            //리스트뷰에 아이템이 인플레이트 되어있는지 확인한후
            //아이템이 없다면 아래처럼 아이템 레이아웃을 인플레이트 하고 view객체에 담는다.
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.cell_ship_change, viewGroup, false);
            }

            //이제 아이템에 존재하는 텍스트뷰 객체들을 view객체에서 찾아 가져온다
            TextView tv_plt_no = (TextView) view.findViewById(R.id.tv_plt_no);
            //TextView tv_wgt = (TextView) view.findViewById(R.id.tv_wgt);
            final EditText et_plt_wgt = (EditText) view.findViewById(R.id.et_plt_wgt);
            TextView tv_bar = (TextView) view.findViewById(R.id.tv_bar);
            //TextView tv_cnt_plt = (TextView) view.findViewById(R.id.tv_cnt_plt);

            //현재 포지션에 해당하는 아이템에 글자를 적용하기 위해 list배열에서 객체를 가져온다.
            final ShipScanData listdata = list.get(i);

            //가져온 객체안에 있는 글자들을 각 뷰에 적용한다
            tv_plt_no.setText(listdata.getPltno()); //원래 int형이라 String으로 형 변환
            //tv_wgt.setText(Integer.toString(listdata.getSum_scan_qty()));      //중량합 sum_scan_qty
            et_plt_wgt.setText(Integer.toString(listdata.getWg()));
            tv_bar.setText(listdata.getBarcode());
            //tv_cnt_plt.setText(Integer.toString(listdata.getCnt_plt()));

            et_plt_wgt.addTextChangedListener(new TextWatcher() {
                String result = "";
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().length() > 0 && !s.toString().equals(result)) {     // StackOverflow를 막기위해,
                        result = s.toString();   // 에딧텍스트의 값을 변환하여, result에 저장.

                        int cnt = Utils.stringToInt(result);

                        et_plt_wgt.setText(result);    // 결과 텍스트 셋팅.
                        et_plt_wgt.setSelection(result.length());     // 커서를 제일 끝으로 보냄.

                        boolean isUpdated = myDB.updateData(listdata.getPltno(),
                                Integer.parseInt(et_plt_wgt.getText().toString()));

                        /*for (int i=0; i<mAdapter.getCount(); i++){
                            c_cnt += listdata.getScan_qty();
                        }*/
                        //tv_edit_scan_cnt.setText(Integer.toString(c_cnt));

                    }

                }
            });

            return view;
        }

        public void clearData() {
            if (list != null) list.clear();
        }

        //ArrayList로 선언된 list 변수에 목록을 채워주기 위함 다른방식으로 구현해도 됨
        public void addItemToList(String plt, int wg) {
            ShipScanData listdata = new ShipScanData();

            listdata.setPltno(plt);
            listdata.setWg(wg);
            /*listdata.setSum_scan_qty(wg_tot);
            listdata.setBarcode(bar);
            listdata.setCnt_plt(cnt_plt);*/
            //listdata.setFgname(fgnm);

            //값들의 조립이 완성된 listdata객체 한개를 list배열에 추가
            list.add(listdata);

        }
    }//Close Adapter


    /**
     * 출하등록
     */
    private void request_ship_save() {
        int cnt = 0, c_cnt = 0;
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        JsonObject json = new JsonObject();
        //로그인 ID
        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");
        String fac_code = (String) SharedData.getSharedData(mContext, SharedData.UserValue.FAC_CODE.name(), "");
        //List<ShipListModel.ShipItem> items = (List<ShipListModel.ShipItem>) mShipModel.getItems();

        Cursor res = myDB.getwg();
        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            cnt++;
            if (cnt == res.getCount()) {
                buffer.append(res.getString(0) + ";" + res.getString(1));
            } else {
                buffer.append(res.getString(0) + ";" + res.getString(1) + ";");
            }


        }

        Cursor res1 = myDB.getbarplt();
        //Cursor res1 = myDB.getplt();
        StringBuffer buffer1 = new StringBuffer();

        while (res1.moveToNext()) {
            c_cnt ++;
            if (c_cnt == res1.getCount()){
                buffer1.append(res1.getString(0) + ";" + res1.getString(1));
                //buffer.append("s_barcode: " + res.getString(0));
            }else {
                buffer1.append(res1.getString(0) + ";" + res1.getString(1) + ";");
                //buffer.append("s_barcode: " + res.getString(0) + ";");
            }
        }

        JsonObject obj = new JsonObject();
        JsonArray list = new JsonArray();

        obj.addProperty("p_ship_date", m_date);                    //일자
        obj.addProperty("p_fac_code", fac_code);                   //공장코드
        obj.addProperty("p_ship_no", "");                    //ship_no
        obj.addProperty("p_wh_code", wh_code);                     //창고코드
        obj.addProperty("p_cst_code", cst_code);                   //거래처코드
        obj.addProperty("p_deli_place", deli_code);                 //deli_place
        obj.addProperty("p_plt_wgt", buffer.toString());           //중량
        obj.addProperty("p_lbl_list", buffer1.toString());         //바코드+PLTNO
        obj.addProperty("p_user_id", userID);    //로그인ID

        list.add(obj);
        json.add("detail", list);

        Utils.Log("new Gson().toJson(json) ==> : " + new Gson().toJson(json));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(json));

        Call<ResultModel> call = service.postShipSave(body);

        call.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                if (response.isSuccessful()) {
                    ResultModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {

                            mOneBtnPopup = new OneBtnPopup(getActivity(), "출하등록 되었습니다.", R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        getActivity().finish();
                                        deleteDatas();
                                        btn_next_ok.setEnabled(true);
                                    }
                                }
                            });

                        } else {
                            mOneBtnPopup = new OneBtnPopup(getActivity(), model.getMSG(), R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        mOneBtnPopup.hideDialog();
                                        btn_next_ok.setEnabled(true);

                                    }
                                }
                            });
                        }
                    }
                } else {
                    Utils.LogLine(response.message());

                    mTwoBtnPopup = new TwoBtnPopup(getActivity(), "출하등록을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                request_ship_save();
                                mTwoBtnPopup.hideDialog();
                                btn_next_ok.setEnabled(true);

                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResultModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                mTwoBtnPopup = new TwoBtnPopup(getActivity(), "출하등록을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            request_ship_save();
                            mTwoBtnPopup.hideDialog();
                            btn_next_ok.setEnabled(true);

                        }
                    }
                });
            }
        });

    }//Close

    @Override
    public void onDestroy() {
        super.onDestroy();
        deleteDatas();
    }

    // 데이터베이스 삭제하기
    public void deleteDatas() {
        Integer deleteRows = myDB.deleteDatas();
        if (deleteRows > 0)
            //Toast.makeText(mContext,"데이터 삭제 성공",Toast.LENGTH_LONG ).show();
            Log.d("삭제여부", "OK!");
        else
            //Toast.makeText(mContext,"데이터 삭제 실패", Toast.LENGTH_LONG ).show();
            Log.d("삭제여부?", "NO!");
    }


}
