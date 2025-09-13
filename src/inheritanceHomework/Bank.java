package inheritanceHomework;
import java.util.Scanner;

public class Bank {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 정보
        System.out.print("이름 입력: ");
        String name = scanner.nextLine();
        System.out.print("주민번호 앞자리 입력(6자리): ");
        int birth = scanner.nextInt();

        Limit myAccount = new Limit(name, birth);
        myAccount.printInfo();

        // 메뉴
        while (true) {
            System.out.println("--------------------------------------------");
            System.out.println("1. 입금 | 2. 출금 | 3. 잔액 확인 | 4. 종료");
            System.out.println("--------------------------------------------");
            System.out.print("번호 선택: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("입금액을 입력하세요: ");
                    int depositAmount = scanner.nextInt();
                    myAccount.deposit(depositAmount);
                    break;
                case 2:
                    System.out.print("출금액을 입력하세요: ");
                    int withdrawAmount = scanner.nextInt();
                    myAccount.limitedWithdraw(withdrawAmount);
                    break;
                case 3:
                    myAccount.checkBalance();
                    break;
                case 4:
                    System.out.println("시스템을 종료합니다.");
                    return;
                default:
                    System.out.println("올바른 선택이 아닙니다. 다시 선택해주세요.");
            }
        }
    }
}