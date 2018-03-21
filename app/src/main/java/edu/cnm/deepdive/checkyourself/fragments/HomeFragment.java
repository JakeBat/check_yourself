package edu.cnm.deepdive.checkyourself.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer.GridStyle;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import edu.cnm.deepdive.checkyourself.maps.MapActivity;
import edu.cnm.deepdive.checkyourself.MainActivity;
import edu.cnm.deepdive.checkyourself.R;
import edu.cnm.deepdive.checkyourself.models.Record.Display;
import edu.cnm.deepdive.checkyourself.models.Total;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

  private static final int MAX_VALUE = 100;
  private static final int GREEN_THRESHOLD = 70;
  private static final int RED_THRESHOLD = 30;

  private List<String> labels = new ArrayList<>();
  private TextView foodLeft;
  private TextView monthlyLeft;
  private TextView enterLeft;
  private TextView miscLeft;
  private Button mapButton;


  public HomeFragment() {

  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_home, container, false);
    mapButton = view.findViewById(R.id.map_button);
    graphSetup(view);
    setupAmountsLeft(view);

    mapButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getActivity(), MapActivity.class);
        startActivity(intent);
      }
    });
    return view;
  }

  private void graphSetup(final View view) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        List<Display> sums = ((MainActivity) Objects.requireNonNull(getActivity()))
            .getDatabase(getContext()).recordDao()
            .getSums();
        List<Total> totals = ((MainActivity) getActivity()).getDatabase(getContext())
            .totalDao().getAll();

        GraphView graph = view.findViewById(R.id.graph);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(MAX_VALUE);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(5);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setGridStyle(GridStyle.NONE);

        DataPoint[] dataPoints = new DataPoint[sums.size()];
        for (int i = 0; i < sums.size(); i++) {
          double sum = 100 + ((sums.get(i).getAmount() / totals.get(i).getTotal()) * 100);
          dataPoints[i] = new DataPoint((i + 1), sum);
        }
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoints);
        graph.addSeries(series);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        labels.add("");
        for (Display sum : sums) {
          String label = sum.getTag();
          labels.add(label);
        }
        labels.add("");
        staticLabelsFormatter.setHorizontalLabels(labels.toArray(new String[labels.size()]));

        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);

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
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.WHITE);
        series.setValuesOnTopSize(50);
      }
    }).start();
  }

  private void setupAmountsLeft(View view) {
    foodLeft = view.findViewById(R.id.food_left);
    monthlyLeft = view.findViewById(R.id.monthly_left);
    enterLeft = view.findViewById(R.id.enter_left);
    miscLeft = view.findViewById(R.id.misc_left);

    new Thread(new Runnable() {
      @Override
      public void run() {
        final List<Display> sums = ((MainActivity) Objects.requireNonNull(getActivity()))
            .getDatabase(getContext()).recordDao()
            .getSums();
        final List<Total> totals = ((MainActivity) getActivity()).getDatabase(getContext())
            .totalDao().getAll();
        getActivity().runOnUiThread(new Runnable() {
          @SuppressLint("DefaultLocale")
          @Override
          public void run() {
            if (!sums.isEmpty()) {
              foodLeft.setText(
                  String.format("%.2f", totals.get(0).getTotal() + sums.get(0).getAmount()));
              monthlyLeft.setText(
                  String.format("%.2f", totals.get(1).getTotal() + sums.get(1).getAmount()));
              enterLeft.setText(
                  String.format("%.2f", totals.get(2).getTotal() + sums.get(2).getAmount()));
              miscLeft.setText(
                  String.format("%.2f", totals.get(3).getTotal() + sums.get(3).getAmount()));
            }
          }
        });
      }
    }).start();
  }
}
