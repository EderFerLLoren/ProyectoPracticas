package filtroSesion;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/filtroFacturas.xhtml",
							"/altaFacturas.xhtml",
							"/filtroPagos.xhtml",
							"/altaPagos.xhtml"})
public class FiltroSesion implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Método init requerido por la interfaz Filter, puedes dejarlo vacío si no necesitas inicializar nada
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Verificar si el usuario tiene una sesión iniciada
        if (httpRequest.getSession().getAttribute("usuario") == null) {
            // Si no hay sesión iniciada, redirigir al usuario a la página de inicio de sesión
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.xhtml");
        } else {
            // Si hay sesión iniciada, permitir que la solicitud continúe hacia la siguiente etapa del proceso de solicitud
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // Método destroy requerido por la interfaz Filter, puedes dejarlo vacío si no necesitas realizar ninguna limpieza
    }
}
