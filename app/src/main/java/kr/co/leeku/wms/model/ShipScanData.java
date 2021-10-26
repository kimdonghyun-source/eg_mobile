package kr.co.leeku.wms.model;

public class ShipScanData {


    private String pltno;
    private String barcode;
    private float scan_qty;
    private String fgname;
    private String mac;
    int position;
    private int sum_scan_qty;
    private int cnt_plt;
    private int wg;

    public String getPltno() {
        return pltno;
    }

    public void setPltno(String pltno) {
        this.pltno = pltno;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public float getScan_qty() {
        return scan_qty;
    }

    public void setScan_qty(float scan_qty) {
        this.scan_qty = scan_qty;
    }

    public String getFgname() {
        return fgname;
    }

    public void setFgname(String fgname) {
        this.fgname = fgname;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getSum_scan_qty() {
        return sum_scan_qty;
    }

    public void setSum_scan_qty(int sum_scan_qty) {
        this.sum_scan_qty = sum_scan_qty;
    }

    public int getCnt_plt() {
        return cnt_plt;
    }

    public void setCnt_plt(int cnt_plt) {
        this.cnt_plt = cnt_plt;
    }

    public int getWg() {
        return wg;
    }

    public void setWg(int wg) {
        this.wg = wg;
    }
}
