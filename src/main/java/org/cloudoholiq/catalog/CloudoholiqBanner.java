package org.cloudoholiq.catalog;

import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

import static org.springframework.boot.ansi.AnsiElement.DEFAULT;
import static org.springframework.boot.ansi.AnsiElement.FAINT;
import static org.springframework.boot.ansi.AnsiElement.GREEN;

/**
 * Created by Eda on 19.2.2015.
 */
public class CloudoholiqBanner implements Banner{

        private static final String[] BANNER = {"", "" +
                "       .__                   .___     .__           .__  .__        \n" +
                "  ____ |  |   ____  __ __  __| _/____ |  |__   ____ |  | |__| ______\n" +
                "_/ ___\\|  |  /  _ \\|  |  \\/ __ |/  _ \\|  |  \\ /  _ \\|  | |  |/ ____/\n" +
                "\\  \\___|  |_(  <_> )  |  / /_/ (  <_> )   Y  (  <_> )  |_|  < <_|  |\n" +
                " \\___  >____/\\____/|____/\\____ |\\____/|___|  /\\____/|____/__|\\__   |\n" +
                "     \\/                       \\/           \\/                   |__|"};

        private static final String SPRING_BOOT = " :: cloudoholiq :: ";

        private static final int STRAP_LINE_SIZE = 42;

        @Override
        public void printBanner(Environment environment, Class<?> sourceClass,
                                PrintStream printStream) {
            for (String line : BANNER) {
                printStream.println(line);
            }
            String version = "1.0.0";
            String padding = "";
            while (padding.length() < STRAP_LINE_SIZE
                    - (version.length() + SPRING_BOOT.length())) {
                padding += " ";
            }

            printStream.println(AnsiOutput.toString(GREEN, SPRING_BOOT, DEFAULT, padding,
                    FAINT, version));
            printStream.println();
        }

}
