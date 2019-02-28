package javax.enterprise.inject;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * <p>The built-in qualifier type.</p>
 *
 * <p>Every bean has the qualifier <tt>&#064;Any</tt>,
 * even if it does not explicitly declare this qualifier,
 * except for the special
 * {@link javax.enterprise.inject.New &#064;New qualified beans}.</p>
 *
 * <p>Every event has the qualifier <tt>&#064;Any</tt>,
 * even if it was raised without explicitly declaration
 * of this qualifier.</p>
 *
 * <p>The <tt>&#064;Any</tt> qualifier allows an injection
 * point to refer to all beans or all events of a certain
 * bean type.</p>
 *
 * <pre>
 * &#064;Inject &#064;Any Instance&lt;PaymentProcessor&gt; anyPaymentProcessor;
 * </pre>
 *
 * <pre>
 * &#064;Inject &#064;Any Event&lt;User&gt; anyUserEvent;
 * </pre>
 *
 * <pre>
 * &#064;Inject &#064;Delegate &#064;Any Logger logger;
 * </pre>
 *
 * @author Gavin King
 * @author David Allen
 */

@Qualifier
@Retention(RUNTIME)
@Target( { TYPE, METHOD, FIELD, PARAMETER })
@Documented
public @interface Any
{

}

