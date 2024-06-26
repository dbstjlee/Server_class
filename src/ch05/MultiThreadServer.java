package ch05;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;

// 함수로 분리하기
public class MultiThreadServer {

	// 메인 함수
	public static void main(String[] args) {

		System.out.println("==== 서버 실행 ====");

		// 서버측 소켓을 만들기 위한 준비물
		// 서버 소켓, 포트 번호

		try (ServerSocket serverSocket = new ServerSocket(5000)) {
			// 클라이언트 대기를 타는 중 --> 연결 요청이 오면 --> 소켓 객체를 생성함.(단, 클라이언트와 연결된 상태)
			Socket socket = serverSocket.accept();
			System.out.println("------ client connected -----");
			// 런타임 시점에 연결되면
			// 클라이언트와 통신을 위한 스트림을 설정(대상 소켓을 얻었기 때문임)
			BufferedReader readerStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// 클라이언트에게 보낼 Stream
			PrintWriter writerStream = new PrintWriter(socket.getOutputStream(), true);

			// 키보드 스트림 준비(문자 기반)
			BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));

			// 스레드를 시작합니다.
			startReadThread(readerStream); // <-- main join() 
			startWriteThread(writerStream, keyboardReader);
			
			// waitForThreadToEnd() 메서드 쓰지말기 <-- join() : 안정적인 종료 위해서  -> 굳이 안 써도 됨.

			System.out.println("main 스레드 작업 완료...");

		} catch (Exception e) {
			e.printStackTrace();
		}

	} // end of main

	// 클라이언트로부터 데이터를 읽는 스레드 분리
	// 소켓(대상) <---- 스트림을 얻어야 한다. 데이터를 읽는 객체는?? <--- 문자기반(BufferedReader)
	private static void startReadThread(BufferedReader bufferedReader) {

		Thread readThread = new Thread(() -> {
			try {
				String clientMessage;
				while ((clientMessage = bufferedReader.readLine()) != null) {
					// 서버측 콘솔에 클라이언트가 보낸 문자 데이터 출력
					System.out.println("클라이언트에서 온 MSG : " + clientMessage);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}); // run() 메서드 안
		readThread.start(); // 스레드 실행 -> run() 메서드 진행
		// 메인 스레드 대기 처리 --> join() --> 고민 --> 2번 반복될듯?
		// join() 호출 -> readThread 가 종료될 때까지 메인 스레드를 대기시킴
		//waitForThreadToEnd(readThread);  // readThread가 join을 결면 

	}

	// 서버 측에서 --> 클라이언트로 데이터를 보내는 기능
	private static void startWriteThread(PrintWriter printWriter, BufferedReader keyboardReader) {
		// 대상은 키보드, 문자기반으로 받을 거임. -> keyboardreader
		// printWriter = 출력, keyboardreader = 키보드로 메시지 입력
		Thread writeThread = new Thread(() -> {
			try {
				String serverMessage;
				while ((serverMessage = keyboardReader.readLine()) != null) {
					printWriter.println(); // 한줄 단위로 보냄
					printWriter.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		writeThread.start();
		//waitForThreadToEnd(writeThread);
		// 메인 스레드 대기
	}

	// 워커 스레드가 종료될 때까지 기다리는 메서드
	private static void waitForThreadToEnd(Thread thread) { // 대상 스레드만 매개변수에 넣으면 됨
		try {
			thread.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

} // end of class