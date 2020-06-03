package kr.co.ajcc.wms.menu.pallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.custom.CommonFragment;

public class PrinterFragment extends CommonFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_printer, container, false);
        return v;
    }
}
