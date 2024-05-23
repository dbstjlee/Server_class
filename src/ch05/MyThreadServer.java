package ch05;

import java.io.IOException;
import java.net.ServerSocket;

public class MyThreadServer extends AbstractServer {

	@Override
	protected void setupServer() throws IOException {
		// 추상 클래스가 부모에 있음 // 자식은 부모 기능의 확장 또는 사용 가능함
		// 서버측 소캣 통신을 하려면 서버 소켓이 필요하다.
		super.setServerSocket(new ServerSocket(5001));
		System.out.println(">>> Server started on port 5000 <<<");
	}

	@Override
	protected void connection() throws IOException {
		// 서버 소켓.accept() 호출이다.
		super.setSocket(super.getServerSocket().accept());

	}

	public static void main(String[] args) {
		MyThreadServer myThreadServer = new MyThreadServer();
		myThreadServer.run();
	}

}