package edu.cnm.deepdive.checkyourself;


import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridLayout.Alignment;
import android.widget.GridLayout.LayoutParams;
import android.widget.GridLayout.Spec;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import edu.cnm.deepdive.checkyourself.dao.BudgetDao;
import edu.cnm.deepdive.checkyourself.models.Budget;
import edu.cnm.deepdive.checkyourself.models.Category;
import edu.cnm.deepdive.checkyourself.models.Record;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class InputFragment extends Fragment implements TextWatcher {

  private EditText incomeEdit;
  private EditText familyEdit;
  private EditText savingsEdit;
  private EditText monthlyEdit;
  private TextView totalEdit;
  private double incomeValue;
  private int familyValue;
  private int savingsValue;
  private double monthlyValue;
  private double totalValue;
  private Button updateButton;

  public InputFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_input, container, false);
    incomeEdit = view.findViewById(R.id.income_edit);
    familyEdit = view.findViewById(R.id.family_edit);
    savingsEdit = view.findViewById(R.id.savings_edit);
    monthlyEdit = view.findViewById(R.id.monthly_edit);
    totalEdit = view.findViewById(R.id.total_edit);
    updateButton = view.findViewById(R.id.update_button);
    incomeEdit.addTextChangedListener(this);
    familyEdit.addTextChangedListener(this);
    savingsEdit.addTextChangedListener(this);
    monthlyEdit.addTextChangedListener(this);

    new Thread(new Runnable() {
      @Override
      public void run() {
        List<Budget> budgetList = ((MainActivity) getActivity()).getDatabase(getContext())
            .budgetDao().getAll();
        if (!budgetList.isEmpty()) {
          final Budget budget = ((MainActivity) getActivity()).getDatabase(getContext()).budgetDao()
              .getFirst();
          getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
              incomeEdit.setText(String.format("%.2f", budget.getIncome()));
              familyEdit.setText(String.format("%d", budget.getFamilySize()));
              savingsEdit.setText(String.format("%d", budget.getPercentSavings()));
              monthlyEdit.setText(String.format("%.2f", budget.getMonthlyPayments()));

            }
          });
        }
      }
    }).start();

    updateButton.setOnClickListener(new OnClickListener() {

      Budget budget = new Budget();

      @Override
      public void onClick(View v) {
        new Thread(new Runnable() {
          @Override
          public void run() {
            List<Budget> budgetList = ((MainActivity) getActivity()).getDatabase(getContext())
                .budgetDao().getAll();
            if (!budgetList.isEmpty()) {
              budget = budgetList.get(0);
            }
            budget.setIncome(incomeValue);
            budget.setFamilySize(familyValue);
            budget.setPercentSavings(savingsValue);
            budget.setMonthlyPayments(monthlyValue);
            if (budgetList.isEmpty()) {
              ((MainActivity) getActivity()).getDatabase(getContext()).budgetDao().insert(budget);
            } else {
              ((MainActivity) getActivity()).getDatabase(getContext()).budgetDao()
                  .updateBudget(budget);
            }
          }
        }).start();
      }
    });

    return view;
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
      totalValue = (incomeValue - monthlyValue) * (1 - (savingsValue / 100));

      totalEdit.setText(String.format("%.2f", totalValue));

    } catch (NumberFormatException e) {
      e.printStackTrace();
      Toast.makeText(getActivity(), "Invalid Input", Toast.LENGTH_LONG).show();
    }
  }

  public class MoneyTextWatcher implements TextWatcher {
    private final WeakReference<EditText> editTextWeakReference;

    public MoneyTextWatcher(EditText editText) {
      editTextWeakReference = new WeakReference<EditText>(editText);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
      EditText editText = editTextWeakReference.get();
      if (editText == null) return;
      String s = editable.toString();
      if (s.isEmpty()) return;
      editText.removeTextChangedListener(this);
      String cleanString = s.replaceAll("[$,.]", "");
      BigDecimal parsed = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
      String formatted = NumberFormat.getCurrencyInstance().format(parsed);
      editText.setText(formatted);
      editText.setSelection(formatted.length());
      editText.addTextChangedListener(this);
    }
  }
}
