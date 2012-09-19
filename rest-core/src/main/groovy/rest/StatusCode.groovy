package rest

interface StatusCode {

    public final int OK = 200;
//    public final int CREATED = 201;
//    public final int BAD_REQUEST = 400;
    public final int NOT_FOUND = 404;
    public final int METHOD_NOT_ALLOWED = 405;
    public final int INTERNAL_SERVER_ERROR = 500;
}
