package edu.cnm.deepdive.checkyourself.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import edu.cnm.deepdive.checkyourself.MainActivity;
import edu.cnm.deepdive.checkyourself.R;
import edu.cnm.deepdive.checkyourself.UniDatabase;
import edu.cnm.deepdive.checkyourself.models.Category;
import edu.cnm.deepdive.checkyourself.models.Record;
import edu.cnm.deepdive.checkyourself.models.Record.Display;
import java.util.List;
import java.util.Objects;

/**
 * <code>SpendingFragment</code> is comprised entirely of a <code>ListView</code> that reads
 * from the database to display a list of the users transactions.
 */
public class SpendingFragment extends Fragment {

  private ListView spending;
  private double amountValue;

  /**
   * Default constructor for the fragment.
   */
  public SpendingFragment() {

  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    final View view = inflater.inflate(R.layout.fragment_spending, container, false);
    spending = view.findViewById(R.id.spending);

    FloatingActionButton spendingAdd = view.findViewById(R.id.spending_add);
    spendingAdd.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        SpendingDialog spendingDialog = new SpendingDialog();
        spendingDialog.spendingDialog();

      }
    });

    updateDisplay();

    return view;
  }

  private void updateDisplay() {
    new Thread(new Runnable() {
      @Override
      public void run() {

        List<Display> records = UniDatabase.getInstance(getContext()).recordDao().getSummary();
        final ArrayAdapter<Display> adapter = new ArrayAdapter<>(getActivity(),
            android.R.layout.simple_list_item_1, records);
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
          @Override
          public void run() {
            spending.setAdapter(adapter);
          }
        });
      }
    }).start();
  }

  private class SpendingDialog {

    /**
     * Creates an <code>AlertDialog</code> that allows the user to input transactions and
     * store them in the database for display in the <code>SpendingFragment</code>.
     */
    public void spendingDialog() {
      AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

      LayoutInflater inflater = getLayoutInflater();
      @SuppressLint("InflateParams") final View dialogView = inflater
          .inflate(R.layout.spending_dialog, null);

      builder.setView(dialogView);

      final Spinner tagSpinner = dialogView.findViewById(R.id.tag_spinner);
      final Spinner signSpinner = dialogView.findViewById(R.id.sign_spinner);
      final EditText amountEdit = dialogView.findViewById(R.id.amount_edit);
      final EditText infoEdit = dialogView.findViewById(R.id.info_edit);
      Button spendingSubmit = dialogView.findViewById(R.id.spending_submit);
      Button spendingCancel = dialogView.findViewById(R.id.spending_cancel);

      new Thread(new Runnable() {
        @Override
        public void run() {
          String[] signs = {"-", "+"};
          List<Category> tags = ((MainActivity) Objects.requireNonNull(getActivity()))
              .getDatabase(getContext()).categoryDao().getAll();
          final ArrayAdapter<Category> tagAdapter = new ArrayAdapter<>(getActivity(),
              android.R.layout.simple_spinner_dropdown_item, tags);
          final ArrayAdapter<String> signAdapter = new ArrayAdapter<>(getActivity(),
              android.R.layout.simple_spinner_dropdown_item, signs);
          tagSpinner.setAdapter(tagAdapter);
          signSpinner.setAdapter(signAdapter);
        }
      }).start();

      final Record record = new Record();

      final AlertDialog dialog = builder.create();

      spendingSubmit.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          amountValue = Double.parseDouble(amountEdit.getText().toString());
          final String infoValue = infoEdit.getText().toString();
          if (signSpinner.getSelectedItem().toString().equals("-")) {
            amountValue = -amountValue;
          }

          new Thread(new Runnable() {
            @Override
            public void run() {
              record.setTag_id(((Category) tagSpinner.getSelectedItem()).getId());
              record.setAmount(amountValue);
              record.setInfo(infoValue);
              ((MainActivity) Objects.requireNonNull(getActivity())).getDatabase(getContext())
                  .recordDao().insert(record);
            }
          }).start();
          updateDisplay();
          dialog.dismiss();
        }
      });

      spendingCancel.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          dialog.dismiss();
        }
      });

      dialog.show();
    }
  }
}
