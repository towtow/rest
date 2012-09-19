package rest

import groovy.json.JsonOutput

import java.text.SimpleDateFormat

class Controller {
    private final static String[] RESOURCE_METHODS = ['GET', 'POST', 'PUT', 'DELETE', 'PATCH']

    final router

    Controller(router) {
        this.router = router
    }

    void handle(exchange) {
        try {
            def rsp = invokeResource(exchange)
            def contentLength = rsp.contentLength ?: 0
            exchange.sendResponseHeaders(rsp.code as int, contentLength as long)
            if (rsp.body) exchange.responseBody << rsp.body
            logAccess(exchange, rsp, contentLength)
        }
        finally {
            exchange.close()
        }
    }

    private invokeResource(exchange) {
        try {
            def x = [request: [uri: exchange.requestURI], response: [:]]
            def res = router.matchResource(x.request.uri)
            if (res && exchange.requestMethod == 'OPTIONS') {
                addAllowHeader(exchange, res)
                [code: StatusCode.OK]
            }
            else if (res && (exchange.requestMethod == 'HEAD' && isMethodDefined(res, 'GET')
                    || RESOURCE_METHODS.contains(exchange.requestMethod) && isMethodDefined(res, exchange.requestMethod))) {
                def isHeadReq = exchange.requestMethod == 'HEAD'
                def methodToCall = isHeadReq ? 'get' : exchange.requestMethod.toLowerCase(Locale.ENGLISH)
                res.response = [code: StatusCode.OK, headers: [:], body: [:]]
                res.invokeMethod(methodToCall, null)
                def body = null
                def contentLength = 0
                if (res.response.body) {
                    exchange.responseHeaders.add('Content-Type', 'application/json;encoding=utf-8')
                    def bytes = JsonOutput.toJson(res.response.body).getBytes('utf-8')
                    contentLength = bytes.size()
                    if (!isHeadReq) body = bytes
                }
                res.response.headers.each { k, v -> exchange.responseHeaders.add(k, v) }
                [code: res.response.code, body: body, contentLength: contentLength]
            }
            else if (res) {
                addAllowHeader(exchange, res)
                [code: StatusCode.METHOD_NOT_ALLOWED]
            }
            else {
                [code: StatusCode.NOT_FOUND]
            }
        } catch (Exception ex) {
            logError(exchange, ex)
            [code: StatusCode.INTERNAL_SERVER_ERROR]
        }
    }

    def addAllowHeader(exchange, resource) {
        def allowedMethods = RESOURCE_METHODS.findAll { isMethodDefined(resource, it) }
        if (allowedMethods.contains('GET')) allowedMethods << 'HEAD'
        if (allowedMethods) exchange.responseHeaders.add('Allow', allowedMethods.join(', '))
    }

    private isMethodDefined(res, methodName) {
        def lcName = methodName.toLowerCase(Locale.ENGLISH)
        res.class.methods.find { it.name == lcName }
    }

    private void logAccess(exchange, rsp, contentLength) {
        def clientAdr = exchange?.remoteAddress?.address?.hostAddress
        def timestamp = new SimpleDateFormat('dd/MMM/yyyy:kk:mm:ss Z', Locale.ENGLISH).format(new Date())
        System.out.println("${clientAdr ?: '-'} - [${timestamp ?: '-'}] \'${requestInfo(exchange)}\' ${rsp.code ?: '-'} ${contentLength ?: '-'}")
    }

    private logError(exchange, Exception ex) {
        System.err.println(requestInfo(exchange))
        ex.printStackTrace(System.err)
    }

    private requestInfo(exchange) {
        "${exchange?.requestMethod ?: '-'} ${exchange?.requestURI ?: '-'} ${exchange?.protocol ?: '-'}".toString()
    }
}
