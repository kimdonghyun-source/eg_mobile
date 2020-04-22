package kr.co.ajcc.wms.menu.print;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import androidx.annotation.Nullable;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.menu.custom.BaseCompatActivity;
import kr.co.ajcc.wms.menu.main.MainActivity;

public class ConfigActivity extends BaseCompatActivity {
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_print);


        //프린트등록 버튼
        findViewById(R.id.btn_printAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });

        //뒤로가기 버튼
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, MainActivity.class));
            }
        });



    }
}
