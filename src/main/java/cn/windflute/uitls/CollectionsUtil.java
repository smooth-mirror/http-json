package cn.windflute.uitls;

import com.alibaba.fastjson.JSONArray;

import java.util.*;

public class CollectionsUtil {

    private CollectionsUtil() {
        //nothing
    }

    public static Set<String> transLongListToStringSet(List<Long> sourceList){
        Set<String> resultSet = new HashSet<>();
        if(isNotEmpty(sourceList)){
            for(Long value : sourceList){
                resultSet.add(value.toString());
            }
        }
        return resultSet;
    }
    /**
     * map的key转化为列表
     * @param map
     * @return
     */
    public static List transMapKeyToList(Map map){
        if(map==null||map.size()==0){
            return Collections.emptyList();
        }
        List resultList = new ArrayList<>();
        for (Object key : map.keySet()) {
            resultList.add(key);
        }
        return resultList;
    }
    /**
     * map的Value转化为列表
     * @param map
     * @return
     */
    public static List transMapValueToList(Map map){
        if(map==null||map.size()==0){
            return Collections.emptyList();
        }
        List resultList = new ArrayList<>();
        for (Object value : map.values()) {
            resultList.add(value);
        }
        return resultList;
    }
    public static  Map  transListMapToMap(List<Map> listMap,String keyName,String valueName){
        if(isEmpty(listMap)) {
            return null;
        }
        Map map = new HashMap(40);
        for (Map temp : listMap) {
            map.put(temp.get(keyName), temp.get(valueName));
        }
        return map;
    }

    public static List<String> districtListString(List<String> sourceList) {
        Set<String> resultSet = new HashSet<>();
        List<String> list=null;
        if(isNotEmpty(sourceList)){
            resultSet.addAll(sourceList);
            list=new ArrayList<>();
            list.addAll(resultSet);
        }
        return list;

    }
    /**
     * 请重写toString方法，根据toString结果去重的
     * @param sourceList
     * @param <T>
     * @return
     */
    public static <T> List<T> districtListByToString(List<T> sourceList) {
        if(isEmpty(sourceList)){
            return Collections.emptyList();
        }
        Map<String,T> map=new HashMap<>(40);
        for(T obj:sourceList){
            map.put(obj.toString(),obj);
        }
        List<T> targetList=new ArrayList<>();
        targetList.addAll(map.values());
        return targetList;
    }
    public static List<String> transStringToList(String ids, String separate) {
        if(null==ids||"".equals(ids)){
            return Collections.emptyList();
        }
        List<String> list=new ArrayList<>();
        String[] idArr=ids.split(separate);
        Collections.addAll(list,idArr);
        return list;
    }
    public static List<Long> transStringToLongList(String ids, String separate) {
        if(null==ids||"".equals(ids)){
            return Collections.emptyList();
        }
        List<Long> list=new ArrayList<>();
        String[] idArr=ids.split(separate);
        for(String num:idArr){
            list.add(Long.parseLong(num));
        }
        return list;
    }
    public static void setStrConcatMap(Map<String,String>  map,String key,String value,String separate){
        String str=null;
        if(null!=value&&!value.isEmpty()){
            str = map.get(key);
            if (str == null) {
                str = value;
            } else {
                str = str + separate + value;
            }
            map.put(key, str);
        }
    }
    /**
     * 把list转换成separate分隔符隔开的，使用open和end括起来每个元素
     * @param ids
     * @param separate
     * @param open
     * @param end
     * @return
     */
    public static String transListToString(List<String> ids, String separate,String open,String end) {
        if(isEmpty(ids)){
            return null;
        }
        StringBuilder stringBuffer=new StringBuilder();
        for(String id:ids){
            stringBuffer.append(open).append(id).append(end).append(separate);
        }
        return stringBuffer.substring(0,stringBuffer.lastIndexOf(separate));
    }
    public static String transListToString(List<String> ids, String separate) {
        if(isEmpty(ids)){
            return null;
        }
        StringBuilder stringBuffer=new StringBuilder();
        for(String id:ids){
            stringBuffer.append(id).append(separate);
        }
        return stringBuffer.substring(0,stringBuffer.lastIndexOf(separate));
    }
    public static String transString(Collection<String> ids, String separate) {
        if(isEmpty(ids)){
            return null;
        }
        StringBuilder stringBuffer=new StringBuilder();
        for(String id:ids){
            stringBuffer.append(id).append(separate);
        }
        return stringBuffer.substring(0,stringBuffer.lastIndexOf(separate));
    }
    public static <T> T getMapValue(Map<String,Object> map,String key,Class<T> clazz) {
        Object value=map.get(key);
        return transForcedType(value,clazz);
    }

