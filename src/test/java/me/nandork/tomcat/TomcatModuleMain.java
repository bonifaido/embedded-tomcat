package me.nandork.tomcat;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.google.inject.servlet.SessionScoped;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.template.Template;
import freemarker.template.TemplateModel;
import org.apache.catalina.manager.JMXProxyServlet;

import javax.management.remote.JMXPrincipal;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

public class TomcatModuleMain {
    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new TomcatModule(), new ServletModule() {
            @Override
            protected void configureServlets() {
                serve("/jmx/*").with(new JMXProxyServlet());
                serve("/*").with(MyFreemarkerServlet.class);
            }

            @Provides
            @SessionScoped
            Principal providePrincipal() {
                return new JMXPrincipal("name");
            }
        });

        TomcatService tomcat = injector.getInstance(TomcatService.class);

//        tomcat.addListener(new Service.Listener() {
//            Logger logger = LoggerFactory.getLogger(getClass());
//
//            @Override
//            public void running() {
//                logger.info("Tomcat started, so stopping it");
//                tomcat.stopAsync();
//            }
//        }, MoreExecutors.sameThreadExecutor());

        tomcat.startAsync();
    }
}

@Singleton
class MyFreemarkerServlet extends FreemarkerServlet {

    @Inject
    private Provider<Principal> provider;

    @Override
    protected boolean preTemplateProcess(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Template template, TemplateModel data) throws ServletException, IOException {
        System.out.println(System.identityHashCode(provider.get()));
        return super.preTemplateProcess(request, response, template, data);
    }
}