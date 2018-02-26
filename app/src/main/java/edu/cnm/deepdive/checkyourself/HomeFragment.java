package edu.cnm.deepdive.checkyourself;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


  public HomeFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_home, container, false);

    GraphView graph = (GraphView) view.findViewById(R.id.graph);
    BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
        new DataPoint(0, 1),
        new DataPoint(1, 5),
        new DataPoint(2, 3),
        new DataPoint(3, 2),
        new DataPoint(4, 6)
    });
    graph.addSeries(series);

    StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
    staticLabelsFormatter.setHorizontalLabels(new String[] {"Tag1", "Tag2", "Tag3", "Tag4", "Tag5"});
    staticLabelsFormatter.setVerticalLabels(new String[] {"low", "middle", "high"});
    graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

// styling
    series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
      @Override
      public int get(DataPoint data) {
        return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
      }
    });

    series.setSpacing(50);

//// draw values on top
//    series.setDrawValuesOnTop(true);
//    series.setValuesOnTopColor(Color.RED);
//    series.setValuesOnTopSize(50);

    return view;
  }

}
