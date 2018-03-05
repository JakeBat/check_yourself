package edu.cnm.deepdive.checkyourself;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import edu.cnm.deepdive.checkyourself.models.Category;
import edu.cnm.deepdive.checkyourself.models.Record;
import edu.cnm.deepdive.checkyourself.models.Record.Display;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SpendingFragment extends Fragment {

  private FloatingActionButton spendingAdd;
  private ListView spending;

  public SpendingFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    final View view = inflater.inflate(R.layout.fragment_spending, container, false);
    spending = view.findViewById(R.id.spending);

    spendingAdd = view.findViewById(R.id.spending_add);
    spendingAdd.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        SpendingDialog spendingDialog = new SpendingDialog();
        spendingDialog.spendingDialog(view);

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
        final ArrayAdapter<Display> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, records);
        getActivity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            spending.setAdapter(adapter);
          }
        });
      }
    }).start();
  }

  private class SpendingDialog {

    public void spendingDialog(View view) {
      AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

      LayoutInflater inflater = getLayoutInflater();
      final View dialogView = inflater.inflate(R.layout.spending_dialog,null);

      builder.setView(dialogView);

      final Spinner tagSpinner = dialogView.findViewById(R.id.tag_spinner);
      final EditText amountEdit = dialogView.findViewById(R.id.amount_edit);
      final EditText infoEdit = dialogView.findViewById(R.id.info_edit);
      Button spendingSubmit = dialogView.findViewById(R.id.spending_submit);

      new Thread(new Runnable() {
        @Override
        public void run() {
          List<Category> tags = ((MainActivity)getActivity()).getDatabase(getContext()).categoryDao().getAll();
          final ArrayAdapter<Category> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, tags);
          tagSpinner.setAdapter(adapter);
        }
      }).start();


      final Record record = new Record();

      final AlertDialog dialog = builder.create();

      spendingSubmit.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          final double amountValue = Double.parseDouble(amountEdit.getText().toString());
          final String infoValue = infoEdit.getText().toString();

          new Thread(new Runnable() {
            @Override
            public void run() {
              record.setTag_id(((Category) tagSpinner.getSelectedItem()).getId());
              record.setAmount(amountValue);
              record.setInfo(infoValue);
              ((MainActivity)getActivity()).getDatabase(getContext()).recordDao().insert(record);
            }
          }).start();
          updateDisplay();
          dialog.dismiss();
        }
      });

      dialog.show();
    }

  }
}
