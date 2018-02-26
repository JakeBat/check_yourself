package edu.cnm.deepdive.checkyourself;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer.GridStyle;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

  public static final int MAX_VALUE = 100;

  public HomeFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_home, container, false);

    GraphView graph = (GraphView) view.findViewById(R.id.graph);
    graph.getViewport().setMinY(0);
    graph.getViewport().setMaxY(MAX_VALUE);
    graph.getViewport().setYAxisBoundsManual(true);
    graph.getGridLabelRenderer().setGridStyle(GridStyle.NONE);
    BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
        new DataPoint(0, 67),
        new DataPoint(1, 98),
        new DataPoint(2, 56),
        new DataPoint(3, 21),
        new DataPoint(4, 47)
    });
    graph.addSeries(series);

    StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
    staticLabelsFormatter.setHorizontalLabels(new String[] {"Tag1", "Tag2", "Tag3", "Tag4", "Tag5"});
    graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
    graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
    graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);

// styling
    series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
      @Override
      public int get(DataPoint data) {
        int color;
        double percentValue = ((data.getY() / MAX_VALUE) * 100);
        if (percentValue > 70) {
          color = Color.GREEN;
        } else if (percentValue < 70 && percentValue > 30) {
          color = Color.YELLOW;
        } else {
          color = Color.RED;
        }
        return color;
      }
    });

    series.setSpacing(60);

//// draw values on top
//    series.setDrawValuesOnTop(true);
//    series.setValuesOnTopColor(Color.RED);
//    series.setValuesOnTopSize(50);

    return view;
  }

}
