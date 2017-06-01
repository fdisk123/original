package com.cheuks.bin.original.rmi.config;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public abstract class AbstractConfig implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	protected final static Map<String, String> PROPERTIES = new ConcurrentSkipListMap<String, String>();

	public abstract AbstractConfig makeConfig(Element element, ParserContext parserContext);

	protected void registerBeanDefinition(ParserContext parserContext, Class<?> bean, String beanName, Map<String, Object> pagams, Object... constructorArgumentValues) {

		ConstructorArgumentValues constructor = new ConstructorArgumentValues();
		if (null != constructorArgumentValues) {
			for (Object arg : constructorArgumentValues)
				constructor.addGenericArgumentValue(arg);
		}
		MutablePropertyValues mutablePropertyValues = new MutablePropertyValues(pagams);
		BeanDefinition beanDefinition = new RootBeanDefinition(bean, constructor, mutablePropertyValues);
		parserContext.getRegistry().registerBeanDefinition(beanName, beanDefinition);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}