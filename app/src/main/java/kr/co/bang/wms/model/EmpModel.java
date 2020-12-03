package kr.co.bang.wms.model;

import java.util.List;

public class EmpModel extends ResultModel{
    List<Items> items;
    public List<Items> getItems(){
        return items;
    }

    public void setItems(List<Items> items){
        this.items = items;
    }

    public class Items extends ResultModel{
        //작업자코드
        String Code;
        //작업자 명
        String Name;

        public String getCode() {
            return Code;
        }

        public void setCode(String code) {
            Code = code;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }
    }


}
