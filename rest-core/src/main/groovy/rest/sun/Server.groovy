package rest.sun

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import rest.Controller
import rest.Exchange

class Server {

    static start(Controller controller) {
        def server = HttpServer.create()
        server.bind(new InetSocketAddress(8080), 8080)
        server.createContext('/', new HttpHandler() {
            @Override
            void handle(HttpExchange httpExchange) {
                controller.handle(httpExchange as Exchange)
            }
        })
        server.start()
    }
}
