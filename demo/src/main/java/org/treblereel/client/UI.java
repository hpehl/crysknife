package org.treblereel.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.HTMLDivElement;
import org.jboss.gwt.elemento.core.IsElement;
import org.treblereel.client.databinding.Databinding;
import org.treblereel.client.events.BeanWithCDIEvents;
import org.treblereel.gwt.crysknife.annotation.DataField;
import org.treblereel.gwt.crysknife.annotation.Templated;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 12/5/19
 */
@Singleton
@Templated(value = "ui.html")
public class UI implements IsElement<HTMLDivElement> {

    @Inject
    @DataField
    protected NamedBeanFieldInjectionPanel namedBeanFieldInjectionPanel;

    @Inject
    @DataField
    protected NamedBeanConstructorInjectionPanel namedBeanConstructorInjectionPanel;

    @Inject
    @DataField
    protected SingletonBeans singletonBeans;

    @Inject
    @DataField
    protected DependentBeans dependentBeans;

    @Inject
    @DataField
    protected TransitiveInjection transitiveInjection;

    @Inject
    @DataField
    protected BeanWithCDIEvents beanWithCDIEvents;

    @Inject
    @DataField
    protected Databinding databinding;

    @PostConstruct
    public void init() {

    }
}
