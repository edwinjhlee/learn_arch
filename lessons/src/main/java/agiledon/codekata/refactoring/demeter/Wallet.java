package agiledon.codekata.refactoring.demeter;

public class Wallet {
    private float value;

    public float getTotalMoney() {
        return value;
    }

    public void setTotalMoney(float newValue) {
        value = newValue;
    }

    public void addMoney(float deposit) {
        value += deposit;
    }

    public void subtractMoney(float debit) {
        value -= debit;
    }

    public boolean transfer(float payment, Wallet targetWallet) {
        if (this.getTotalMoney() < payment) return false;

        this.subtractMoney(payment);
        targetWallet.addMoney(payment);
        return true;
    }
}
