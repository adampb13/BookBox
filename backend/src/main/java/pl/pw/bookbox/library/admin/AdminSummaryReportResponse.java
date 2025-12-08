// backend/src/main/java/pl/pw/bookbox/library/admin/AdminSummaryReportResponse.java
package pl.pw.bookbox.library.admin;

import java.util.List;

public class AdminSummaryReportResponse {

    private long totalUsers;
    private long totalBooks;
    private long totalLoans;
    private long activeLoans;
    private long overdueLoans;
    private List<TopBookDto> topBooks;

    public AdminSummaryReportResponse(long totalUsers,
                                      long totalBooks,
                                      long totalLoans,
                                      long activeLoans,
                                      long overdueLoans,
                                      List<TopBookDto> topBooks) {
        this.totalUsers = totalUsers;
        this.totalBooks = totalBooks;
        this.totalLoans = totalLoans;
        this.activeLoans = activeLoans;
        this.overdueLoans = overdueLoans;
        this.topBooks = topBooks;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getTotalBooks() {
        return totalBooks;
    }

    public long getTotalLoans() {
        return totalLoans;
    }

    public long getActiveLoans() {
        return activeLoans;
    }

    public long getOverdueLoans() {
        return overdueLoans;
    }

    public List<TopBookDto> getTopBooks() {
        return topBooks;
    }
}
