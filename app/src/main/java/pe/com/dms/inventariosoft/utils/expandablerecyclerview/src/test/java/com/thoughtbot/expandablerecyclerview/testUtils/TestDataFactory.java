package pe.com.dms.inventariosoft.utils.expandablerecyclerview.src.test.java.com.thoughtbot.expandablerecyclerview.testUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pe.com.dms.inventariosoft.utils.expandablerecyclerview.src.main.java.com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

public class TestDataFactory {

  public static List<ExpandableGroup> makeGroups() {
    ArrayList list = new ArrayList();
    for (int i = 0; i < 6; i++) {
      List items = Arrays.asList(i + ".0", i + ".1", i + ".2");
      list.add(new ExpandableGroup("Section " + i, items));
    }
    return list;
  }

}
