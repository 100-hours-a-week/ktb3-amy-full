package inheritanceHomework;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Bank {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 정보
        System.out.print("이름 입력: ");
        String name = scanner.nextLine();
        System.out.print("주민번호 앞자리 입력(6자리): ");
        String personalCode = scanner.nextLine();

        Information info = new Information(name, personalCode);

        // 은행명
        System.out.print("은행을 선택하세요 (토스뱅크/우리은행/신한은행/농협은행/카카오뱅크 등등): ");
        String bankName = scanner.nextLine();

        // 출금 한도
        Limit myAccount = new Limit(bankName, info);

        info.printInfo();
        myAccount.printBank();

        //도둑!!
        Thief thief = new Thief(myAccount);
        thief.start();

        // 메뉴
        while (true) {
            System.out.println("--------------------------------------------");
            System.out.println("1. 입금 | 2. 출금 | 3. 잔액 확인 | 4. 종료");
            System.out.println("--------------------------------------------");
            System.out.print("번호 선택: ");
            int choice = 0;

            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("[에러] 숫자를 입력해야 합니다!");
                scanner.nextLine();
                continue;
            }

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