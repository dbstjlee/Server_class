package ch07;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.WritableByteChannel;
import java.util.Vector;

public class MultiClientServer {

	private static final int PORT = 5000;
	private static Vector<PrintWriter> clientWriters = new Vector<>();

	public static void main(String[] args) {
		System.out.println("Server started....");

		try (ServerSocket serverSocket = new ServerSocket(PORT);) { // 여기 안에 포트 번호를 할당함.

			while (true) {
				Socket socket = serverSocket.accept(); // 새로운 소켓을 생성함.
				// input, output을 클래스화시켜서 묶어버리기.

				// 새로운 클라이언트가 연결되면 새로운 스레드가 생성된다.
				new ClientHandler(socket).start();
			}
		} catch (Exception e) {
		}
	}// end of main

	// 정적 내부 클래스 설계
	private static class ClientHandler extends Thread {
		private Socket socket;
		private PrintWriter out; // 출력 스트림
		private BufferedReader in; // 입력 스트림

		public ClientHandler(Socket socket) {
			this.socket = socket;
		}

		// 스레드 start() 호출 시 동작되는 메서드 - 약속
		@Override
		public void run() {
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				// 코드 추가
				// 클라이언트로부터 이름 받기(약속되어 있음)
				String nameMessage = in.readLine();
				if (nameMessage != null && nameMessage.startsWith("NAME:")) {// startsWith: 시작하는 문자열이
					String clientName = nameMessage.substring(5);// 문자열을 잘라냄. N: 0 A: 1 M:2
					// 다음에 오는 문자가 clientName에 담김
					System.out.println("Client Name : " + clientName);
					broadcastMessage("해당 서버에 입장 : " + clientName + "님 입장");
				} else {
					// 약속과 다르게 접근했다면 종료 처리하기
					// "NAME:" 이거 먼저 보내야 하는데 안 보내면
					socket.close();
					return;

				}
				// 여기서 중요! - 서버가 관리하는 자료구조에 자원 저장(클라이언트와 연결된 소켓 -> outputStream)
				clientWriters.add(out);
				// 계속 while문 돌면서 기다리는 역할
				String message;
				while ((message = in.readLine()) != null) {
					System.out.println("Received : " + message);
					// 프로토콜의 형식을 지정
					// 약속 -> 클라이언트와 서버의 약속
					// : 기준으로 처리, / 기준, <--
					// MSG: 안녕\n(클라이언트가 보냄)
					// 문자열 기준으로 자르기 - split
					// String[]: 리턴값
					String[] parts = message.split(":", 2); // : 기준
					System.out.println("parts 인덱스 개수: " + parts.length);
					// 명령 부분을 분리시킴
					String command = parts[0];
					// 데이터 부분을 분리시킴
					String data = parts.length > 1 ? parts[1] : "";

					if (command.equals("MSG")) {
						System.out.println("연결된 전체 사용자에게 MSG 방송");
						broadcastMessage(message);

					} else if (command.equals("BYE")) {

						System.out.println("Client disconnected...");
						break; // while 구문 종료...

						// broadcastMessage(message);
					}
				} // end of while
			} catch (

			Exception e) {
				// e.printStackTrace();
			} finally {
				try {
					socket.close();
					// 도전과제?
					// 서버측에서 관리하고 있는 P.W 제거해야 한다.
					// 인덱스 번호가 필요하다.
					// clientWriters.add() 할 때 지정된 나의 인덱스 번호가 필요
					// clientWriters.remove();
					System.out.println(".....클라이언트 연결 해제......");
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
		} // end of ClientHandler

		// 모든 클라이언트에게 메시지 보내기 - 브로드캐스트
		private static void broadcastMessage(String message) {
			for (PrintWriter writer : clientWriters) {
				writer.println(message);
			}
		}
	}
}
