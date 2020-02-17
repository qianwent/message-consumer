package com.qwt.message.beanscope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SingletonBean_V2 {

    private static final Logger logger = LoggerFactory.getLogger(SingletonBean_V2.class);

    @Autowired
    private ApplicationContext context;

    public void print() {
        PrototypeBean bean = getFromApplicationContext();
        logger.info("Bean SingletonBean's HashCode : {}",bean.hashCode());
        bean.say();
    }

    /**
     * 每次都从ApplicatonContext中获取新的bean引用
     * @return PrototypeBean instance
     */
    PrototypeBean getFromApplicationContext() {
        return this.context.getBean(PrototypeBean.class);
    }
}
