package snowgame;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import javazoom.jl.player.Player;

public class Music extends Thread {
    private Player player; // JLayer를 이용한 MP3 재생을 위한 변수
    private boolean isLoop; // 반복 여부를 판단하는 변수
    private File file;
    private FileInputStream fis;
    private BufferedInputStream bis;

    public Music(String name, boolean isLoop) { // 파일 이름과 반복 여부를 받는 생성자
        try {
            this.isLoop = isLoop;
            file = new File(snow_present.class.getResource("../music/" + name).toURI());
            fis = new FileInputStream(file); // 파일 입력 스트림 생성
            bis = new BufferedInputStream(fis); // 버퍼 입력 스트림 생성
            player = new Player(bis);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    } // Music

    public int getTime() { // 현재 재생 시간을 반환하는 메서드
        if (player == null) 
            return 0;
        return player.getPosition();
    } // getTime

    public void close() { // 음악 재생을 종료하는 메서드
        isLoop = false;
        player.close();
        this.interrupt(); // 스레드 종료
    } // close

    @Override
    public void run() {
        try {
            do {
                player.play();
                fis = new FileInputStream(file); // 파일 입력 스트림 재생
                bis = new BufferedInputStream(fis); // 버퍼 입력 스트림 생성
                player = new Player(bis);
            } while (isLoop); // isLoop가 true인 동안 반복
        } catch (Exception e) {
            System.out.println(e.getMessage()); // 예외 발생 시 메시지 출력
        }
    } // run
}
