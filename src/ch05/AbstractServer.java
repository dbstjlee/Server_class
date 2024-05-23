package ch05;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

// 상속의 활용
public abstract class AbstractServer {

	private ServerSocket serverSocket;
	private Socket socket;
	private BufferedReader readerStream;
	private PrintWriter writerStream;
	private BufferedReader keyboardReader;

	// set 메서드
	// 메서드 의존 주입(멤버 변수의 참조 변수 할당)
	protected void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	// 메서드 의존 주입(멤버 변수의 참조 변수 할당)
	protected void setSocket(Socket socket) {
		this.socket = socket;
	}

	// get 메서드
	protected ServerSocket getServerSocket() {
		return this.serverSocket;
	}

	// 실행의 흐름이 필요하다.(순서가 중요) (자식 클래스에서 오버라이드 불가)
	public final void run() {
		// 1. 서버 셋팅 - 포트 번호 할당
		try {
			setupServer();
			connection();
			setupStream();
			startService(); // 내부적으로 while 동작

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			cleanup();
		}
	}

	// 1. 포트 번호 할당(구현 클래스에서 직접 설계)
	protected abstract void setupServer() throws IOException;

	// 2. 클라이언트 연결 대기 실행(구현 클래스)
	protected abstract void connection() throws IOException;

	// 3. 스트림 초기화(연결된 소켓에서 스트림을 뽑아야 함) - 여기서 함.
	private void setupStream() throws IOException {
		readerStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writerStream = new PrintWriter(socket.getOutputStream(), true);
		keyboardReader = new BufferedReader(new InputStreamReader(System.in));// 문자기반이라 Input사용
	}

	// 4. 서비스 시작
	private void startService() {
		// while <--- (while 문 돌면서 데이터 읽음)
		// while ---> (데이터 보냄)
		Thread readThread = createReadThread();
		Thread writeThread = createWriteThread();

		readThread.start();
		writeThread.start();
		
		try {
			readThread.join();
			writeThread.join();
			// main 스레드 잠깐 기다려
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 캡슐화
	private Thread createReadThread() {
		return new Thread(() -> {

			try {
				String msg;
				// (msg = readerStream.readLine()) 가 가장 큰 연산자 // 오 -> 왼 // readLine부터 돌아감.
				// readLine() == Scanner.nextLine(); <-- 무한 대시(사용자가 콘솔에 값 입력까지 대기)
				// 안녕 \n이 msg 변수에 담기고 null 이 아니라서 연산의 결과는 true가 됨. 
				// -> 내부안으로 들어가서 콘솔창에 msg를 출력함.  -> 조건식 다시 확인 -> readline()
				// -> 클라이언트가 보내기 전까지 대기상태(멈춤)임.(readline 때문에) 
				// client와 소켓이 끊기면 null이되어 종료됨.
				// readerStream = 클라이언트와 연결된 소켓
				while ((msg = readerStream.readLine()) != null) {
					// 서버측 콘솔에 출력
					System.out.println("client 측 msg : " + msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		});
	}

	// 캡슐화
	private Thread createWriteThread() {
		return new Thread(() -> {
			try {
				String msg;
				// 서버측 키보드에서 데이터를 한줄 라인으로 읽음
				while ((msg = keyboardReader.readLine()) != null) {
					// 클라이언트와 연결된 소켓에다가 데이터를 보냄
					writerStream.println(msg); // 키보드에 읽어들였던 데이터를 보냄
					writerStream.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	// 캡슐화 - 소켓 자원 종료
	// 서버가 죽기 직전에 호출되어야 함. 또는 치명적인 오류가 있을 때 호출.
	private void cleanup() {
		try {
			if (socket != null) {
				socket.close();
			}
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
