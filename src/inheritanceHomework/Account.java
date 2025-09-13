package inheritanceHomework;

public class Account extends Information {
    int balance;

    public Account(String name, int birth) {
        super(name, birth);
        this.balance = 0;
    }

    // 입금
    public void deposit(int amount) {
        balance += amount;
        System.out.println(amount + "원을 입금하였습니다. 현재 잔액: " + balance + "원");
    }

    // 출금
    public void withdraw(int amount) {
        if (amount <= balance) {
            balance -= amount;
            System.out.println(amount + "원을 출금하였습니다. 현재 잔액: " + balance + "원");
        } else {
            System.out.println(amount + "원을 출금하려 했으나 잔액이 부족합니다.");
        }
    }

    // 잔액 확인
    public void checkBalance() {
        System.out.println("현재 잔액: " + balance + "원");
    }
}
