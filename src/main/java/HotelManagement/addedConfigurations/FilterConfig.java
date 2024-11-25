package HotelManagement.addedConfigurations;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RequestTimingFilter> loggingFilter() {
        FilterRegistrationBean<RequestTimingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestTimingFilter());
//        registrationBean.addUrlPatterns("/api/*");  // Filter URL patterns (Optional)
        return registrationBean;
    }
}
