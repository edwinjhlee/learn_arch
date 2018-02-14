package agiledon.codekata.refactoring.demeter;

public class Paperboy {
    //    private Customer myCustomer;

    //    public void pay(float payment) {
    //        Wallet theWallet = myCustomer.getWallet();
    //        if (theWallet.getTotalMoney() > payment) {
    //            theWallet.subtractMoney(payment);
    //        } else {
    //            //money not enough
    //        }
    //    }

    private Wallet wallet = new Wallet();

    public boolean getPaid(Customer customer, float payment) {
        final Wallet w = customer.getWallet();
        if (w.getTotalMoney() < payment) return false;

        w.subtractMoney(payment);
        this.wallet.addMoney(payment);
        return true;
    }
}
