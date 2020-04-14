package kr.co.ajcc.wms.menu.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.menu.custom.BaseCompatActivity;
import kr.co.ajcc.wms.menu.print.ConfigActivity;

public class MainActivity extends BaseCompatActivity {
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        findViewById(R.id.bt_menu_7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ConfigActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

            }
        });
    }
}
