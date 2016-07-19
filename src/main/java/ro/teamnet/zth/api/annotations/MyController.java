package ro.teamnet.zth.api.annotations;

import java.lang.annotation.*;

/**
 * Created by Oana.Mihai on 7/11/2016.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyController {
    String urlPath();
}
