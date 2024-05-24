package ch06;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.WritableByteChannel;
import java.util.Vector;

//< 서버측> <- 다수의 클라이언트가 접근
public class MultiClientServer {

	private static final int PORT = 5000;
	// 하나의 변수에 자원을 통으로 관리하기 기법 --> 자료구조
	// 자료구조 --> 코드 단일, 멀티 스레드 -> 멀티 스레드 --> 자료구조??
	// ArrayList는 멀티 스레드에 안정적이지 않음
	// 객체 배열 <-- Vector<> : 멀티 스레드에 안정적이다.
	private static Vector<PrintWriter> clientWriters = new Vector<>(); // 컴파일러가 문맥에서 타입을 추측함.
	// 제네릭 : PrintWriter (배열에 저장되는 타입)

	public static void main(String[] args) {
		System.out.println("Server started....");
		try (ServerSocket serverSocket = new ServerSocket(PORT);) { // 여기 안에 포트 번호를 할당함.
			// 서버 소켓 객체 생성하도록 설계
			while (true) {
				// 1. serverSocket.accept() 호출하면 블로킹 상태가 된다. 멈춰있음
				// 2. 클라이언트가 연결 요청 시 새로운 소켓 객체 생성이 된다.
				// 3. 새로운 스레드를 만들어서 처리할꺼임...(클라이언트가 데이터를 주고 받기 위한 스레드)
				// 4. 새로운 클라이언트가 접속하기까지 다시 대기 유지(계속 반복시킬거임)
				// 즉, 클라이언트가 접속할 때까지 대기(반복) 중 연결 시 새로운 소켓 객체를 생성함.
				Socket socket = serverSocket.accept();
				// input, output을 클래스화시켜서 묶어버리기.

				// 새로운 클라이언트가 연결되면 새로운 스레드가 생성된다.
				new ClientHandler(socket).start();
				// 정적 내부 클래스의 기본 생성자를 호출함. 서버의 소켓이 생성되고 스레드가 시작함.
			}
		} catch (Exception e) {
		}
	}// end of main

	// 정적 내부 클래스 설계
	private static class ClientHandler extends Thread {
		private Socket socket;
		private PrintWriter out; // 출력 스트림
		private BufferedReader in; // 입력 스트림

		// 기본 생성자 (입력한 socket이 this.socket에 담기도록 함.)
		public ClientHandler(Socket socket) {
			this.socket = socket;
		}

		// 스레드 start() 호출 시 동작되는 메서드 - 약속
		@Override
		public void run() {
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 데이터를 읽어들임.
				out = new PrintWriter(socket.getOutputStream(), true); // 데이터를 출력함.

				// 여기서 중요! - 서버가 관리하는 자료구조에 자원 저장(클라이언트와 연결된 소켓 -> outputStream)
				clientWriters.add(out); // outputStream 이라는 자료구조가 clientWriters에 저장되어 배열이 돌아감(꺼내쓸거 꺼내씀)
				// 계속 while문 돌면서 기다리는 역할
				String message;
				while ((message = in.readLine()) != null) { // 메시지 받을 때까지 기다림
					System.out.println("Received : " + message);
					broadcastMessage(message); // 받은 메시지를 broadcast함. (모든 클라이언트에게 보내짐)
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();// 소켓 자원 닫으면 클라이언트와의 연결이 해제됨.
					System.out.println(".....클라이언트 연결 해제......");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	} // end of ClientHandler

	// 서버측과 연결된 모든 클라이언트에게 메시지 보내기 - 브로드캐스트
	private static void broadcastMessage(String message) {
		for (PrintWriter writer : clientWriters) { 
			// clientWriters : 배열이 담겨있는 변수 
			// PrintWriter writer: clientWriters 배열의 각 요소 값을 순차적으로 writer에 전달함.
			writer.println(message); // 받은 메시지 출력
		}
	}

}
