package gov.samhsa.c2s.patientuser.config;

public final class TokenRelayTypeHeader {
    private static final String VALUE_SEPARATOR = ": ";
    public static final String HEADER_KEY = "TOKEN_RELAY_TYPE";
    public static final String DEFAULT = "DEFAULT";
    public static final String CLIENT_CREDENTIALS = "CLIENT_CREDENTIALS";
    public static final String DEFAULT_HEADER_VALUE = HEADER_KEY + VALUE_SEPARATOR + DEFAULT;
    public static final String CLIENT_CREDENTIALS_HEADER_VALUE = HEADER_KEY + VALUE_SEPARATOR + CLIENT_CREDENTIALS;

    private TokenRelayTypeHeader() {
    }
}