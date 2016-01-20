package servlet;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
/**
 * tomcat 7.0.2x/7.0.3X 的版本使用自定义 API （WebSocketServlet 和 StreamInbound，
 *  前者是一个容器，用来初始化 WebSocket 环境；后者是用来具体处理 WebSocket 请求和响应)
 * @author Kuang
 *
 */
public class SocketServer extends WebSocketServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected StreamInbound createWebSocketInbound(String arg0, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
