package org.treblereel.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLSelectElement;
import org.gwtproject.event.dom.client.ClickEvent;
import org.jboss.gwt.elemento.core.IsElement;
import org.treblereel.client.inject.DependentBean;
import org.treblereel.client.resources.TextResource;
import org.treblereel.gwt.crysknife.annotation.DataField;
import org.treblereel.gwt.crysknife.annotation.EventHandler;
import org.treblereel.gwt.crysknife.annotation.Templated;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Singleton
@Templated(value = "dependentbeans.html")
public class DependentBeans implements IsElement<HTMLDivElement> {

    @DataField
    protected HTMLDivElement form;

    @Inject
    @DataField
    protected HTMLInputElement textBox;

    @Inject
    @DataField
    protected HTMLButtonElement checkBtn;

    @Inject
    protected DependentBean beanOne1Instance;

    @Inject
    protected DependentBean beanOne2Instance;

    @Inject
    protected TextResource textResource;

    @Inject
    public DependentBeans(HTMLSelectElement nativeSelect,
                          HTMLDivElement form,
                          HTMLButtonElement checkBtn) {
        this.form = form;
        this.checkBtn = checkBtn;
    }

    private void setText(String text) {
        textBox.value = text;
        textBox.textContent = text;
    }

    @EventHandler("checkBtn")
    public void onFallbackInputChange(final ClickEvent e) {
        StringBuffer sb = new StringBuffer();
        sb.append("beanOne1Instance random :");
        sb.append(beanOne1Instance.getRandom());
        sb.append(", beanOne2Instance random :");
        sb.append(beanOne2Instance.getRandom());
        sb.append(", ? equal " + (beanOne1Instance.getRandom() == beanOne2Instance.getRandom()));

        setText(sb.toString());
    }

    @PostConstruct
    public void init(){
        DomGlobal.console.log("CREATED ");
    }

    @Override
    public HTMLDivElement getElement() {
        return form;
    }
}