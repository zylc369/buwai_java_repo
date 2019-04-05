package buwai.commons.db.jdbc.connect;

import java.sql.Connection;

/**
 * 数据库连接初始化器
 *
 * @author 不歪
 * @version 创建时间：2019-04-05 10:52
 */
public interface IConnectInitializer {

    /**
     * 连接数据库成功后，做一些初始化操作
     *
     * @param connection 数据库连接对象
     * @return 初始化成功，则返回true；初始化失败，则返回false
     * @throws Exception 允许抛出该异常
     */
    boolean init(Connection connection) throws Exception;

}
