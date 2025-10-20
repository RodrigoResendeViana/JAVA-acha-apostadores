package br.com.fiap.challenge.gamblers.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

@Component
public class SwaggerUiOpener implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${app.swagger.auto-open:false}")
    private boolean autoOpen;

    @Value("${server.port:8080}")
    private int serverPort;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!autoOpen) return;

        String url = String.format("http://localhost:%d/swagger-ui/index.html", serverPort);
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                // fallback: try to open via runtime (platform dependent)
                Runtime.getRuntime().exec(new String[] {"cmd", "/c", "start", "", url});
            }
        } catch (Exception e) {
            // don't fail startup if opening browser fails
            System.out.println("Could not open Swagger UI automatically: " + e.getMessage());
        }
    }
}
