package kr.co.leeku.wms.menu.main;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

import kr.co.leeku.wms.GlobalApplication;

import kr.co.leeku.wms.R;
import kr.co.leeku.wms.common.Define;
import kr.co.leeku.wms.custom.BusProvider;
import kr.co.leeku.wms.custom.CommonCompatActivity;
import kr.co.leeku.wms.custom.CommonFragment;
import kr.co.leeku.wms.menu.osr.OsrDetailFragment;
import kr.co.leeku.wms.menu.osr.OsrFragment;
import kr.co.leeku.wms.menu.popup.TwoBtnPopup;
import kr.co.leeku.wms.menu.remelt.RemeltFragment;
import kr.co.leeku.wms.menu.ship.ShipChangeFragment;
import kr.co.leeku.wms.menu.ship.ShipFragment;
import kr.co.leeku.wms.menu.ship.ShipOkFragment;
import kr.co.leeku.wms.model.UserInfoModel;


public class BaseActivity extends CommonCompatActivity {
    Context mContext;

    //사이드 메뉴 레이아웃
    DrawerLayout drawer;
    View drawer_layout;
    //사이드 메뉴 리스트
    //DrawerMenu mDrawerMenu;
    //좌측 메뉴 리스트 아답터
    ListAdapter mAdapter;
    //다른 메뉴 이동시 묻는 팝업
    TwoBtnPopup mTwoBtnPopup;
    //메뉴 타이틀
    ImageView iv_title;
    //GNB 배경 이미지(피킹은 햄버거버튼 사용 안하기 때문)
    ImageView iv_gnb;
    ImageButton bt_drawer;
    ImageButton bt_print;
    //선택된 메뉴 postion
    int mSelectMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_base);

        mContext = BaseActivity.this;

        findViewById(R.id.bt_back).setOnClickListener(onClickListener);

        drawer = findViewById(R.id.drawer);
        drawer_layout = findViewById(R.id.drawer_layout);
        bt_drawer = findViewById(R.id.bt_drawer);
        bt_drawer.setOnClickListener(onClickListener);
        bt_print = findViewById(R.id.bt_print);
        bt_print.setOnClickListener(onClickListener);
        findViewById(R.id.bt_close).setOnClickListener(onClickListener);

        ArrayList<String> list = new ArrayList<>();
        list.add("출하등록");
        list.add("외주출고");
        list.add("제품재용해둥록");


        ListView listView = findViewById(R.id.list);
        mAdapter = new ListAdapter();
        mAdapter.setData(list);
        listView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        GlobalApplication application = (GlobalApplication) getApplicationContext();
        UserInfoModel.Items model = application.getUserInfoModel();

        TextView tv_name = findViewById(R.id.tv_name);
