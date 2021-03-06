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
        return customer.getWallet().transfer(payment, this.wallet);
    }
}
