package rest

interface Exchange {
    def getResponseHeaders()

    URI getRequestURI()

    String getRequestMethod()

    void close()

    OutputStream getResponseBody()

    void sendResponseHeaders(int statusCode, long contentLength)

    InetSocketAddress getRemoteAddress()

    String getProtocol()
}
