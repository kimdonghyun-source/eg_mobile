package kr.co.bang.wms.common;

public class Define {

    //주문자재출고(뱅)
    public static final int MENU_PRODUCTION_IN = 2;
    //주문자재출고 상세(뱅)
    public static final int MENU_PRODUCTION_DETAIL = 21;
    //주문자재출고 피킹(뱅)
    public static final int MENU_PRODUCTION_OUT = 22;
    //창고이동(뱅)
    public static final int MENU_HOUSE_MOVE = 3;
    //재고실사(뱅)
    public static final int MENU_INVENTORY = 4;

    //--------------------------------------------------------------------------------
    public static final int MENU_MATERIAL_OUT  = 5;
    //입고등록
    public static final int MENU_REGISTRATION = 9;
    //로케이션 이동
    public static final int MENU_LOCATION = 1;
    //자재불출 피킹
    public static final int MENU_MATERIAL_PICKING = 31;
    //제품출고
    public static final int MENU_PRODUCT_OUT = 6;
    //제품피킹
    public static final int MENU_PRODUCT_PICKING = 61;
    //파렛트관리
    public static final int MENU_PALLET = 7;
    //파렛트프린터출력
    public static final int MENU_PALLET_PRINTER = 71;
    //프린터설정
    public static final int MENU_CONFIG = 8;


    public static final int MENU_INVENTORY_PICKING = 91;

    //--------------------------------------------------------------------------------
    //주문자재출고(뱅)
    public static final String TAG_PRODUCTION_IN = "production";
    //주문자재출고 상세(뱅)
    public static final String TAG_PRODUCTION_DETAIL = "production_detail";
    //주문자재출고 피킹(뱅)
    public static final String TAG_PRODUCTION_OUT = "production_out";
    //창고이동 (뱅)
    public static final String TAG_HOUSE_MOVE = "house_move";

    //--------------------------------------------------------------------------------

    public static final String TAG_MATERIAL_OUT  = "material";

    //입고등록
    public static final String TAG_REGISTRATION = "registration";
    //로케이션 이동
    public static final String TAG_LOCATION = "location";

    //자재불출피킹
    public static final String TAG_MATERIAL_PICKING = "material_picking";

    //제품출고
    public static final String TAG_PRODUCT_OUT = "product";
    //제품출고피킹
    public static final String TAG_PRODUCT_PICKING = "product_picking";
    //파렛트관리
    public static final String TAG_PALLET = "pallet";
    //파렛트프린터출력
    public static final String TAG_PALLET_PRINTER = "pallet_printer";
    //프린터설정
    public static final String TAG_CONFIG = "config";
    //재고실사
    public static final String TAG_INVENTORY = "invertory";
}
