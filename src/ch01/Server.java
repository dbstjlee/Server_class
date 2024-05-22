package ch01;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

// 뭔가 제공해주는 입장
public class Server {

	public static void main(String[] args) {

		// 서버측 소켓통신을 만들기 위한 준비물
		// 1. ServerSocket(클라이언트 측 소켓과 연결만 시켜준다.)
		// 2. 클라이언트와 연결되는 소켓을 들고 있어야 한다.
		// 로컬 컴퓨터에는 정해진 또는 사용할 수 있는 포트 번호 개수가
		// 할당되어 있다. 1~1024 포트 번호는 잘 알려진 포트 번호
		// 시스템이 선점하고 있는 번호들이다.
		// => 옆사람과 통신할 때 위 포트 번호를 제외하고 할당하라

		try (ServerSocket serverSocket = new ServerSocket(5000);) {
			System.out.println("서버 포트 번호 : 5000 으로 생성");
			
			// 내부 메서드 안에서 while문을 돌면서 클라이언트 측의 연결을 기다리고 있다.
			Socket socket = serverSocket.accept(); 
			// 여기 아래는 클라이언트 측과 양 끝단에 소켓이 서로 연결되어야 실행 흐름이 내려온다. 
			System.out.println("Client connected....");
			// 대상 - 소켓 --> 입력 스트림을 가져온다.
			// 양끝단의 소켓 : 클라이언트 ---> 서버 (stream)
			InputStream input = socket.getInputStream(); 
			// 메서드 호출값 = getter라서 변수명 설정
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			// input은 바이트 기반으로 들어오는 InputStream이다. 
			String message = reader.readLine(); // 한줄 단위로 데이터를 읽어라(문자 기반)
			System.out.println("Received : " + message);
			
			// 기본 소켓은 클라이언트가 연결되어야 생성된다. 
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
