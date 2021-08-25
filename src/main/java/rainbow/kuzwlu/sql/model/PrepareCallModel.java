package rainbow.kuzwlu.sql.model;

import rainbow.kuzwlu.exception.SqlException;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrepareCallModel {

    private Map<Integer, Integer> outParamterMap = new HashMap<Integer, Integer>();

    private Map<Integer, Object> inParamerMap = new HashMap<Integer, Object>();

    public void putOutParameterMap(Integer index,Integer types) {
        isTypes(types);
        outParamterMap.put(index, types);
    }

    public void putInParameterMap(Integer index, Object object) {
        inParamerMap.put(index, object);
    }

    public Map<Integer, Integer> getOutParameterMap() {
        return outParamterMap;
    }

    public Map<Integer, Object> getInParameterMap(){
        return inParamerMap;
    }

    private void isTypes(Integer types) {
        Class<? extends Types> class1 = Types.class;
        List<Integer> sqlTypesList = new ArrayList<Integer>();
        for (Field field : class1.getDeclaredFields()) {
            field.setAccessible(true);
            Integer i = null;
            try {
                i = field.getInt(field.getName());
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            sqlTypesList.add(i);
        }

        if (!sqlTypesList.contains(types)) {
            throw new SqlException("outParamer 类型必须是java.sql.Types下所匹配的!");
        }
    }

}
