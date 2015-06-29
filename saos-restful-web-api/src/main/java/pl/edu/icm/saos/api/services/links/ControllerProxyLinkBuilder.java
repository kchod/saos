package pl.edu.icm.saos.api.services.links;

import java.lang.reflect.Method;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.core.AnnotationMappingDiscoverer;
import org.springframework.hateoas.core.DummyInvocationUtils;
import org.springframework.hateoas.core.LinkBuilderSupport;
import org.springframework.hateoas.core.MappingDiscoverer;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.hateoas.mvc.ControllerLinkBuilderFactory;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import pl.edu.icm.saos.common.http.HttpServletRequestUtils;

/**
 * Builder to ease building {@link Link} instances pointing to Spring MVC controllers. Based on {@link ControllerLinkBuilder}
 * with changed {@link #getBuilder()} which takes into account being behind a proxy.
 * 
 * @author Łukasz Dumiszewski
 */

public class ControllerProxyLinkBuilder extends LinkBuilderSupport<ControllerProxyLinkBuilder> {

        private static final MappingDiscoverer DISCOVERER = new AnnotationMappingDiscoverer(RequestMapping.class);
        private static final ControllerLinkBuilderFactory FACTORY = new ControllerLinkBuilderFactory();

        /**
         * Creates a new {@link ControllerLinkBuilder} using the given {@link UriComponentsBuilder}.
         * 
         * @param builder must not be {@literal null}.
         */
        ControllerProxyLinkBuilder(UriComponentsBuilder builder) {
            super(builder);
        }

        /**
         * Creates a new {@link ControllerLinkBuilder} with a base of the mapping annotated to the given controller class.
         * 
         * @param controller the class to discover the annotation on, must not be {@literal null}.
         * @return
         */
        public static ControllerProxyLinkBuilder linkTo(Class<?> controller) {
            return linkTo(controller, new Object[0]);
        }

        /**
         * Creates a new {@link ControllerLinkBuilder} with a base of the mapping annotated to the given controller class. The
         * additional parameters are used to fill up potentially available path variables in the class scop request mapping.
         * 
         * @param controller the class to discover the annotation on, must not be {@literal null}.
         * @param parameters additional parameters to bind to the URI template declared in the annotation, must not be
         *          {@literal null}.
         * @return
         */
        public static ControllerProxyLinkBuilder linkTo(Class<?> controller, Object... parameters) {

            Assert.notNull(controller);

            ControllerProxyLinkBuilder builder = new ControllerProxyLinkBuilder(getBuilder());
            String mapping = DISCOVERER.getMapping(controller);

            UriComponents uriComponents = UriComponentsBuilder.fromUriString(mapping == null ? "/" : mapping).build();
            UriComponents expandedComponents = uriComponents.expand(parameters);

            return builder.slash(expandedComponents);
        }

        /*
         * @see org.springframework.hateoas.MethodLinkBuilderFactory#linkTo(Method, Object...)
         */
        public static ControllerProxyLinkBuilder linkTo(Method method, Object... parameters) {
            return linkTo(method.getDeclaringClass(), method);
        }

        /*
         * @see org.springframework.hateoas.MethodLinkBuilderFactory#linkTo(Class<?>, Method, Object...)
         */
        public static ControllerProxyLinkBuilder linkTo(Class<?> controller, Method method, Object... parameters) {

            Assert.notNull(controller, "Controller type must not be null!");
            Assert.notNull(method, "Method must not be null!");

            UriTemplate template = new UriTemplate(DISCOVERER.getMapping(controller, method));
            URI uri = template.expand(parameters);

            return new ControllerProxyLinkBuilder(getBuilder()).slash(uri);
        }

        /**
         * Creates a {@link ControllerLinkBuilder} pointing to a controller method. Hand in a dummy method invocation result
         * you can create via {@link #methodOn(Class, Object...)} or {@link DummyInvocationUtils#methodOn(Class, Object...)}.
         * 
         * <pre>
         * @RequestMapping("/customers")
         * class CustomerController {
         * 
         *   @RequestMapping("/{id}/addresses")
         *   HttpEntity&lt;Addresses&gt; showAddresses(@PathVariable Long id) { … } 
         * }
         * 
         * Link link = linkTo(methodOn(CustomerController.class).showAddresses(2L)).withRel("addresses");
         * </pre>
         * 
         * The resulting {@link Link} instance will point to {@code /customers/2/addresses} and have a rel of
         * {@code addresses}. For more details on the method invocation constraints, see
         * {@link DummyInvocationUtils#methodOn(Class, Object...)}.
         * 
         * @param invocationValue
         * @return
         */
        public static ControllerLinkBuilder linkTo(Object invocationValue) {
            return FACTORY.linkTo(invocationValue);
        }

        /**
         * Wrapper for {@link DummyInvocationUtils#methodOn(Class, Object...)} to be available in case you work with static
         * imports of {@link ControllerLinkBuilder}.
         * 
         * @param controller must not be {@literal null}.
         * @param parameters parameters to extend template variables in the type level mapping.
         * @return
         */
        public static <T> T methodOn(Class<T> controller, Object... parameters) {
            return DummyInvocationUtils.methodOn(controller, parameters);
        }

        /* 
         * (non-Javadoc)
         * @see org.springframework.hateoas.UriComponentsLinkBuilder#getThis()
         */
        @Override
        protected ControllerProxyLinkBuilder getThis() {
            return this;
        }

        /* 
         * (non-Javadoc)
         * @see org.springframework.hateoas.UriComponentsLinkBuilder#createNewInstance(org.springframework.web.util.UriComponentsBuilder)
         */
        @Override
        protected ControllerProxyLinkBuilder createNewInstance(UriComponentsBuilder builder) {
            return new ControllerProxyLinkBuilder(builder);
        }

        /**
         * Returns a {@link UriComponentsBuilder} to continue to build the already built URI in a more fine grained way.
         * 
         * @return
         */
        public UriComponentsBuilder toUriComponentsBuilder() {
            return UriComponentsBuilder.fromUri(toUri());
        }

        /**
         * Returns a {@link UriComponentsBuilder} obtained from the current servlet mapping with the host tweaked in case the
         * request contains an {@code X-Forwarded-Host} header and the scheme tweaked in case the request contains an
         * {@code X-Forwarded-Proto} header and the port tweaked in case the request contains an {@code X-Forwarded-port)
         * <br/>
         * Uses {@link HttpServletRequestUtils}
         * @return
         */
        static UriComponentsBuilder getBuilder() {

            HttpServletRequest request = getCurrentRequest();
            ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromServletMapping(request);

            String scheme = HttpServletRequestUtils.extractScheme(request);
            builder.scheme(scheme);
            
            builder.host(HttpServletRequestUtils.extractHost(request));

            int port = HttpServletRequestUtils.extractServerPort(request);
            
            if (!HttpServletRequestUtils.isDefaultPort(scheme, port)) {
                builder.port(port);
            } else {
                builder.port(-1);
            }
            
            return builder;
        }

        /**
         * Copy of {@link ServletUriComponentsBuilder#getCurrentRequest()} until SPR-10110 gets fixed.
         * 
         * @return
         */
        @SuppressWarnings("null")
        private static HttpServletRequest getCurrentRequest() {

            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            Assert.state(requestAttributes != null, "Could not find current request via RequestContextHolder");
            Assert.isInstanceOf(ServletRequestAttributes.class, requestAttributes);
            HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
            Assert.state(servletRequest != null, "Could not find current HttpServletRequest");
            return servletRequest;
        }
}