package edu.cnm.deepdive.checkyourself.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Budget {

  @PrimaryKey(autoGenerate = true)
  private int id;

  @ColumnInfo(name = "income")
  private int income;

  @ColumnInfo(name = "amountToSpend")
  private String amountToSpend;

  @ColumnInfo(name = "amountSpent")
  private String amountSpent;

  @ColumnInfo(name = "familySize")
  private int familySize;

  @ColumnInfo(name = "percentSavings")
  private double percentSavings;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getIncome() {
    return income;
  }

  public void setIncome(int income) {
    this.income = income;
  }

  public String getAmountToSpend() {
    return amountToSpend;
  }

  public void setAmountToSpend(String amountToSpend) {
    this.amountToSpend = amountToSpend;
  }

  public String getAmountSpent() {
    return amountSpent;
  }

  public void setAmountSpent(String amountSpent) {
    this.amountSpent = amountSpent;
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
}
