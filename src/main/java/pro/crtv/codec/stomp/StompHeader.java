package pro.crtv.codec.stomp;

public enum StompHeader {

    ID("id"),
    MESSAGE("message"),
    MESSAGE_ID("message-id"),
    NACK("nack"),
    HOST("host"),
    SERVER("server"),
    LOGIN("login"),
    PASSCODE("passcode"),
    RECEIPT("receipt"),
    RECEIPT_ID("receipt-id"),
    VERSION("version"),
    SESSION("session"),
    DESTINATION("destination"),
    TRANSACTION("transaction"),
    HEART_BEAT("heart-beat"),
    ACCEPT_VERSION("accept-version"),
    CONTENT_LENGTH("content-length");

    private final String keyName;

    StompHeader(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyName() {
        return keyName;
    }
}
