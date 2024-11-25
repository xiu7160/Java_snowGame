package snowgame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class game_Frame extends JFrame implements KeyListener, Runnable {
    public void start() { // 게임 시작
        addKeyListener(this); // 키 리스너 추가
        th = new Thread(this); // 스레드 생성
        th.start(); // 스레드 시작
    }
    
    ArrayList<Snow> Snow_arr = new ArrayList<>();
    ArrayList<Gift> Gift_arr = new ArrayList<>();
    Random rnd = new Random();
    
    static int xx, yy; // 화면 좌표
    int random, f_width, f_height, x, y, xw, xh;
    int cc = 0;
    int cnt;
    int score, count, Snow_count = 0;
    int speed = 1000;
    int level = 1;
    int difficult = 1500;
    int Hp = 4;
    double Snow_speed;
    boolean KeyLeft, KeyRight, KeySpace, start, end, cr = false;
    
    Thread th; // 스레드 변수
    Toolkit tk = Toolkit.getDefaultToolkit();
    Image buffImage, Sfloor_img, Gift_img;
    Image Hp_img, Snow_img, me_img;
    
    Graphics buffg, screenGraphic;
    Gift gift;
    Snow snow;
    Font a;
    Music introMusic;

    public game_Frame() {
        init();
        start();
        setTitle("눈송이와 선물 피하기 게임!");
        setSize(f_width, f_height);
        Dimension screen = tk.getScreenSize();
        int f_xpos = (int) (screen.getWidth() / 2 - f_width / 2);
        int f_ypos = (int) (screen.getHeight() / 2 - f_height / 2);
        setLocation(f_xpos, f_ypos);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
    }

    public void init() { // 초기화
        x = 450; // 캐릭터 위치
        y = 470;
        xw = 100;
        xh = 100;
        f_width = 1000;
        f_height = 600;
        Gift_img = new ImageIcon("image/선물상자2.png").getImage();
        me_img = new ImageIcon("image/멈춤.png").getImage();
        Snow_img = new ImageIcon("image/눈송이3.png").getImage();
        Hp_img = new ImageIcon("image/생명.png").getImage();
    }


	public void run() { // 게임 루프
	    try {
	        int S_count = 0;
	        while (true) {
	            random = rnd.nextInt(10);
	            Thread.sleep(20);
	            if (start) {
	                keyControl();
	                SnowProcess();
	                GiftProcess();
	                HpProcess();
	                if (S_count > speed) { // 스노우 생성 주기
	                    enCreate();
	                    S_count = 0;
	                }
	                if (cr && cc + 80 < count) {
	                    cr = false; // 추가 점수(+30) 표시
	                }
	                S_count += 10;
	                score = count;
	                count++;
	                if (count > difficult) { // 특정 점수 이상일 때 난이도 증가
	                    S_count += 7; // 스노우 생성 간격 단축
	                    difficult *= 1.5;
	                    level++;
	                    speed -= 70;
	                }
	            }
	            repaint(); // 화면 갱신
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public boolean Crash(int x1, int y1, int x2, int y2, Image img1, Image img2) { // 충돌 체크
	    boolean check = false;
	    if (Math.abs((x1 + img1.getWidth(null) / 2) - (x2 + img2.getWidth(null) / 2)) < (img2.getWidth(null) / 2
	            + img1.getWidth(null) / 2)
	        && Math.abs((y1 + img1.getHeight(null) / 2)
	                - (y2 + img2.getHeight(null) / 2)) < (img2.getHeight(null) / 2 + img1.getHeight(null) / 2)) {
	        // 두 이미지의 중심 좌표가 겹치는지 체크
	        check = true; // 충돌 발생
	    } else {
	        check = false; // 충돌 없음
	    }
	    return check;
	}
	
	public void enCreate() { // 스노우 생성
	    double rx;
	    double ry;
	    for (int i = 0; i < level + 2; i++) { // 현재 레벨에 따라 스노우 생성
	        rx = Math.random() * (f_width - xw); // x 좌표 랜덤
	        ry = Math.random() * 150; // y 좌표 랜덤
	        Snow en = new Snow((int) rx, (int) ry - 100);
	        Snow_arr.add(en);
	    }
	    int rand = level + 3;
	    if (random < rand) {
	        rx = Math.random() * (f_width - xw); // x 좌표 랜덤
	        ry = Math.random() * 150; // y 좌표 랜덤
	        Gift gift = new Gift((int) rx, (int) ry - 100);
	        Gift_arr.add(gift);
	    }
	}
	
	public void paint(Graphics g) { // 화면 그리기
	    buffImage = createImage(f_width, f_height);
	    buffg = buffImage.getGraphics();
	    update(g);
	}
	
	public void update(Graphics g) { 
	    Draw_Char();
	    if (cr) {
	        Draw_plus();
	    }
	    SnowProcess();
	    GiftProcess();
	    HpProcess();
	    draw();
	    g.drawImage(buffImage, 0, 0, this);
	}
	
	public void Draw_plus() { // 추가 점수 표시
	    if (end) {
	        yy = 0;
	    }
	    buffg.setColor(Color.red);
	    buffg.setFont(new Font("Default", Font.BOLD, 20)); // 기본 글꼴
	    buffg.drawString(" +30", xx, yy);
	}
	
	public void draw() { // 점수와 상태 표시
	    buffg.setColor(Color.white);
	    buffg.setFont(new Font("Default", Font.BOLD, 20));
	    buffg.drawString("점수 : " + score, 800, 70);
	    buffg.drawString("게임 시작 : Enter", 800, 90);
	    if (end) {
	        end();
	    }
	}
	public void end() { // 게임 종료 처리
	    buffg.setColor(Color.red);
	    me_img = new ImageIcon("image/눈맞음.png").getImage();
	    buffg.drawString("SCORE : " + score, 100, 290); // 최종 점수 표시
	    buffg.drawString("G A M E   O V E R !!", 100, 250); // 게임 오버 메시지
	    count = 0; // 카운트 리셋
	    difficult = 1500; // 난이도 리셋
	    level = 1; // 레벨 초기화
	    speed = 1000; // 속도 초기화
	    Hp = 4; // 생명 초기화
	    Snow_arr.clear(); // 스노우 배열 초기화
	    Gift_arr.clear(); // 선물 배열 초기화
	    introMusic.close(); // 음악 종료
	    cnt = 0; // 카운트 초기화
	    
	    if (cnt == 0) {
	        introMusic.close(); // 음악 종료
	    }
	}
	
	public void GiftProcess() { // 선물 처리
	    for (int i = 0; i < Gift_arr.size(); i++) {
	        gift = (Gift) (Gift_arr.get(i));
	        buffg.drawImage(Gift_img, gift.x, gift.y, this);
	        gift.move();
	
	        if (gift.y > 560) {
	            Gift_arr.remove(i); // 화면 밖으로 나간 선물 제거
	        }
	        for (int j = 0; j < Gift_arr.size(); ++j) { 
	            gift = (Gift) Gift_arr.get(j);
	            if (Crash(x, y, gift.x, gift.y, me_img, Gift_img)) {
	                Gift_arr.remove(i); // 선물 획득
	                cr = true; 
	                xx = gift.x; 
	                yy = gift.y; 
	                cc = count; 
	                Draw_plus(); 
	                count += 30; // 점수 증가
	            }
	        }
	    }
	}
	
	public void SnowProcess() { // 스노우 처리
	    for (int i = 0; i < Snow_arr.size(); i++) {
	        snow = (Snow) (Snow_arr.get(i));
	        buffg.drawImage(Snow_img, snow.x, snow.y, this);
	        snow.move();
	        
	        if (Crash(x, y, snow.x, snow.y, me_img, Snow_img)) { // 캐릭터와 스노우 충돌 체크
	            if (snow.y > 560)
	                continue;
	            Snow_arr.remove(i); // 스노우 제거
	            Hp--; // 생명 감소
	            if (Hp == 0) {
	                // 게임 오버 처리
	                end = true;
	                start = false;
	            }
	        }
	    }
	}
	public void HpProcess() { // HP 표시
	    if (Hp == 4) {
	        buffg.drawImage(Hp_img, 30, 50, this);
	        buffg.drawImage(Hp_img, 65, 50, this);
	        buffg.drawImage(Hp_img, 100, 50, this);
	    } else if (Hp == 3) {
	        buffg.drawImage(Hp_img, 30, 50, this);
	        buffg.drawImage(Hp_img, 65, 50, this);
	    } else if (Hp == 2) {
	        buffg.drawImage(Hp_img, 30, 50, this);
	    }
	}
	
	public void Draw_Char() { // 캐릭터 그리기
	    buffg.clearRect(0, 0, f_width, f_height);
	    buffg.drawImage(me_img, x, y, this);
	}
	
	public void keyPressed(KeyEvent e) { // 키 누름 이벤트
	    switch (e.getKeyCode()) {
	        case KeyEvent.VK_LEFT: // 왼쪽 화살표
	            KeyLeft = true;
	            if (start && !end)
	                me_img = new ImageIcon("image/왼쪽.png").getImage();
	            break;
	        case KeyEvent.VK_RIGHT: // 오른쪽 화살표
	            KeyRight = true;
	            if (start && !end)
	                me_img = new ImageIcon("image/오른쪽.png").getImage();
	            break;
	        case KeyEvent.VK_ENTER: // 엔터키
	            cnt++;
	            if (cnt == 1) {
	                introMusic = new Music("jingle5.mp3", false); 
	                introMusic.start();
	            }
	            start = true;
	            me_img = new ImageIcon("image/멈춤.png").getImage();
	            end = false;
	            break;
	    }
	}
	
	public void keyReleased(KeyEvent e) { // 키 떼기 이벤트
	    switch (e.getKeyCode()) {
	        case KeyEvent.VK_LEFT:
	            KeyLeft = false;
	            me_img = new ImageIcon("image/왼쪽.png").getImage();
	            break;
	        case KeyEvent.VK_RIGHT:
	            KeyRight = false;
	            me_img = new ImageIcon("image/오른쪽.png").getImage();
	            break;
	    }
	}
	
	public void keyTyped(KeyEvent e) {
	    // 키 입력 처리
	}
	
	public void keyControl() {
	    if (0 < x) {
	        if (KeyLeft)
	            x -= 5; // 왼쪽으로 이동
	    }
	    if (f_width > x + xw) {
	        if (KeyRight)
	            x += 5; // 오른쪽으로 이동
	    }
	}

	class Snow { // 눈송이 클래스
	    int x;
	    int y;
	    public Snow(int x, int y) {
	        this.x = x;
	        this.y = y;
	    }
	    public void move() {
	        y++; // 아래로 이동
	    }
	}
	
	class Gift { // 선물 클래스
	    int x;
	    int y;
	    public Gift(int x, int y) {
	        this.x = x;
	        this.y = y;
	    }
	    public void move() {
	        y++; // 아래로 이동
	    }
	}
}
