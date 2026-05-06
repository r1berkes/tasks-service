package ie.rberkes.tasks.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TraceFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {
        String traceId = req.getHeader("X-Trace-Id");

        if (traceId == null) {
            res.setStatus(400);
            return;
        }

        MDC.put("traceId", traceId);

        try {
            chain.doFilter(req, res);
        } finally {
            MDC.clear();
        }
    }
}