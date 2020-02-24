package com.kafka;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiElement;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

/**
 * @author jayendravikramsingh
 *         <p>
 *         <p>
 *         21/02/20
 */
public class WorkerBanner implements Banner {
    
    static final AnsiElement WORKER_ORANGE = new AnsiElement() {
        @Override
        public String toString() {
            return "38;5;208"; // Ansi 256 color code 208 (orange)
        }
    };
    
    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        try (InputStream stream = new ClassPathResource("producer.txt").getInputStream()) {
            String banner = StreamUtils.copyToString(stream, StandardCharsets.UTF_8);
            
            // Instead of use property expansion for only 2 ansi codes, inline them
            banner = banner.replace("${AnsiOrange}", AnsiOutput.encode(WORKER_ORANGE));
            banner = banner.replace("${AnsiNormal}", AnsiOutput.encode(AnsiStyle.NORMAL));
            
            out.println(banner);
        } catch (Exception ex) {
            // who cares
        }
    }
}
