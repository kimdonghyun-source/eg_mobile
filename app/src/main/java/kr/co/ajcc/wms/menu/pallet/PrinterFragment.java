package kr.co.ajcc.wms.menu.pallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.custom.CommonFragment;

public class PrinterFragment extends CommonFragment {
    ImageButton btn_next    = null;
    ImageButton btn_change  = null;
    TextView    tv_pallet_sn= null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_printer, container, false);
        btn_change = v.findViewById(R.id.bt_change);
        btn_next = v.findViewById(R.id.btn_next);

        Bundle args = getArguments();
        if(args!=null){
            final String palletSN = args.getString("SN");
            tv_pallet_sn.setText(palletSN);
            btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
        btn_change.setSelected(true);
        return v;
    }
}
