package buwai.commons.db.jdbc;

import lombok.Data;

/**
 * 连接参数
 *
 * @author 不歪
 * @version 创建时间：2019-04-05 10:30
 */
@Data
public class ConnectParam {

    /**
     * 数据库连接链接
     */
    private String url;

    /**
     * 数据库用户名
     */
    private String user;

    /**
     * 数据库连接密码
     */
    private String password;

}
