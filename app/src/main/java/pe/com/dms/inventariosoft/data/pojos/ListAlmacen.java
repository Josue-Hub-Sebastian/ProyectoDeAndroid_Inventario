package pe.com.dms.inventariosoft.data.pojos;

import java.util.List;

import pe.com.dms.inventariosoft.data.models.Almacen;

/**
 * Created by cwramirezg on 29/11/2017.
 */

public class ListAlmacen {

    private List<Almacen> list;

    public ListAlmacen(List<Almacen> list) {
        this.list = list;
    }

    public List<Almacen> getList() {
        return list;
    }
}
