package me.nandork.tomcat;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.inject.Inject;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;


public class TomcatService extends AbstractExecutionThreadService {

    static {
        // Funnel Tomcat's jul logs into slf4j
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Tomcat tomcat;

    @Inject
    public TomcatService(Tomcat tomcat) {
        this.tomcat = tomcat;
    }

    @Override
    protected void startUp() throws Exception {
        tomcat.start();
        logger.info("Tomcat Started");
    }

    @Override
    protected void run() throws Exception {
        logger.info("Tomcat Running");
        tomcat.getServer().await();
    }

    @Override
    protected void shutDown() throws Exception {
        logger.info("Tomcat Shutdown");
    }

    @Override
    protected void triggerShutdown() {
        logger.info("Tomcat Stopping");
        try {
            tomcat.stop();
        } catch (LifecycleException e) {
            logger.error("Failed to stop Tomcat", e);
        }
        logger.info("Tomcat Stopped");
    }
}
