package edu.cnm.deepdive.checkyourself.fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import edu.cnm.deepdive.checkyourself.MainActivity;
import edu.cnm.deepdive.checkyourself.R;
import edu.cnm.deepdive.checkyourself.models.Budget;
import edu.cnm.deepdive.checkyourself.models.Total;
import java.util.List;
import java.util.Objects;

/**
 * <code>InputFragment</code> contains four <code>EditText</code> fields that take user
 * input values and stores them in the data base. It also contains five <code>TextView</code>'s
 * that read from the database to display total monthly amounts for categories of the budget.
 *
 * @author Jake Batchelor
 */
public class InputFragment extends Fragment implements TextWatcher {

  private EditText incomeEdit;
  private EditText familyEdit;
  private EditText savingsEdit;
  private EditText monthlyEdit;
  private TextView totalEdit;
  private TextView foodTotal;
  private TextView enterTotal;
  private TextView miscTotal;
  private TextView savingsTotal;
  private double incomeValue;
  private int familyValue;
  private int savingsValue;
  private double monthlyValue;
  private double totalValue;
  private double foodValue;
  private double enterValue;
  private double miscValue;
  private Budget budget = new Budget();
  private Total totalFood = new Total();
  private Total totalMonthly = new Total();
  private Total totalEnter = new Total();
  private Total totalMisc = new Total();

  /**
   * Default constructor for the fragment.
   */
  public InputFragment() {

  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_input, container, false);
    incomeEdit = view.findViewById(R.id.income_edit);
    familyEdit = view.findViewById(R.id.family_edit);
    savingsEdit = view.findViewById(R.id.savings_edit);
    monthlyEdit = view.findViewById(R.id.monthly_edit);
    totalEdit = view.findViewById(R.id.total_edit);
    Button updateButton = view.findViewById(R.id.update_button);
    foodTotal = view.findViewById(R.id.food_total);
    enterTotal = view.findViewById(R.id.enter_total);
    miscTotal = view.findViewById(R.id.misc_total);
    savingsTotal = view.findViewById(R.id.savings_total);
    incomeEdit.addTextChangedListener(this);
    familyEdit.addTextChangedListener(this);
    savingsEdit.addTextChangedListener(this);
    monthlyEdit.addTextChangedListener(this);

    updateDisplay();

    updateButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        new Thread(new Runnable() {
          @Override
          public void run() {
            List<Budget> budgetList = ((MainActivity) Objects.requireNonNull(getActivity()))
                .getDatabase(getContext())
                .budgetDao().getAll();
            List<Total> totalList = ((MainActivity) getActivity()).getDatabase(getContext())
                .totalDao().getAll();
            if (!budgetList.isEmpty() && !totalList.isEmpty()) {
              budget = budgetList.get(0);
              totalFood = totalList.get(0);
              totalMonthly = totalList.get(1);
              totalEnter = totalList.get(2);
              totalMisc = totalList.get(3);
            }
            budget.setIncome(incomeValue);
            budget.setFamilySize(familyValue);
            budget.setPercentSavings(savingsValue);
            budget.setMonthlyPayments(monthlyValue);
            budget.setSpendingTotal(totalValue);
            totalFood.setTotal(foodValue);
            totalMonthly.setTotal(monthlyValue);
            totalEnter.setTotal(enterValue);
            totalMisc.setTotal(miscValue);

            if (budgetList.isEmpty()) {
              ((MainActivity) getActivity()).getDatabase(getContext()).budgetDao().insert(budget);
              ((MainActivity) getActivity()).getDatabase(getContext()).totalDao()
                  .updateAll(totalFood, totalMonthly, totalEnter, totalMisc);
            } else {
              ((MainActivity) getActivity()).getDatabase(getContext()).budgetDao()
                  .updateBudget(budget);
              ((MainActivity) getActivity()).getDatabase(getContext()).totalDao()
                  .updateAll(totalFood, totalMonthly, totalEnter, totalMisc);
            }
          }
        }).start();
        updateDisplay();
      }
    });

    return view;
  }

  private void updateDisplay() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        List<Budget> budgetList = ((MainActivity) Objects.requireNonNull(getActivity()))
            .getDatabase(getContext())
            .budgetDao().getAll();
        List<Total> totalList = ((MainActivity) getActivity()).getDatabase(getContext())
            .totalDao().getAll();
        if (!budgetList.isEmpty() && !totalList.isEmpty()) {
          final Budget budget = ((MainActivity) getActivity()).getDatabase(getContext()).budgetDao()
              .getFirst();
          totalFood = totalList.get(0);
          totalMonthly = totalList.get(1);
          totalEnter = totalList.get(2);
          totalMisc = totalList.get(3);
          getActivity().runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
              incomeEdit.setText(String.format("%.2f", budget.getIncome()));
              familyEdit.setText(String.format("%d", budget.getFamilySize()));
              savingsEdit.setText(String.format("%d", budget.getPercentSavings()));
              monthlyEdit.setText(String.format("%.2f", budget.getMonthlyPayments()));
              totalEdit.setText(String.format("%.2f", budget.getSpendingTotal()));
              foodTotal.setText(String.format("%.2f", totalFood.getTotal()));
              enterTotal.setText(String.format("%.2f", totalEnter.getTotal()));
              miscTotal.setText(String.format("%.2f", totalMisc.getTotal()));
              savingsTotal.setText(
                  String.format("%.2f", budget.getIncome() * (budget.getPercentSavings() / 100.0)));
            }
          });
        }
      }
    }).start();
  }


  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {

  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {

  }

  @Override
  public void afterTextChanged(Editable s) {

    try {
      incomeValue = Double.parseDouble(incomeEdit.getText().toString());
      familyValue = Integer.parseInt(familyEdit.getText().toString());
      savingsValue = Integer.parseInt(savingsEdit.getText().toString());
      monthlyValue = Double.parseDouble(monthlyEdit.getText().toString());
      totalValue = ((incomeValue - monthlyValue) * (1.0 - (savingsValue / 100.0)));
      foodValue = (300.00 * familyValue);
      enterValue = ((totalValue - foodValue) * .3);
      miscValue = ((totalValue - foodValue) * .7);

    } catch (NumberFormatException e) {
//      e.printStackTrace();
//      Toast.makeText(getActivity(), "Invalid Input", Toast.LENGTH_LONG).show();
    }
  }
}
