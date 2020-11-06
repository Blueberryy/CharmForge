package svenhjol.charm.base.iface;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Module {
    boolean alwaysEnabled() default false;
    boolean enabledByDefault() default true;
    boolean hasSubscriptions() default false;
    String description() default "";
    String mod() default "";
}
