package cn.windflute.uitls;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ReflectUtil {

    private ReflectUtil() {
        //nothing
    }

    public static final String SERIAL_VERSIONUID="serialVersionUID";

    /**
     * 通过对象的所有方法对象列表（包括父类）
     * 获取对象的 DeclaredMethod
     * @param object : 子类对象
     * @param methodName : 父类中的方法名
     * @param parameterTypes : 父类中的方法参数类型
     * @return 父类中的方法对象
     */
    public static Method getDeclaredMethodFromMethods(Object object, String methodName, Class<?> ... parameterTypes){
        List<Method> methodList=getDeclaredMethods(object);
        Method res = null;
        boolean isSuit=false;
        for (Method method:methodList) {

            isSuit=method.getName().equals(methodName)
                    && CollectionsUtil.arrayContentsEq(parameterTypes, method.getParameterTypes())
                    && (res == null
                    || res.getReturnType().isAssignableFrom(method.getReturnType()));
            if(isSuit){
                return method;
            }
            res=method;
        }
        return res;
    }
    /**
     * 循环向上转型, 获取对象的 DeclaredMethod
     * @param object : 子类对象
     * @return 父类中的方法对象列表
     */
    public static List<Method> getDeclaredMethods(Object object){
        return getDeclaredMethods(object.getClass());
    }
    public static List<Method> getDeclaredMethods(Class<?> clazz ){
        Method[] methods = null ;
        List<Method> list=new ArrayList<>();
        for(;clazz != Object.class; clazz = clazz.getSuperclass()) {
            methods = clazz.getDeclaredMethods() ;
            Collections.addAll(list,methods);
        }
        return list;
    }
    /**
     * 直接调用对象方法, 而忽略修饰符(private, protected, default)
     * @param object : 子类对象
     * @param methodName : 父类中的方法名
     * @param parameterTypes : 父类中的方法参数类型
     * @param parameters : 父类中的方法参数
     * @return 父类中方法的执行结果
     */
    public static Object invokeMethod(Object object, String methodName, Class<?> [] parameterTypes,
                                      Object [] parameters) throws InvocationTargetException, IllegalAccessException {
        //根据 对象、方法名和对应的方法参数 通过反射 调用上面的方法获取 Method 对象
        Method method = getDeclaredMethodFromMethods(object, methodName, parameterTypes) ;
        if(null == method) {
            return null;
        }
        //抑制Java对方法进行检查,主要是针对私有方法而言
        method.setAccessible(true) ;
        //调用object 的 method 所代表的方法，其方法的参数是 parameters
        return method.invoke(object, parameters) ;


    }

    /**
     * 通过对象的所有属性对象列表（包括父类）
     * 获取对象的 DeclaredField
     * @param object : 子类对象
     * @param fieldName : 父类中的属性名
     * @return 父类中的属性对象
     */
    public static Field getDeclaredFieldFromFields(Object object, String fieldName){
        List<Field> fieldList=getDeclaredFields(object);
        for(Field field:fieldList){
            if(field.getName().equals(fieldName)){
                return field;
            }
        }
        return null;
    }
    /**
     * 循环向上转型, 获取对象的 DeclaredFields
     * @param object : 子类对象
     * @return 父类中的属性对象
     */
    public static List<Field> getDeclaredFields(Object object){
        Class<?> clazz = object.getClass() ;
        return getDeclaredFields(clazz);
    }
    /**
     * 循环向上转型, 获取对象的 DeclaredFields
     * @param clazz : 子类
     * @return 父类中的属性对象
     */
    public static List<Field> getDeclaredFields(Class<?> clazz){
        Field[] fields = null ;
        List<Field> fieldList=new ArrayList<>();
        for(; clazz != Object.class ; clazz = clazz.getSuperclass()) {
            fields = clazz.getDeclaredFields();
            Collections.addAll(fieldList,fields);
        }
        return fieldList;
    }

    /**
     * 直接设置对象属性值, 忽略 private/protected 修饰符, 也不经过 setter
     * @param object : 子类对象
     * @param fieldName : 父类中的属性名
     * @param value : 将要设置的值
     */
    public static void setFieldValue(Object object, String fieldName, Object value) throws IllegalAccessException {
        //根据 对象和属性名通过反射 调用上面的方法获取 Field对象
        Field field = getDeclaredFieldFromFields(object, fieldName) ;
        if(null==field){
            return;
        }
        //抑制Java对其的检查
        field.setAccessible(true) ;
        //将 object 中 field 所代表的值 设置为 value
        field.set(object, value) ;

    }
    /**
     * 通过反射把Map转成Bean
     * 忽略 private/protected 修饰符, 也不经过 setter
     * @param map
     * @param classz
     */
    public static <T> T transMap2Bean(Map<String, Object> map, Class<T> classz) throws IllegalAccessException, InstantiationException {

        T result=classz.newInstance();
        List<Field> fieldList=getDeclaredFields(classz);
        Object value=null;
        for(Field field:fieldList){
            value=map.get(field.getName());
            if(value!=null){
                //抑制Java对其的检查
                field.setAccessible(true) ;
                //将 object 中 field 所代表的值 设置为 value
                field.set(result, value) ;
            }
        }
        return result;
    }

    /**
     * 直接读取对象的属性值, 忽略 private/protected 修饰符, 也不经过 getter
     * @param object : 子类对象
     * @param fieldName : 父类中的属性名
     * @return : 父类中的属性值
     */
    public static Object getFieldValue(Object object, String fieldName) throws IllegalAccessException {

        //根据 对象和属性名通过反射 调用上面的方法获取 Field对象
        Field field = getDeclaredFieldFromFields(object, fieldName) ;

        return getFieldValue(object,field) ;

    }
    /**
     * 直接读取对象的属性值, 忽略 private/protected 修饰符, 也不经过 getter
     * @param object : 子类对象
     * @param field :  属性
     * @return : 父类中的属性值
     */
    public static Object getFieldValue(Object object, Field field) throws IllegalAccessException {

        if(null==field){
            return null;
        }
        //抑制Java对其的检查
        field.setAccessible(true) ;
        //获取 object 中 field 所代表的属性值
        return field.get(object) ;
    }
    /**
     * 遍历实体类，获取属性名和属性值
     * @param model : 实例对象
     * @param ingoreFileds 需要过滤的属性名
     * @return : 键值对
     */
    public static Map<String,Object> transBean2Map(Object model, String[] ingoreFileds) throws IllegalAccessException {
        Map<String,Object> map =new HashMap<>(16);
        if(model==null){
            return map;
        }

        for (Field field : getDeclaredFields(model)) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(model));
        }

        map.remove(SERIAL_VERSIONUID);
        if(null!=ingoreFileds&&ingoreFileds.length>0) {
            CollectionsUtil.removeMapKey(map, ingoreFileds);
        }
        return map;
    }
    /**
     * 遍历实体类，获取属性名和属性值
     * @param model : 实例对象
     * @return : 键值对
     */
    public static Map<String,Object> transBean2Map(Object model) throws IllegalAccessException {

        return transBean2Map(model,null);
    }
    /**
     * 遍历实体类，获取属性名和属性值,且属性值不为空
     * @param model : 实例对象
     * @return : 键值对
     */
    public static Map getFieldValueMapNotNull(Object model) throws IllegalAccessException {
        Map map =new HashMap(16);
        if(model==null){
            return map;
        }
        Object value=null;

        for (Field field : getDeclaredFields(model)) {
            field.setAccessible(true);
            value=field.get(model);
            if(value!=null){
                map.put(field.getName(),value);
            }
        }

        return map;
    }
    /**
     * 遍历列表对象，去掉属性全为null的对象
     * @param list : 实例对象
     */
    public static void removeNullInList(List list) throws IllegalAccessException {
        boolean isRemove=true;
        Iterator iter = list.iterator();
        Object model =null;
        while(iter.hasNext()){
            model = iter.next();
            if(null!=model) {
                for (Field field :model.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    if (field.get(model) != null&&!SERIAL_VERSIONUID.equals(field.getName())) {
                        isRemove=false;
                        break;
                    }
                }
            }
            if(isRemove) {
                iter.remove();
            }
            isRemove=true;
        }
    }
    /**
     * 根据重写的oStrong方法去掉属性全为null的对象
     * 主要根据等号后面的null字符串过滤
     * @param list : 实例对象
     */
    public static <T> void  removeNullInListByToString(List<T> list,Class<T> clazz) throws IllegalAccessException, InstantiationException {
        boolean isRemove=true;
        Iterator iter = list.iterator();
        Object model =null;
        String objString=null;
        String nullFieldObjStr=clazz.newInstance().toString();
        while(iter.hasNext()){
            model =iter.next();
            if(null!=model) {
                objString=model.toString();
                if(!objString.equals(nullFieldObjStr)){
                    isRemove=false;
                }
            }
            if(isRemove) {
                iter.remove();
            }
            isRemove=true;
        }
    }
    /**
     * 根据对象属性的路径获取属性值
     * @param object    实体对象
     * @param fieldPath 多层嵌套对象的路径，用点隔开各级属性名
     * @return
     */
    public static Object getFieldValueByPath(Object object, String fieldPath) throws IllegalAccessException {
        Field field =null;
        //根据 对象和属性名通过反射 调用上面的方法获取 Field对象
        String[] path=fieldPath.split("\\.");
        for(String fieldName:path){
            field = getDeclaredFieldFromFields(object,fieldName);
            if(null==field){
                return null;
            }
            field.setAccessible(true);
            //获取 object 中 field 所代表的属性值
            object = field.get(object);
        }
        return object;

    }

}