//        tv_name.setText(model.getDpt_name()+" "+model.getEmp_name());

        iv_title = findViewById(R.id.iv_title);
        iv_gnb = findViewById(R.id.iv_gnb);

        int menu = getIntent().getIntExtra("menu", 0);

        Bundle args = getIntent().getBundleExtra("args");

        mSelectMenu = menu - 2;

        switch (menu) {


            //출하등록
            case Define.MENU_SHIP: {
                CommonFragment fragment = new ShipFragment();
                fragment.setArguments(args);
                replaceContent(fragment, Define.TAG_SHIP, R.id.fl_content);
                break;
            }

            //출하피킹
            case Define.MENU_SHIP_OK: {
                CommonFragment fragment = new ShipOkFragment();
                fragment.setArguments(args);
                replaceContent(fragment, Define.TAG_SHIP_OK, R.id.fl_content);
                break;
            }

            //출하중량변경
            case Define.MENU_SHIP_CHANGE: {
                CommonFragment fragment = new ShipChangeFragment();
                fragment.setArguments(args);
                replaceContent(fragment, Define.TAG_SHIP_CHANGE, R.id.fl_content);
                break;
            }

            //외주출고
            case Define.MENU_OSR: {
                CommonFragment fragment = new OsrFragment();
                fragment.setArguments(args);
                replaceContent(fragment, Define.TAG_OSR, R.id.fl_content);
                break;
            }

            //외주출고상세
            case Define.MENU_OSR_DETAIL: {
                CommonFragment fragment = new OsrDetailFragment();
                fragment.setArguments(args);
                replaceContent(fragment, Define.TAG_OSR_DETAIL, R.id.fl_content);
                break;
            }

            //제품재용해등록
            case Define.MENU_REMELT: {
                CommonFragment fragment = new RemeltFragment();
                fragment.setArguments(args);
                replaceContent(fragment, Define.TAG_REMELT, R.id.fl_content);
                break;
            }
        }

        setTitleImage(menu);
    }

    private void setTitleImage(int menu) {
        //메뉴별 타이틀 이미지
        int image = 0;
        //자재불출과 제품출고에 있는 피킹 상단엔 좌측메뉴가 노출되지 않기 때문에 분기해야함
        int gnb = R.drawable.titilbar;
        int isDrawer = View.VISIBLE;
        int isLock = DrawerLayout.LOCK_MODE_UNLOCKED;

        //프린터 화면에서만 노출하면 되기 때문에 gone 처리 후 프린터 화면 진입 시 visible
        bt_print.setVisibility(View.GONE);

        switch (menu) {
//---------------------------------------------------------------------------------뱅

            //출하등록
            case Define.MENU_SHIP: {
                image = R.drawable.ssis_title;
                break;
            }

            //출하피킹
            case Define.MENU_SHIP_OK: {
                image = R.drawable.ssis_title1;
                break;
            }

            //출하PLT중량변경확인
            case Define.MENU_SHIP_CHANGE: {
                image = R.drawable.ssis_title3;
                break;
            }

            //외주출고
            case Define.MENU_OSR: {
                image = R.drawable.eg_title2;
                break;
            }

            //외주출고상세
            case Define.MENU_OSR_DETAIL: {
                image = R.drawable.eg_title2;
                break;
            }

            //제품재용해등록
            case Define.MENU_REMELT: {
                image = R.drawable.eg_title4;
                break;
            }

        }

        iv_title.setBackgroundResource(image);
        iv_gnb.setBackgroundResource(gnb);
        bt_drawer.setVisibility(isDrawer);
        drawer.setDrawerLockMode(isLock);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int view = v.getId();
            switch (view) {
                case R.id.bt_back:
                    mTwoBtnPopup = new TwoBtnPopup(BaseActivity.this, "작업 내용이 취소됩니다.\n 뒤로가시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                mTwoBtnPopup.hideDialog();
                                finish();
                            }
                        }
                    });
                    //backPressed();
                    break;
                case R.id.bt_drawer:
                    drawer.openDrawer(drawer_layout);
                    break;
                case R.id.bt_close:
                    drawer.closeDrawers();
                    break;
                case R.id.bt_print:
                    BusProvider.getInstance().post(0);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        mTwoBtnPopup = new TwoBtnPopup(BaseActivity.this, "작업 내용이 취소됩니다.\n 뒤로가시겠습니까?", R.drawable.popup_title_alert, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    mTwoBtnPopup.hideDialog();
                    finish();
                }
            }
        });
    }

    public void backPressed() {
        try {
            //사이드 메뉴가 열려있으면 닫아준다.
            if (drawer.isDrawerOpen(drawer_layout)) {
                drawer.closeDrawers();
                return;
            }

            FragmentManager mFragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
            int count = mFragmentManager.getBackStackEntryCount();

            if (count >= 1) {
                mFragmentManager.popBackStack();
            } else {
                //finish();
            }
        } catch (Exception e) {
            //finish();
        }
    }

    class ListAdapter extends BaseAdapter {
        LayoutInflater mInflater;
        ArrayList<String> mList;

        public void setData(ArrayList<String> list) {
            mList = list;
        }

        public ListAdapter() {
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            if (mList == null) {
                return 0;
            }

            return mList.size();
        }

        @Override
        public String getItem(int position) {
            return mList.get(position);
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
                v = mInflater.inflate(R.layout.cell_drawer, null);
                v.setTag(holder);

                holder.tv_menu = v.findViewById(R.id.tv_menu);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            if (mSelectMenu == position) {
                holder.tv_menu.setSelected(true);
            } else {
                holder.tv_menu.setSelected(false);
            }

            final String menu = mList.get(position);
            holder.tv_menu.setText((position + 1) + ". " + menu);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectMenu == position) {
                        drawer.closeDrawer(drawer_layout);
                        return;
                    }
                    mTwoBtnPopup = new TwoBtnPopup(BaseActivity.this, menu + " 메뉴로 이동하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                //팝업 닫기
                                mTwoBtnPopup.hideDialog();
                                //좌측메뉴 닫기
                                drawer.closeDrawer(drawer_layout);
                                //선택한 메뉴 기억
                                mSelectMenu = position;
                                //0이 없고 입고등록이 삭제되어 +2 해줘야함
                                switch (position + 2) {

                                    //출하등록
                                    case Define.MENU_SHIP: {
                                        CommonFragment fragment = new ShipFragment();
                                        replaceContent(fragment, Define.TAG_SHIP, R.id.fl_content);
                                        break;
                                    }

                                    //출하피킹
                                    case Define.MENU_SHIP_OK: {
                                        CommonFragment fragment = new ShipOkFragment();
                                        replaceContent(fragment, Define.TAG_SHIP_OK, R.id.fl_content);
                                        break;
                                    }

                                    //출하피킹
                                    case Define.MENU_SHIP_CHANGE: {
                                        CommonFragment fragment = new ShipChangeFragment();
                                        replaceContent(fragment, Define.TAG_SHIP_CHANGE, R.id.fl_content);
                                        break;
                                    }

                                    //외주출고
                                    case Define.MENU_OSR: {
                                        CommonFragment fragment = new OsrFragment();
                                        replaceContent(fragment, Define.TAG_OSR, R.id.fl_content);
                                        break;
                                    }

                                    //외주출고상세
                                    case Define.MENU_OSR_DETAIL: {
                                        CommonFragment fragment = new OsrDetailFragment();
                                        replaceContent(fragment, Define.TAG_OSR_DETAIL, R.id.fl_content);
                                        break;
                                    }

                                    //제픔재용해등록
                                    case Define.MENU_REMELT: {
                                        CommonFragment fragment = new RemeltFragment();
                                        replaceContent(fragment, Define.TAG_REMELT, R.id.fl_content);
                                        break;
                                    }

                                }
                                setTitleImage(position + 2);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            });

            return v;
        }

        class ViewHolder {
            TextView tv_menu;
        }
    }
}
