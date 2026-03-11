package dto;

import java.util.Map;

public class JsonRpcRequest {
    public String jsonrpc = "2.0";
    public String method;
    public Map<String, Object> params;
    public Object id;
}
