package servlets.endpoints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Endpoint {
    enum Method {
        GET, POST, PUT, DELETE, OPTIONS;
    };

    String value();
    Method method() default Method.GET;
}
