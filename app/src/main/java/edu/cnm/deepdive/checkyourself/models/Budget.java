package edu.cnm.deepdive.checkyourself.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Budget {

  @PrimaryKey(autoGenerate = true)
  private long id;

  @ColumnInfo(name = "income")
  private double income;

  @ColumnInfo(name = "family_size")
  private int familySize;

  @ColumnInfo(name = "percent_savings")
  private int percentSavings;

  @ColumnInfo(name = "monthly_payments")
  private double monthlyPayments;

  @ColumnInfo(name = "spending_total")
  private double spendingTotal;

  @ColumnInfo(name = "food_total")
  private double foodTotal;

  @ColumnInfo(name = "enter_total")
  private double enterTotal;

  @ColumnInfo(name = "misc_total")
  private double miscTotal;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public double getIncome() {
    return income;
  }

  public void setIncome(double income) {
    this.income = income;
  }

  public int getFamilySize() {
    return familySize;
  }

  public void setFamilySize(int familySize) {
    this.familySize = familySize;
  }

  public int getPercentSavings() {
    return percentSavings;
  }

  public void setPercentSavings(int percentSavings) {
    this.percentSavings = percentSavings;
  }

  public double getMonthlyPayments() {
    return monthlyPayments;
  }

  public void setMonthlyPayments(double monthlyPayments) {
    this.monthlyPayments = monthlyPayments;
  }

  public double getSpendingTotal() {
    return spendingTotal;
  }

  public void setSpendingTotal(double spendingTotal) {
    this.spendingTotal = spendingTotal;
  }

  public double getFoodTotal() {
    return foodTotal;
  }

  public void setFoodTotal(double foodTotal) {
    this.foodTotal = foodTotal;
  }

  public double getEnterTotal() {
    return enterTotal;
  }

  public void setEnterTotal(double enterTotal) {
    this.enterTotal = enterTotal;
  }

  public double getMiscTotal() {
    return miscTotal;
  }

  public void setMiscTotal(double miscTotal) {
    this.miscTotal = miscTotal;
  }
}
