package buwai.commons.db.jdbc.util;

import buwai.commons.db.jdbc.annotation.Column;
import buwai.commons.db.jdbc.IColumnParser;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据库操作帮助类
 *
 * @author 不歪
 * @version 创建时间：2019-04-05 10:43
 */
public class DBHelper {

    private static final Map<String, Map<String, Field>> CLASS_FIELD_MAP = Collections.synchronizedMap(new HashMap<>());

    /**
     * 结果集合转换为对象列表
     *
     * @param clazz     类
     * @param resultSet 从数据库中查到的结果集
     * @param <T>       泛型
     * @return 返回转换后的列表
     * @throws IllegalAccessException 可能抛出该异常
     * @throws InstantiationException 可能抛出该异常
     * @throws SQLException           可能抛出该异常
     * @throws ClassNotFoundException 可能抛出该异常
     */
    public static <T> List<T> resultSet2ObjectList(Class<T> clazz, ResultSet resultSet) throws IllegalAccessException, InstantiationException, SQLException, ClassNotFoundException {
        List<T> list = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        while (resultSet.next()) {
            list.add(resultSet2Object(clazz, metaData, resultSet));
        }
        return list;
    }

    /**
     * 将一条数据库记录转换为一个对象
     *
     * @param clazz     类
     * @param metaData  元数据
     * @param resultSet 从数据库中查到的结果集
     * @param <T>       泛型
     * @return 返回转换后的对象
     * @throws IllegalAccessException 可能抛出该异常
     * @throws InstantiationException 可能抛出该异常
     * @throws SQLException           可能抛出该异常
     * @throws ClassNotFoundException 可能抛出该异常
     */
    public static <T> T resultSet2Object(Class<T> clazz, ResultSetMetaData metaData, ResultSet resultSet) throws IllegalAccessException, InstantiationException, SQLException, ClassNotFoundException {
        String className = clazz.getName();
        Map<String, Field> map = CLASS_FIELD_MAP.get(className);
        if (null == map) {
            map = Arrays.stream(clazz.getDeclaredFields())
                    .peek(item -> item.setAccessible(true))
                    .filter(item -> null != item.getAnnotation(Column.class))
                    .collect(Collectors.toMap(item -> item.getAnnotation(Column.class).name(), item -> item));
            CLASS_FIELD_MAP.put(className, map);
        }
        T result = clazz.newInstance();

        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            Field field = map.get(columnName);
            Column column = field.getAnnotation(Column.class);
            String parseClassName = column.parser();
            Object value = resultSet.getObject(i);
            if (StringUtils.isBlank(parseClassName)) {
                field.set(result, value);
            } else {
                IColumnParser parser = (IColumnParser) Class.forName(parseClassName).newInstance();
                Object newValue = parser.parse(value);
                field.set(result, newValue);
            }
        }

        return result;
    }

}
