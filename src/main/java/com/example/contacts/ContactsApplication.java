package com.example.contacts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;

@SpringBootApplication
public class ContactsApplication extends SpringBootServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger(ContactsApplication.class);

    /**
     * Bootstrap method if the app is run as a .jar file
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplicationBuilder()
                .sources(ContactsApplication.class)
                .build();
        app.run(args);
    }

    /**
     * Bootstrap method if the app is deployed to a web server (.war)
     * Spring's ServletContainerInitializer will call it.
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ContactsApplication.class);
    }

    /**
     * Fix for an inconvenient behaviour of a RequestToViewNameTranslator bean:
     * If ModelAndView has no viewName (= path to the template file),
     * RequestToViewNameTranslator will generate viewName automatically based on the request URL.
     *
     * Problem comes when we disable automatic viewName suffix in viewResolver,
     * using spring.thymeleaf.suffix="",
     * because instead of "index" we want to pass "index.html",
     * but in default setting, the RequestToViewNameTranslator would drop the suffix from the URL.
     *
     * To suppress automatic extension drop, here is the override.
     */
    @Bean
    public RequestToViewNameTranslator viewNameTranslator(Environment environment) {
        DefaultRequestToViewNameTranslator viewNameTranslator = new DefaultRequestToViewNameTranslator();
        String thymeleafSuffix = environment.getProperty("spring.thymeleaf.suffix");
        if (thymeleafSuffix != null && thymeleafSuffix.isEmpty()) {
            viewNameTranslator.setStripExtension(false);
        }
        return viewNameTranslator;
    }

    /**
     * Listener to a web app event, which logs the URL on which the web app is deployed
     * @param evt The event info object
     */
    @EventListener
    public void onAppEvent(ServletWebServerInitializedEvent evt) {
        int port = evt.getApplicationContext().getWebServer().getPort();
        logger.info("Your web app address: http://localhost:" + port +
                evt.getApplicationContext().getServletContext().getContextPath());
    }

}