    /**
     * 强制类型转换，主要处理数字类型转换
     * @param value
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T transForcedType(Object value,Class<T> clazz) {
        if(null==value){
            return null;
        }
        if(value instanceof Number) {
            if (clazz == Integer.class) {
                value = Integer.parseInt(value.toString());
            }
            if (clazz == Double.class) {
                value = Double.parseDouble(value.toString());
            }
            if (clazz == Float.class) {
                value = Float.parseFloat(value.toString());
            }
            if (clazz == Long.class) {
                value = Long.parseLong(value.toString());
            }
            if (clazz == Byte.class) {
                value = Byte.parseByte(value.toString());
            }
            if (clazz == Short.class) {
                value = Short.parseShort(value.toString());
            }
        }
        return (T)value;
    }
    /**
     * 判断数组是否相等
     * @param a1
     * @param a2
     * @return
     */
    public static boolean arrayContentsEq(Object[] a1, Object[] a2) {
        if (a1 == null) {
            return a2 == null || a2.length == 0;
        }

        if (a2 == null) {
            return a1.length == 0;
        }

        if (a1.length != a2.length) {
            return false;
        }

        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != a2[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 删除map的key
     * @param map
     * @param ingoreFileds
     */
    public static void removeMapKey(Map<String,Object> map,String[] ingoreFileds){
        if(map==null||map.size()==0){
            return;
        }
        Set<String> ingoreFiledsSet=new HashSet<>(4);
        for(String filed:ingoreFileds){
            ingoreFiledsSet.add(filed);
        }
        String key=null;
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            if (ingoreFiledsSet.contains(key)) {
                iterator.remove();
                map.remove(key);
            }
        }
    }
    public static boolean isEmpty(Collection list){
        return list==null||list.isEmpty();
    }
    public static boolean isNotEmpty(Collection list){
        return !isEmpty(list);
    }

    /**
     * 截取列表，左闭右开
     * @param srcList
     * @param start 开始位置，0开始
     * @param size  截取长度
     * @param isFillNull 长度不够是否填满空
     * @param <T>
     * @return
     */
    public static <T> List<T> subList(List<T> srcList,int start,int size,boolean isFillNull){
        if(isEmpty(srcList)||start<0||size<=0){
            return Collections.emptyList();
        }
        int length=srcList.size();
        if(start>=length){
            return Collections.emptyList();
        }
        int leftLen=length-start;
        List<T> subList=new ArrayList<>();
        int diffSize=size-leftLen;
        if(diffSize<=0){
            for(int i=start;i<start+size;i++){
                subList.add(srcList.get(i));
            }
            return subList;
        }
        if(diffSize>0){
            for(int i=start;i<length;i++){
                subList.add(srcList.get(i));
            }
        }
        if(isFillNull){
            for(int i=0;i<diffSize;i++){
                subList.add(null);
            }
        }
        return subList;
    }

  /**
   * 字符串list转成字符串Map
   * @param list
   * @return
   */
    public static Map<String,String> transStringList2Map(List<String> list) {
        if(isEmpty(list)){
            return null;
        }
        Map<String,String> map=new HashMap<>(40);
        for(String str:list){
            map.put(str,str);
        }
        return map;
    }

  /**
   * 利用阿里fastjson复制列表
   * @param list
   * @param clazz
   * @param <T>
   * @return
   */
    public static <T> List<T> copyList(List list,Class<T> clazz) {
        if(isEmpty(list)) {
            return Collections.emptyList();
        }
        String jsons= JSONArray.toJSONString(list);
        if(null!=jsons){
            return JSONArray.parseArray(jsons,clazz);
        }
        return Collections.emptyList();
    }
}