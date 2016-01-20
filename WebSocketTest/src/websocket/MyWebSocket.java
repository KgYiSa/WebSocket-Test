package websocket;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * 2014年底，tomcat7.0.5x以上版本WebSocketServlet被弃用，而采用注解方式 (服务器tomcat7.0.67)
 * 
 * @author Kuang
 *
 */
// 该注解用来指定一个URI，客户端可以通过这个URI来连接到WebSocket。类似Servlet的注解mapping。无需在web.xml中配置
@ServerEndpoint("/test1")
public class MyWebSocket {
	// 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的
	private static int onlineCount = 0;
	// concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
	// 若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
	private static CopyOnWriteArraySet<MyWebSocket> webSocketSet = new CopyOnWriteArraySet<MyWebSocket>();
	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session mySession;

	/**
	 * 连接建立成功调用的方法
	 * 
	 * @param session
	 *            可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
	 */
	@OnOpen
	public void onOpen(Session session) {
		mySession = session;
		webSocketSet.add(this);// 加入set中
		addOnlineCount();// 在线数加1
		System.out.println("新连接" + session.getId() + "加入！当前在线人数为" + getOnlineCount());
		for (MyWebSocket item : webSocketSet) {
			try {
				item.sendCloseMessage(item.mySession, session.getId() + "进入房间！");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
		}
	}

	/**
	 * 收到客户端消息后调用的方法
	 * 
	 * @param message:客户端发送过来的消息
	 * @param session:可选的参数
	 */
	@OnMessage
	public void onMessage(String message, Session session) {

		// 群发消息
		for (MyWebSocket item : webSocketSet) {
			try {
				item.sendMessage(item.mySession, session.getId() + ":" + message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
		}
	}

	// @OnMessage(maxMessageSize = 6)
	// public void message(String s) {
	// // @Message 注释，MaxMessageSize 属性可以被用来定义消息字节最大限制，
	// // 在本例中，如果超过 6 个字节的信息被接收，就报告错误和连接关闭
	// }
	/**
	 * 链接关闭调用的方法
	 */
	@OnClose
	public void onClose(Session session) {
		webSocketSet.remove(this);// 从set中删除
		subOnlineCount();// 在线数减1
		System.out.println(session.getId() + "关闭了,当前在线人数" + getOnlineCount());
		// 群发消息
		for (MyWebSocket item : webSocketSet) {
			try {
				item.sendCloseMessage(item.mySession, session.getId() + "离开房间！");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
		}
	}

	/**
	 * 发生错误时调用
	 * 
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		System.out.println("发生错误:" + error.getMessage());

		error.printStackTrace();
	}

	/**
	 * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法
	 * 
	 * @param session
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(Session session, String message) throws IOException {
		session.getBasicRemote().sendText(message);
	}

	public void sendCloseMessage(Session session, String message) throws IOException {
		session.getBasicRemote().sendText("<b>" + message + "</b>");
	}

	public static synchronized int getOnlineCount() {
		return onlineCount;
	}

	public static synchronized void addOnlineCount() {
		MyWebSocket.onlineCount++;
	}

	public static synchronized void subOnlineCount() {
		MyWebSocket.onlineCount--;
	}
}
