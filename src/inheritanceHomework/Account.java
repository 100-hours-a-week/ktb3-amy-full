package inheritanceHomework;

import java.util.concurrent.locks.ReentrantLock;

public class Account extends WhichBank {
    int balance;
    Information owner;
    public ReentrantLock lock = new ReentrantLock();

    public Account(String bankName, Information owner) {
        super(bankName);
        this.owner = owner;
        this.balance = 0;
    }

    // 입금
    public synchronized void deposit(int amount) {
        lock.lock();
        try {
            if (amount <= 0) {
                System.out.println("[에러!!] 입금액은 0원보다 커야 합니다.");
                return;
            }
            balance += amount;
            System.out.println(amount + "원을 입금하였습니다. 현재 잔액: " + balance + "원");
        } finally {
            lock.unlock();
        }
    }

    // 출금
    public synchronized void withdraw(int amount) {
        lock.lock();
        try {
            if (amount <= 0) {
                System.out.println("[에러] 출금액은 0원보다 커야 합니다.");
                return;
            }
            if (amount <= balance) {
                balance -= amount;
                System.out.println(amount + "원을 출금하였습니다. 현재 잔액: " + balance + "원");
            } else {
                System.out.println("[실패] 잔액이 부족합니다.");
            }
        } finally {
            lock.unlock();
        }
    }

    // 잔액 확인
    public synchronized void checkBalance() {
        lock.lock();
        try {
            System.out.println("현재 잔액: " + balance + "원");
        } finally {
            lock.unlock();
        }
    }
}
