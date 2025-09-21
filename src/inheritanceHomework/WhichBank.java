package inheritanceHomework;

public class WhichBank {
    String bankName;

    public WhichBank(String bankName) {
        this.bankName = bankName;
    }

    public void printBank() {
        System.out.println("은행명: " + bankName);
    }
}
