package kr.co.leeku.wms.menu.ship;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.leeku.wms.R;
import kr.co.leeku.wms.common.Utils;
import kr.co.leeku.wms.custom.CommonFragment;
import kr.co.leeku.wms.model.ShipListModel;
import kr.co.leeku.wms.model.ShipScanData;
import kr.co.leeku.wms.model.ShipScanModel;
import kr.co.leeku.wms.network.DBHelper;
import kr.co.leeku.wms.network.MyDatabaseHelper;
import kr.co.leeku.wms.network.SQLiteControl;
import kr.co.leeku.wms.network.SQLiteHelper;

public class ShipOkFragment extends CommonFragment {
    Context mContext;
    ShipScanModel mShipScanModel = null;
    ShipScanModel.Item order1;

    ShipListModel mShipListModel = null;
    ShipListModel.Item order = null;

    int mPosition = -1, cnt;
    //float count = 0;
    ListView ship_ok_listview;
    String cst_code;

    TextView tv_cst_code, tv_itm_name, tv_sp_qty, tv_scan_tot_qty;
    ImageButton btn_next_ok;

    MyDatabaseHelper myDB;
    SimpleCursorAdapter adapter = null;
    ListViewAdapter mAdapter;
    ShipFragment.ShipScanAdapter mScanAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

    }//Close onCreate


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_ship_ok, container, false);

        Bundle arguments = getArguments();
        mShipListModel = (ShipListModel) arguments.getSerializable("model");
        mShipScanModel = (ShipScanModel) arguments.getSerializable("model1");
        cst_code = arguments.getString("cst_code");
        mPosition = arguments.getInt("position");
        order = mShipListModel.getItems().get(mPosition);

        ship_ok_listview = v.findViewById(R.id.ship_ok_listview);
        tv_cst_code = v.findViewById(R.id.tv_cst_code);
        tv_itm_name = v.findViewById(R.id.tv_itm_name);
        tv_sp_qty = v.findViewById(R.id.tv_sp_qty);
        tv_scan_tot_qty = v.findViewById(R.id.tv_scan_tot_qty);
        btn_next_ok = v.findViewById(R.id.btn_next_ok);

        btn_next_ok.setOnClickListener(onClickListener);

        tv_cst_code.setText(cst_code);
        tv_itm_name.setText(order.getFg_name());
        tv_sp_qty.setText(Float.toString(order.getSp_qty()));
        tv_scan_tot_qty.setText(Float.toString(order.getScan_qty()));

        myDB = new MyDatabaseHelper(mContext);
        //Cursor res = myDB.getAllData();

        view(order.getFg_name());

        mAdapter = new ListViewAdapter();
        ship_ok_listview.setAdapter(mAdapter);

        Cursor res = myDB.getData(order.getFg_name());

        while (res.moveToNext()) {
            mAdapter.addItemToList(res.getString(0), res.getString(1), res.getFloat(2));
        }



        return v;

    }//Close onCreateView

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_next_ok:
                    ArrayList<ShipScanData> datas = new ArrayList<ShipScanData>();
                    float count = 0;

                    for (int i = 0; i < mAdapter.getCount(); i++) {
                        count += mAdapter.list.get(i).getScan_qty();
                    }
                    mShipListModel.getItems().get(mPosition).setScan_qty(count);
                    Intent i = new Intent();
                    i.putExtra("model", mShipListModel);
                    getActivity().setResult(Activity.RESULT_OK, i);
                    getActivity().finish();
                    break;
            }
        }
    };


    // 데이터베이스 읽어오기
    public void viewAll() {
        Cursor res = myDB.getAllData();
        if (res.getCount() == 0) {
            Log.d("실패", "데이터를 찾을 수 없습니다.");
            return;
        }


        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("s_pltno: " + res.getString(0) + ", ");
            buffer.append("s_barcode: " + res.getString(1) + ", ");
            buffer.append("s_scanqty: " + res.getString(2) + ", ");
            buffer.append("s_fgname: " + res.getString(3) + ", ");
            buffer.append("s_mac: " + res.getString(4) + ", ");
            buffer.append("s_position: " + res.getString(5) + "");
        }

    }

    public void view(String fg) {
        Cursor res = myDB.getData(fg);
        if (res.getCount() == 0) {
            return;
        }
    }

    // 데이터베이스 삭제하기
    public void deleteData(String bar) {
        Integer deleteRows = myDB.deleteData(bar);
        if (deleteRows > 0)
            //Toast.makeText(mContext,"데이터 삭제 성공",Toast.LENGTH_LONG ).show();
            Log.d("삭제여부", "OK!");
        else
            //Toast.makeText(mContext,"데이터 삭제 실패", Toast.LENGTH_LONG ).show();
            Log.d("삭제여부?", "NO!");
    }


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
                view = inflater.inflate(R.layout.cell_shipok_list, viewGroup, false);
            }

            //이제 아이템에 존재하는 텍스트뷰 객체들을 view객체에서 찾아 가져온다
            TextView tv_plt_no = (TextView) view.findViewById(R.id.tv_plt_no);
            TextView tv_barcode = (TextView) view.findViewById(R.id.tv_barcode);
            TextView tv_scan_qty = (TextView) view.findViewById(R.id.tv_scan_qty);
            ImageButton bt_delete = (ImageButton) view.findViewById(R.id.bt_delete);

            //현재 포지션에 해당하는 아이템에 글자를 적용하기 위해 list배열에서 객체를 가져온다.
            final ShipScanData listdata = list.get(i);

            //가져온 객체안에 있는 글자들을 각 뷰에 적용한다
            tv_plt_no.setText(listdata.getPltno()); //원래 int형이라 String으로 형 변환
            tv_barcode.setText(listdata.getBarcode());
            tv_scan_qty.setText(Float.toString(listdata.getScan_qty()));
            //tv_barcode.setText(listdata.getFgname());
            //tv_barcode.setText(listdata.getMac());

            bt_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    float count = 0;
                    //mShipScanAdapter.itemsList.remove(listdata.getPosition());
                    //mShipScanModel.getItems().remove(listdata.getPosition());
                    Cursor res = myDB.getData(order.getFg_name());
                    //Integer deleteRows = myDB.deleteData(listdata.getBarcode());
                    deleteData(listdata.getBarcode());
                    mAdapter.clearData();

                    ship_ok_listview.setAdapter(mAdapter);
                    while (res.moveToNext()) {
                        mAdapter.addItemToList(res.getString(0), res.getString(1), res.getInt(2));
                    }
                    mAdapter.notifyDataSetChanged();
                    view(order.getFg_name());

                    if (mAdapter.getCount() == 0) {
                        tv_scan_tot_qty.setText(Utils.setComma(0));
                    }

                    for (int i = 0; i < mAdapter.getCount(); i++) {
                        count += mAdapter.list.get(i).getScan_qty();
                    }
                    tv_scan_tot_qty.setText(Utils.setComma(count));
                    mAdapter.notifyDataSetChanged();

                }
            });

            return view;
        }

        public void clearData() {
            if (list != null) list.clear();
        }

        //ArrayList로 선언된 list 변수에 목록을 채워주기 위함 다른방식으로 구현해도 됨
        public void addItemToList(String plt, String bar, float scanqty) {
            ShipScanData listdata = new ShipScanData();

            listdata.setPltno(plt);
            listdata.setBarcode(bar);
            listdata.setScan_qty(scanqty);
            //listdata.setFgname(fgnm);

            //값들의 조립이 완성된 listdata객체 한개를 list배열에 추가
            list.add(listdata);

        }
    }


}//Close
