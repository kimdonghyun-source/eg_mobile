package kr.co.ajcc.wms.menu.product_out;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.custom.CommonFragment;

public class ProductPickingFragment extends CommonFragment {
    Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_product_picking, container, false);

        return v;
    }
}