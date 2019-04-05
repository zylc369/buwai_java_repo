package buwai.commons.db.jdbc.connect;

import buwai.commons.db.jdbc.ConnectParam;

import java.security.InvalidParameterException;

/**
 * 数据库连接接口
 *
 * @author 不歪
 * @version 创建时间：2019-04-05 10:32
 */
public interface IConnect {

    /**
     * 数据库连接参数校验
     *
     * @param connectParam 连接参数
     * @return 校验成功，则返回null；校验失败，则返回异常详情
     * @throws InvalidParameterException 如果参数校验失败，可能抛出该异常
     */
    String connectParamVerify(ConnectParam connectParam) throws InvalidParameterException;

}
