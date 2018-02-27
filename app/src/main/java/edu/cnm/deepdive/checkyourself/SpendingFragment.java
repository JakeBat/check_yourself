package edu.cnm.deepdive.checkyourself;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import edu.cnm.deepdive.checkyourself.models.Record;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SpendingFragment extends Fragment {

  private ListView spending;
  private ArrayAdapter listAdapter;

  public SpendingFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    final View view = inflater.inflate(R.layout.fragment_spending, container, false);

//    spending = view.findViewById(R.id.spending);
//    String[] spendingArray = {"item 1", "item 2", "item 3", "item 4", "item 5", "item 6", "item 7", "item 8", "item 9", "item 10", "item 11", "item 12"};
//    ArrayList<String> spendingList = new ArrayList<>();
//    spendingList.addAll(Arrays.asList(spendingArray));
//    listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, spendingList);
//    spending.setAdapter(listAdapter);

    new Thread(new Runnable() {
      @Override
      public void run() {
//        Record record = new Record();
//        record.setTag("Food");
//        record.setAmount(-78.48);
//        ((MainActivity)getActivity()).getDatabase().recordDao().insert(record);

        List<Record> records = ((MainActivity)getActivity()).getDatabase().recordDao().getAll();
        Log.v("spending", records.toString());
        final ListAdapter adapter = new ArrayAdapter<Record>(getActivity(), android.R.layout.simple_list_item_1, records);
        getActivity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            ((ListView) view).setAdapter(adapter);
          }
        });
      }
    }).start();

    return view;
  }

}
