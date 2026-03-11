package dto;

public class JsonRpcResponse {
    public String jsonrpc = "2.0";
    public Object result;
    public Object error;
    public Object id;
    public static JsonRpcResponse success(Object id, Object result) {
        JsonRpcResponse resp = new JsonRpcResponse();
        resp.id = id;
        resp.result = result;
        return resp;
    }
}
