package inheritanceHomework;

public class Limit extends Account {
    private static final int withdrawLimit = 1000000;

    public Limit(String bankName, Information owner) {
        super(bankName, owner);
    }

    // 출금 한도
    public void limitedWithdraw(int amount) {
        if (amount > withdrawLimit) {
            System.out.println("출금 실패하였습니다. 출금 한도(" + withdrawLimit + "원) 초과!");
        } else {
            withdraw(amount);
        }
    }
}
