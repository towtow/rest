package rest.sun

import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import rest.Controller

class Server {

    static start(Controller controller) {
        def server = HttpServer.create()
        server.bind(new InetSocketAddress(8080), 8080)
        server.createContext('/', controller as HttpHandler)
        server.start()
    }
}
