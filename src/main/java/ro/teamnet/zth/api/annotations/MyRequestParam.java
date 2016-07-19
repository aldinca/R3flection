package ro.teamnet.zth.api.annotations;

import java.lang.annotation.*;

/**
 * @author andreeaf
 * @since 5/7/2015 12:55 PM
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestParam {
    String name();
}
