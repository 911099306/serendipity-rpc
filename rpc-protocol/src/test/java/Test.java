import com.alibaba.fastjson2.JSON;
import com.serendipity.rpc.protocol.RpcProtocol;
import com.serendipity.rpc.protocol.header.RpcHeader;
import com.serendipity.rpc.protocol.header.RpcHeaderFactory;
import com.serendipity.rpc.protocol.request.RpcRequest;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/5
 **/
public class Test {

    public static RpcProtocol<RpcRequest> getRpcProtocol(){
        RpcHeader header = RpcHeaderFactory.getRequestHeader("jdk");
        RpcRequest body = new RpcRequest();
        body.setOneway(false);
        body.setAsync(false);
        body.setClassName("com.serendipity.rpc.demo.RpcProtocol");
        body.setMethodName("hello");
        body.setGroup("serendipity");
        body.setParameters(new Object[]{"serendipity"});
        body.setParameterTypes(new Class[]{String.class});
        body.setVersion("1.0.0");

        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        protocol.setBody(body);
        protocol.setHeader(header);
        return protocol;
    }
    public static void main(String[] args) {
        RpcProtocol<RpcRequest> rpcProtocol = getRpcProtocol();
        System.out.println("rpcProtocol = " + JSON.toJSONString(rpcProtocol));
    }
}
