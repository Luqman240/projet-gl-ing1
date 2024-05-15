package com.example.cybooks.model;

import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * This class represents the loan made by the library user.
 * It contains informations on the loan such as the loanID, userID, bookID, loanDate, dueDate, returnDate.
 * It provides getter/setters methods and also methods to create a loan, check its validity, and end the loan.
 */

public class Loan {
    private int loanID;
    private int userID;
    private int bookID;
    private LocalDate loanDate;
    private int numberOfDays;
    private LocalDate dueDate;
    private LocalDate returnDate = null;
    private boolean isReturned = false;
    
    /**
     * Create a new loan with the specified information.
     * 
     * @param loanID The loan's ID.
     * @param userID The user's ID.
     * @param bookID The book's ID.
     * @param loanDate The date where the loan started.
     * @param numberOfDays The length of the loan wanted, starting from the loanDate.
     * @param dueDate The date that the book should be returned.
     * @param returnDate The date the book has been returned.
     * @param isReturned Boolean that describe if the book has been returned.
    */

    public Loan(int userID, int bookID, int numberOfDays){
        this.bookID = bookID;
        LocalDate currentDate = LocalDate.now();
        this.loanDate = currentDate;
        this.numberOfDays = numberOfDays;
        this.dueDate = loanDate.plusDays(numberOfDays);
    }

    /**
     * Gets the loan's ID.
     * 
     * @return The loan's ID.
     */

    public int getLoanID() {
        return loanID;
    }

    /**
     * Sets the loan's ID.
     *
     */

    public void setLoanID(int loanID) {
        this.loanID = loanID;
    }

    /**
     * Gets the user's ID.
     * 
     * @return The user's ID.
     */

    public int getUserID() {
        return userID;
    }

    /**
     * Sets the user's ID.
     * 
     */

    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * Gets the book's ID.
     * 
     * @return The book's ID.
     */

    public int getBookID() {
        return bookID;
    }

    /**
     * Sets the book's ID.
     */

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    /**
     * Gets the loan's Date.
     * 
     * @return The loan's creation date.
     */

    public LocalDate getLoanDate() {
        return loanDate;
    }

    /**
     * Sets the loan's Date.
     */

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    /**
     * Gets the due date.
     * 
     * @return The due date.
     */

    public LocalDate getDueDate() {
        return dueDate;
    }
    
    /**
     * Sets the due date.
     */

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the return date.
     * 
     * @return The return date.
     */

    public LocalDate getReturnDate() {
        return returnDate;
    }

    /**
     * Sets the return date.
     */

    public void setReturnDate(LocalDate returnDate){
        this.returnDate = returnDate;
    }

    /**
     * Gets the return status.
     * 
     * @return The return status.
     */

    public boolean getIsReturned() {
        return isReturned;
    }

    /**
     * Sets the return status.
     */

    public void setIsReturned(boolean isReturned) {
        this.isReturned = isReturned;
    }

    /**
     * Check if the loan is still valid.
     * 
     * @return True if the loan is still valid, false otherwise.
     */

    public boolean isValid() {
        LocalDate currentDate = LocalDate.now();
        return !isReturned && dueDate.isBefore(currentDate);
    }
}
