package me.molchanoff.microblog.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import javax.servlet.ServletContext;

/**
 * This class is used to initialize Spring Security
 */
public class SecurityWebInitializer extends AbstractSecurityWebApplicationInitializer {
    @Override
    protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        HiddenHttpMethodFilter hiddenHttpMethodFilter = new HiddenHttpMethodFilter();
        insertFilters(servletContext, characterEncodingFilter, hiddenHttpMethodFilter);
    }
}
