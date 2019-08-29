package cn.windflute.http.annotation;

import java.lang.annotation.*;

/**
 * @author zan00
 * @date 2019/8/28.
 */
@Documented
@Inherited
@Target({ ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TamperResistantAnnotation {
    /**
     * 是否转成json
     * @return
     */
    boolean isTransJson() default false;
}
