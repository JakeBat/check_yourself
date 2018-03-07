package edu.cnm.deepdive.checkyourself;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer.GridStyle;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import edu.cnm.deepdive.checkyourself.models.Budget;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

  public static final int MAX_VALUE = 100; // TODO Make dynamic using user input
  public static final int GREEN_THRESHOLD = 70;
  public static final int RED_THRESHOLD = 30;

  private ListView homeList;
  private ArrayAdapter listAdapter;
  private Budget budget;

  public HomeFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_home, container, false);

    graphSetup(view);

    return view;
  }

  private void graphSetup(final View view) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        budget = ((MainActivity) getActivity()).getDatabase(getContext()).budgetDao()
            .getFirst();

        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(MAX_VALUE);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(5);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setGridStyle(GridStyle.NONE);
        double total = budget.getSpendingTotal();
        double foodPercent = (100.00 * (budget.getFoodTotal() / total));
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
            new DataPoint(1, foodPercent),
            new DataPoint(2, 90),
            new DataPoint(3, 56),
            new DataPoint(4, 21),
        });
        graph.addSeries(series);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"", "Food", "Monthly", "Enter.", "Misc.", ""}); // TODO link to database
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);

// styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
          @Override
          public int get(DataPoint data) {
            int color;
            double percentValue = ((data.getY() / MAX_VALUE) * 100);
            if (percentValue > GREEN_THRESHOLD) {
              color = Color.GREEN;
            } else if (percentValue < GREEN_THRESHOLD && percentValue > RED_THRESHOLD) {
              color = Color.YELLOW;
            } else {
              color = Color.RED;
            }
            return color;
          }
        });

        series.setSpacing(50);

// draw values on top
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.WHITE);
        series.setValuesOnTopSize(50);
      }
    }).start();


    homeList = view.findViewById(R.id.home_list);
    String[] homeArray = {"item 1", "item 2", "item 3", "item 4", "item 5", "item 6", "item 7", "item 8", "item 9", "item 10", "item 11", "item 12"};
    ArrayList<String> homeArrayList = new ArrayList<>();
    homeArrayList.addAll(Arrays.asList(homeArray));
    listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, homeArrayList);
    homeList.setAdapter(listAdapter);

//    new Thread(new Runnable() {
//      @Override
//      public void run() {
//        foodAmount = UniDatabase.getInstance(getContext()).categoryDao().foodAmount();
//      }
//    }).start();
  }

}
