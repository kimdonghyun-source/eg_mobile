package kr.co.bang.wms.menu.config;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import kr.co.bang.wms.R;
import kr.co.bang.wms.common.SharedData;
import kr.co.bang.wms.custom.CommonFragment;
import kr.co.jesoft.jelib.listener.OnJEReaderResponseListener;
import kr.co.jesoft.jelib.tsc.printer.TSCPrinter;

public class ConfigFragment extends CommonFragment {
    Context mContext;

    BluetoothAdapter mBluetoothAdapter;

    ListViewAdapter adapter = null;
    ListView listview = null;
    EditText et_printer = null;
    BluetoothDevice mDevice = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_config, container, false);
        this.listview   = v.findViewById(R.id.listview);
        this.et_printer = v.findViewById(R.id.et_printer);
        String printer = (String)SharedData.getSharedData(mContext, "printer_info","");
        String arr[] = printer.split(" ");
        if(arr!=null && arr.length>=2) {
            et_printer.setText(arr[0]+"["+arr[1]+"]");
        }
        v.findViewById(R.id.btn_printAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDevice!=null) {
                    String str = mDevice.getName() + " " + mDevice.getAddress();
                    SharedData.setSharedData(mContext, "printer_info", str);

                    stopDiscovery();

                    //TSCPrinter.shared(mContext).saveConnectDevice(mDevice.getAddress());
                    TSCPrinter.shared(mContext).connect(mDevice.getAddress(), new OnJEReaderResponseListener() {
                        @Override
                        public void jeReaderDidConnect() {
                            getActivity().finish();
                        }

                        @Override
                        public void jeReaderDataReceived(Object o) {

                        }
                    });
                } else {
                    Toast.makeText(mContext,"프린터를 선택하세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        startDiscovery();

        return v;
    }
    @Override
    public void onDestroy(){
        try {
            stopDiscovery();
        } catch (Exception e){

        }
        super.onDestroy();
    }
    private void stopDiscovery(){
        if(mBluetoothAdapter!=null && mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }
        try {
            mContext.unregisterReceiver(mBluetoothSearchReceiver);
        } catch (Exception e){

        }
    }

    private void startDiscovery(){
        if(mBluetoothAdapter==null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        //블루투스를 지원하지 않으면 null을 리턴한다
        if(mBluetoothAdapter == null){
            Toast.makeText(mContext, "블루투스를 지원하지 않는 단말기 입니다.", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return;
        }

        adapter = new ListViewAdapter(mContext, R.layout.cell_select_code);
        listview.setAdapter(adapter);
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        adapter.addAll(pairedDevices);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mDevice = (BluetoothDevice)adapterView.getItemAtPosition(i);
                et_printer.setText(mDevice.getName()+"["+mDevice.getAddress()+"]");
            }
        });

        adapter.notifyDataSetChanged();

        IntentFilter searchFilter = new IntentFilter();
        searchFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); //BluetoothAdapter.ACTION_DISCOVERY_STARTED : 블루투스 검색 시작
        searchFilter.addAction(BluetoothDevice.ACTION_FOUND); //BluetoothDevice.ACTION_FOUND : 블루투스 디바이스 찾음
        searchFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); //BluetoothAdapter.ACTION_DISCOVERY_FINISHED : 블루투스 검색 종료
        mContext.registerReceiver(mBluetoothSearchReceiver, searchFilter);

        if(!mBluetoothAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity)mContext).startActivityForResult(intent, 100);
        } else {
            if(mBluetoothAdapter.isDiscovering()){
                mBluetoothAdapter.cancelDiscovery();
            }
            mBluetoothAdapter.startDiscovery();
        }
    }
    //블루투스 검색결과 BroadcastReceiver
    BroadcastReceiver mBluetoothSearchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch(action){
                //블루투스 디바이스 검색 종료
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    //dataDevice.clear();
                    Toast.makeText(mContext, "블루투스 검색 시작", Toast.LENGTH_SHORT).show();
                    break;
                //블루투스 디바이스 찾음
                case BluetoothDevice.ACTION_FOUND:
                    //검색한 블루투스 디바이스의 객체를 구한다
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //devices.add(device);
                    adapter.add(device);
                    adapter.notifyDataSetChanged();
                    break;
                //블루투스 디바이스 검색 종료
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Toast.makeText(mContext, "블루투스 검색 종료", Toast.LENGTH_SHORT).show();
                    //btnSearch.setEnabled(true);
                    break;
            }
        }
    };

    class ListViewAdapter extends ArrayAdapter<BluetoothDevice>
    {
        private int mLayoutId = 0;

        public ListViewAdapter(Context context, int resourceId)
        {
            super(context, resourceId);
            mLayoutId = resourceId;
        }

        public View getView(final int position, View convertView, ViewGroup parent)
        {
            View row = convertView;
            final ListViewHolder holder;
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if(row == null){
                row = inflater.inflate(mLayoutId, null);
                holder = new ListViewHolder();

                holder.tv_left = (TextView)row.findViewById(R.id.tv_left);
                holder.tv_right = (TextView)row.findViewById(R.id.tv_right);

                row.setTag(holder);
            }else{
                holder = (ListViewHolder) row.getTag();
            }
            BluetoothDevice device = getItem(position);
            holder.tv_left.setText(device.getName());
            holder.tv_right.setText(device.getAddress());

            return row;
        }
    }

    static class ListViewHolder {
        TextView tv_left = null;
        TextView tv_right = null;
    }

}
