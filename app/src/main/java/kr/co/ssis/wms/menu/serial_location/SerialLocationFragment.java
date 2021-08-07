package kr.co.ssis.wms.menu.serial_location;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.honeywell.aidc.BarcodeReadEvent;

import java.util.List;


import kr.co.siss.wms.R;
import kr.co.ssis.wms.common.Utils;
import kr.co.ssis.wms.custom.CommonFragment;
import kr.co.ssis.wms.honeywell.AidcReader;
import kr.co.ssis.wms.model.ResultModel;
import kr.co.ssis.wms.model.SerialLocationModel;
import kr.co.ssis.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SerialLocationFragment extends CommonFragment {
    Context mContext;
    EditText et_from;
    TextView tv_empty;
    RecyclerView serialLocation_listView;
    String barcode_scan, beg_barcode = null;

    SerialLocationModel mSerialModel;
    List<SerialLocationModel.Item> mSerialList;
    SerialLocationAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();

    }//Colse onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_serial_location, container, false);
        et_from = v.findViewById(R.id.et_from);
        tv_empty = v.findViewById(R.id.tv_empty);
        serialLocation_listView = v.findViewById(R.id.serialLocation_listView);
        serialLocation_listView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new SerialLocationAdapter(getActivity());
        serialLocation_listView.setAdapter(mAdapter);

        return v;
    }//onCreateView Close


    @Override
    public void onResume() {
        super.onResume();
        AidcReader.getInstance().claim(mContext);
        AidcReader.getInstance().setListenerHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BarcodeReadEvent event = (BarcodeReadEvent) msg.obj;
                    barcode_scan = event.getBarcodeData();
                    tv_empty.setVisibility(View.GONE);

                    moveaskScan();

                    beg_barcode = barcode_scan;
                    et_from.setText(barcode_scan);
                }
            }
        });


    }//Close onResume


    /**
     * 시리얼위치조회 시리얼 스캔
     */
    private void moveaskScan() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<SerialLocationModel> call = service.serialLocatonScan("sp_pda_serial_loc_scan", barcode_scan);

        call.enqueue(new Callback<SerialLocationModel>() {
            @Override
            public void onResponse(Call<SerialLocationModel> call, Response<SerialLocationModel> response) {
                if (response.isSuccessful()) {
                    mSerialModel = response.body();
                    final SerialLocationModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mSerialModel != null) {

                        if (mSerialModel.getFlag() == ResultModel.SUCCESS) {
                            mSerialList = mSerialModel.getItems();
                            if (model.getItems().size() > 0) {
                                mAdapter.clearData();
                                SerialLocationModel.Item item = (SerialLocationModel.Item) model.getItems().get(0);
                                mAdapter.addData(item);
                                mAdapter.notifyDataSetChanged();

                            }

                        } else {
                            Utils.Toast(mContext, model.getMSG());
                            if (mSerialList != null) {
                                mSerialList.clear();
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else {
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code() + " : " + response.message());
                }
            }


            @Override
            public void onFailure(Call<SerialLocationModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });

    }//Close Barcode Serial Scan


}//Close Fragment
