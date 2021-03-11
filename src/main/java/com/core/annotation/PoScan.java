package com.core.annotation;

import com.core.annotation.registarar.PoScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({PoScannerRegistrar.class})
public @interface PoScan {

	String[] value() default {};

}
