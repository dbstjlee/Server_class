package ch03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFile {

	public static void main(String[] args) {
		// 준비물
		// 1. 서버 소켓이 필요하다.
		// 2. 포트 번호가 필요한다. (0 ~ 65535까지 존재)
		// 2.1 잘 알려진 포트 번호 : 주로 시스템 레벨 - 0 ~ 1023까지 사용
		// 2.2 등록 가능한 포트 : 1024 ~ 49151까지 등록 가능(권장)
		// 2.3 동적/사설 포트 번호 - 그 외 임시 사용을 위해 할당된다.

		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(5001); // 서버 소켓
			System.out.println("서버를 시작합니다 - 포트번호 : 5001");
			socket = serverSocket.accept(); // 클라이언트와의 연결 기다리는 중임
			System.out.println(">>> 클라이언트가 연결하였습니다. <<<");

			// 대상은 소켓이다.(input Stream) 작업을 해 놓은 상태
			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input)); // 소켓 데이터를 담아서 읽을 준비중임

			// 1. 클라이언트에서 먼저 보낸 데이터를 읽는다.
			// 실제 데이터를 읽는 행위가 필요하다.
			String message = reader.readLine(); // 데이터를 읽음
			System.out.println("클라이언트 측 메세지 전달 받음 : " + message);

			// 2. 클라이언트 측으로 데이터를 보낸다.
			// 대상은 소켓이다. (Output Stream) 작업을 해야 한다.
			// OutputStream outputStream = new OutputStream 과 같음
			// 보조스트림
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true); // auto flush
			writer.println("난 서버야, 클라이언트 반가워"); // 줄바꿈 포함 메서드 안녕--> 안녕\n
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (serverSocket != null) {
				// 객체가 인스턴스화 됐다면 닫으면 된다.
				// 닫을때도 예외 처리
				// 프로그램이 종료되지 않게끔하는 것이 목적이다.
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
