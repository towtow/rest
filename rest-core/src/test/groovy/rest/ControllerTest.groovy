package rest

import spock.lang.Specification

class ControllerTest extends Specification {
    def router = Mock(Router)
    def exchange = Mock(Exchange)
    def responseHeaders = Mock(Headers)
    def responseBody = new ByteArrayOutputStream()
    def controller = new Controller(router)

    def setup() {
        exchange.getResponseHeaders() >> responseHeaders
        exchange.getResponseBody() >> responseBody
    }

    def 'no matching route results in sending 404'() {
        exchange.getRequestURI() >> URI.create(requestURI)

        when:
        controller.handle(exchange)

        then:
        1 * exchange.sendResponseHeaders(404, 0)
        0 * exchange.getResponseBody()

        then:
        1 * exchange.close()

        where:
        requestURI << ['/', '/test', '/somethingelse']
    }

    def 'ok'() {
        def uri = URI.create('/exists')
        exchange.getRequestURI() >> uri
        exchange.getRequestMethod() >> 'GET'
        def resource = Mock(Resource)
        router.matchResource(uri) >> resource
        resource.getResponse() >> [code: 200, body: [a: 1]]

        when:
        controller.handle(exchange)

        then:
        1 * responseHeaders.add('Content-Type', 'application/json;encoding=utf-8')
        1 * resource.get()

        then:
        1 * exchange.sendResponseHeaders(200, '{"a":1}'.getBytes('utf-8').size())

        then:
        1 * exchange.close()

        then:
        responseBody.toString('utf-8') == '{"a":1}'
    }
}
