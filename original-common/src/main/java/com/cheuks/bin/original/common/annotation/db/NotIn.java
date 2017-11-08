package com.cheuks.bin.original.common.annotation.db;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * 
 * @Title: original-common
 * @Description: 不包含  ? not in(list)
 * @Company: 
 * @Email: 20796698@qq.com
 * @author cheuk.bin.li
 * @date 2017年11月7日  上午11:11:10
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface NotIn {

}
