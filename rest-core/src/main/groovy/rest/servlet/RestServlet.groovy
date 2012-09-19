package rest.servlet

import rest.Controller
import rest.Exchange
import rest.Router

import javax.servlet.Servlet
import javax.servlet.ServletConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RestServlet implements Servlet {
    private ServletConfig config
    private Controller controller

    @Override
    void init(ServletConfig config) {
        this.config = config
        List<Class> resourceClasses = config.getInitParameter("rest.resourceClasses").split(",").collect {
            Thread.currentThread().contextClassLoader.loadClass(it)
        }
        def router = new Router(resourceClasses)
        controller = new Controller(router)
    }

    @Override
    ServletConfig getServletConfig() {
        return config
    }

    @Override
    void service(final ServletRequest req, final ServletResponse res) {
        final HttpServletRequest request = (HttpServletRequest) req
        final HttpServletResponse response = (HttpServletResponse) res
        controller.handle(new Exchange() {
            @Override
            def getResponseHeaders() {
                [add: { String name, String value ->  response.addHeader(name, value) }]
            }

            @Override
            URI getRequestURI() {
                URI.create(request.requestURI)
            }

            @Override
            String getRequestMethod() {
                request.method
            }

            @Override
            void close() {
            }

            @Override
            OutputStream getResponseBody() {
                response.outputStream
            }

            @Override
            void sendResponseHeaders(int statusCode, long contentLength) {
                response.setStatus(statusCode)
                response.addHeader('Content-Length', String.valueOf(contentLength))

            }

            @Override
            InetSocketAddress getRemoteAddress() {
                new InetSocketAddress(request.remoteAddr, request.remotePort)
            }

            @Override
            String getProtocol() {
                request.protocol
            }
        })
    }

    @Override
    String getServletInfo() {
        return "rest servlet"
    }

    @Override
    void destroy() {
    }
}
