package inheritanceHomework;

public class Thief extends Thread {
    public Account account;

    public Thief(Account account) {
        this.account = account;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(15000);
                int steal = 1000;
                if (account.balance >= steal) {
                    account.balance -= steal;
                    System.out.println("[도둑이야!!] 누군가 당신의 계좌에서 " + steal + " 원을 훔쳐갔습니다!");
                } else {
                    System.out.println("[도둑이야!!] 돈이 없어서 그냥 갔습니다...");
                }
            }
        } catch (InterruptedException e){
                System.out.println("도둑이 그냥 도망가버렸습니다!");
        }
    }
}