package com.hxm.books.utils;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * make json and Map/List/Object transform each other
 * Created by hxm on 2016/3/7.
 */
public class JsonConvert {

    /**
     * 将一个对象转成json数组
     *
     * @param o object对象
     * @return json数组
     */
    public String getJsonByJSONArrayFromObject(Object o) {
        JSONArray json = JSONArray.fromObject(o);
        return json.toString();
    }

    /**
     * 将一个对象转成json对象
     *
     * @param o object对象
     * @return json对象
     */
    public String getJsonByBeanFromObject(Object o) {
        JSONObject jsonObj = JSONObject.fromObject(o);
        return jsonObj.toString();
    }

    /**
     * 将一个List转成json数组
     *
     * @param list list对象
     * @return json数组 返回的格式:[{"password":"1234","username":"cxl"}]
     */
    public String getJsonByJSONArrayFromList(List list) {
        JSONArray json = JSONArray.fromObject(list);
        return json.toString();
    }

    /**
     * 将一个List转成json对象
     *
     * @param list list对象
     * @return json对象
     */
    public String getJsonByJSONObjectFromList(List list) {
        JSONObject jsonObj = new JSONObject();
        for (int i = 0; i < list.size(); i++) {
            jsonObj.put(list.get(i).toString(), list.get(i));
        }
        return jsonObj.toString();
    }

    /**
     * 将一个map转成json数组
     *
     * @param map map对象
     * @return json数组
     */
    public String getJsonByJSONArrayFromMap(Map map) {
        JSONArray json = JSONArray.fromObject(map);
        return json.toString();
    }

    /**
     * 将一个map转成json对象
     *
     * @param map
     * @return json对象
     * Map<String,Object> map = new HashMap<String,Object>();
     * map.put("users",users); map.put("u", u);
     */
    public String getJsonByJSONObjectFromMap(Map map) {
        JSONObject json = JSONObject.fromObject(map);
        return json.toString();
    }

    /**
     * 将json对象（只有一组值）转成object
     * {id:'id1',code:'code1',name:'name1'}
     *
     * @param json json字符串
     * @return javabean 对象
     */
    public Object getBeantByJSONObjectFromJson(String json) {
        JSONObject jsonObject = JSONObject.fromObject(json);
        Object object = (Object) JSONObject.toBean(jsonObject);
        return object;
    }

    /**
     * 将json转成map
     * {id:'id1',code:'code1',name:'name1'}
     *
     * @param json json字符串
     * @return map
     */
    public Map<String, Object> getMapByJson(String json) {
        Map<String, Object> map = new HashMap<String, Object>();
        JSONObject object = JSONObject.fromObject(json);
        for (Object k : object.keySet()) {
            Object v = object.get(k);
            map.put(k.toString(), v);
        }
        return map;
    }

    /**
     * 将json转成list
     * [{id:'id1',code:'code1',name:'name1'},{id:'id2',code:'code2',name:'name2'}]
     *
     * @param json json字符串
     * @return list
     */
    public List getListByJSONArrayFromJson(String json) {
        JSONArray array = JSONArray.fromObject(json);
        List list = (List) JSONArray.toList(array);
        return list;
    }
}
