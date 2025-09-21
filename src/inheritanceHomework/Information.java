package inheritanceHomework;

import java.time.LocalDate;

public class Information {
    String name;
    String personalCode;
    String date;

    public Information(String name, String personalCode) {
        this.name = name;
        this.personalCode = personalCode;
        this.date = LocalDate.now().toString();
    }

    public void printInfo() {
        System.out.println(name + "님 안녕하세요! 은행 서비스를 시작하겠습니다.");
        System.out.println("접속 날짜: " + date);
    }
}
