package pe.com.dms.inventariosoft.features.queries;


import java.util.List;

import pe.com.dms.inventariosoft.data.pojos.HijoConsulta;
import pe.com.dms.inventariosoft.utils.expandablerecyclerview.src.main.java.com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

/**
 * Created by cwramirezg on 22/11/2017.
 */

public class HeaderConsulta extends ExpandableGroup<HijoConsulta> {

    public HeaderConsulta(String title, List<HijoConsulta> items) {
        super(title, items);
    }

}
