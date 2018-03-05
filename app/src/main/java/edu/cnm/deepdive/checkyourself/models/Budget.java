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

  @ColumnInfo(name = "familySize")
  private int familySize;

  @ColumnInfo(name = "percentSavings")
  private double percentSavings;

  @ColumnInfo(name = "monthlyPayments")
  private double monthlyPayments;

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

  public double getPercentSavings() {
    return percentSavings;
  }

  public void setPercentSavings(double percentSavings) {
    this.percentSavings = percentSavings;
  }

  public double getMonthlyPayments() {
    return monthlyPayments;
  }

  public void setMonthlyPayments(double monthlyPayments) {
    this.monthlyPayments = monthlyPayments;
  }
}
