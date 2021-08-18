package open.zikun.rpc.enumeration;

/**
 * 请求包 or 响应包 的标签
 */
public enum PackageType {

    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;

    PackageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
