package buwai.commons.db.jdbc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库列描述
 *
 * @author 不歪
 * @version 创建时间：2019-03-24 22:24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Column {

    String name();

    String parser() default "";

}
