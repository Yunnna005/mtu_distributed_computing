package dto;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
@Path("/rpc")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class JsonRpcResource {
    @POST
    public JsonRpcResponse handleRequest(JsonRpcRequest request) {
        return switch (request.method) {
        case "sayHello" ->
            JsonRpcResponse.success(request.id, "Hello, " + request.params.get("name") + "!");
        case "add" -> {
            int a = (int) request.params.get("a");
            int b = (int) request.params.get("b");
            yield JsonRpcResponse.success(request.id, a + b);
        }
            default -> throw new WebApplicationException("Method not found", 404);
        };
    }
}
