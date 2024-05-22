package ch02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFile {

	public static void main(String[] args) {
		// 준비물
		// 1. 서버 소켓이 필요하다.
		// 2. 포트 번호가 필요하다. (0 ~ 65535까지 존재)
		// 2.1 잘 알려진 포트 번호 : 주로 시스템 레벨 - 0 ~ 1023까지 사용
		// 2.2 등록 가능한 포트 : 1024 ~ 49151까지 등록 가능(권장)
		// 2.3 동적/사설 포트 번호 - 그 외 임시 사용을 위해 할당된다.

		// 지역 변수의 특성이 있음(지역변수라서)
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(5001); // ServerSocket 객체 생성 시 포트 번호 5001로 할당 받음.
			System.out.println("서버를 시작합니다 - 포트번호 : 5001");
			Socket socket = serverSocket.accept(); // while --> 돌면서 기다리고 있음.
			// 할당 받은 포트 번호에서 클라이언트의 데이터를 받기 위해 기다리는 중임.
			// while 문 돌다가 연결하면 (클라이언트와 연결되어있는)소켓을 리턴한다.(소켓이 연결된다.)
			System.out.println(">>> 클라이언트가 연결하였습니다. <<<");

			// 데이터를 전달받기 위해서는? 스트림이 필요하다.
			// 받아야 함. --> inputstream
			InputStream input = socket.getInputStream(); // 클라이언트의 데이터를 받은 socket이 input에 담겨짐.
			// 문자 기반 스트림 -> 기능의 확장
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			// 클라이언트의 데이터를 받은 socket이 담긴 input이 reader에 담김
			// 버퍼에서는 바로 안 받아져서 기반 스트림 리더를 익명 생성자로 호출함.
			// 실제 데이터를 읽는 행위가 필요하다.
			String message = reader.readLine(); // 한 줄씩 읽어진 데이터를 message에 담음.
			System.out.println("클라이언트 측 메세지 전달 받음 : " + message); // 한 줄씩 읽어진 데이터가 출력됨.

			// socket = serverSocket.accept()로 클라이언트와 연결된 소켓임.
			socket.close();// 클라이언트와 연결된 소켓을 닫아야 함.
			// 자원 아니라서 직접 닫기

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
