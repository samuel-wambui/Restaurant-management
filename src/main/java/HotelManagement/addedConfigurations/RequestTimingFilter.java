package HotelManagement.addedConfigurations;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class RequestTimingFilter implements Filter {

    private static final Logger logger = Logger.getLogger(RequestTimingFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // This method is optional but can be used to initialize filter-related resources.
        System.out.println("Filter Initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        long startTime = System.currentTimeMillis(); // Capture start time
        chain.doFilter(request, response); // Proceed with the request-response chain
        long endTime = System.currentTimeMillis(); // Capture end time

        long duration = endTime - startTime; // Calculate request time
        logger.info("Request processed in " + duration + " ms");
    }

    @Override
    public void destroy() {
        // Clean up any resources (if needed) when the filter is destroyed
        System.out.println("Filter Destroyed");
    }
}