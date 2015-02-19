package me.nandork.tomcat;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;
import org.apache.catalina.Context;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.ServletException;

/**
 * TODO add configuration
 * TODO make ProtocolHandlers configurable
 */
public class TomcatModule extends PrivateModule {

    @Override
    protected void configure() {
        bind(TomcatService.class).in(Singleton.class);
        expose(TomcatService.class);
    }

    /**
     * Creates and configures the one and only Tomcat instance.
     *
     * @return a Tomcat instance
     * @throws ServletException
     */
    @Provides
    @Singleton
    Tomcat provideTomcat() throws ServletException {
        Tomcat tomcat = new Tomcat();

        String appBase = ".";
        tomcat.setPort(8080);

        tomcat.setBaseDir("webapp");
        tomcat.getHost().setAppBase(appBase);

        String contextPath = "/";

        // Add AprLifecycleListener to give native speed boost
        // sudo apt-get install libtcnative-1
        // Server server = tomcat.getServer();
        // AprLifecycleListener listener = new AprLifecycleListener();
        // server.addLifecycleListener(listener);

        Context context = tomcat.addContext("", appBase);
        Tomcat.addServlet(context, "default", new DefaultServlet());
        context.addServletMapping(contextPath, "default");

        // vs.

        // Context context = tomcat.addWebapp(contextPath, appBase);


        context.addFilterDef(newFilterDef("guice", GuiceFilter.class.getName()));
        context.addFilterMap(newFilterMap("guice", "/*"));


        return tomcat;
    }

    private FilterDef newFilterDef(String filterName, String filterClass) {
        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName(filterName);
        filterDef.setFilterClass(filterClass);
        return filterDef;
    }

    private FilterMap newFilterMap(String filterName, String urlPattern) {
        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(filterName);
        filterMap.addURLPattern(urlPattern);
        return filterMap;
    }
}
