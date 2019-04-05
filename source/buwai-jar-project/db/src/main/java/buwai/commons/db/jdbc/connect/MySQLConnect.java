package buwai.commons.db.jdbc.connect;

import buwai.commons.db.jdbc.ConnectParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * MySQL数据库连接
 *
 * @author 不歪
 * @version 创建时间：2019-04-05 10:29
 */
@Slf4j
public class MySQLConnect implements IConnect, Closeable {

    private static final String NAME = "com.mysql.cj.jdbc.Driver";

    private Connection connection;

    public MySQLConnect(@Nonnull ConnectParam connectParam,
                        @Nullable IConnectInitializer connectInitializer)
            throws Exception {
        connectParamVerify(connectParam);
        Class.forName(NAME);
        connection = DriverManager.getConnection(connectParam.getUrl(), connectParam.getUser(), connectParam.getPassword());
        if (null != connectInitializer) {
            connectInitializer.init(connection);
        }
    }

    @Override
    public String connectParamVerify(ConnectParam connectParam) throws InvalidParameterException {
        if (null == connectParam) {
            throw new InvalidParameterException("connectParam is null");
        }
        StringBuilder result = new StringBuilder();
        if (StringUtils.isBlank(connectParam.getUrl())) {
            result.append("url is blank;");
        }
        if (StringUtils.isBlank(connectParam.getUser())) {
            result.append("user is blank");
        }
        if (StringUtils.isBlank(connectParam.getPassword())) {
            result.append("password is blank");
        }
        if (result.length() == 0) {
            return null;
        } else {
            throw new InvalidParameterException("connectParam is null");
        }
    }

    @Override
    public void close() throws IOException {
        if (null != this.connection) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                log.error("", e);
            }
        }
    }
}
