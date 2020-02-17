package com.qwt.message.beanscope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public abstract class SingletonBean_V3 {

    private static final Logger logger = LoggerFactory.getLogger(SingletonBean_V3.class);

    @Autowired
    private ApplicationContext context;

    public void print() {
        PrototypeBean bean = methodInject();
        logger.info("Bean SingletonBean's HashCode : {}",bean.hashCode());
        bean.say();
    }

    /**
     * abstract
     */
    @Lookup
    protected abstract PrototypeBean methodInject();
}
