package com.kid.brain.provider.request;

/**
 * Created by khiemnt on 11/4/16.
 */
public interface WebserviceConfig {

    // Server CID
    String CID_APP_ID = "EKPq5a6unrqWw4DAnrQVNE9jh7ATrcDP";
    String CID_API_SECRET_KEY = "Ds2wxVjfr5ySmMX3MAPgsYz6BhMhXEKe";

    String HEADER_CONTENT_TYPE = "Content-Type";
    String HEADER_CONTENT_TYPE_VALUE = "application/json";
    String HEADER_ACCEPT_LANGUAGE = "Accept-Language";

    interface Method {
        String GET = "GET";
        String POST = "POST";
        String UPDATE = "UPDATE";
        String DELETE = "DELETE";
    }

    public interface HTTP_CODE{
        int OK = 200;
        int SUCCESS = 0;
        int FAILED = 1;
        int CREATED = 201;
        int BAD_REQUEST = 400;
        int UNAUTHORIZED = 401;
        int NOT_FOUND = 404;
        int SYSTEM_ERROR = 500;
        int EMAIL_VERIFIED = 428;
        int EMAIL_NOT_VERIFIED = 554;
    }
}
