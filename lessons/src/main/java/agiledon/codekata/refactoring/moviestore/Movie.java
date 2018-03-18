package agiledon.codekata.refactoring.moviestore;

public class Movie {
    public static final int REGULAR = 0;
    public static final int NEW_RELEASE = 1;
    public static final int CHILDREN = 2;

    private String title;
    private int priceCode;

    public Movie(String title, int priceCode) {
        this.title = title;
        this.priceCode = priceCode;
    }

    public int getPriceCode() {
        return priceCode;
    }

    public void setPriceCode(int arg) {
        priceCode = arg;
    }

    public String getTitle() {
        return title;
    }

    public int getPrice(int days) { // each.getDaysRented
        int thisAmount = 0;
        switch (this.getPriceCode()) {
            case Movie.REGULAR:
                thisAmount += 2;
                if (days > 2) thisAmount += (days - 2) * 1.5;
                break;
            case Movie.NEW_RELEASE:
                thisAmount += days * 3;
                break;
            case Movie.CHILDREN:
                thisAmount += 1.5;
                if (days > 3) thisAmount += (days - 3) * 1.5;
                break;
        }
        return thisAmount;
    }

    public int getPoints(int daysRented) {
        // add bonus for a two day new release rental
        if ((this.getPriceCode() == Movie.NEW_RELEASE) && daysRented > 1) return 2;
        return 1;
    }
}
