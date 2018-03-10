package com.frauas.his.rt.listeners;

import com.frauas.his.rt.models.Output;
import org.jfree.data.xy.XYDataset;

import java.util.List;

public interface ChartHelper {
    public XYDataset createDataset(List<Output> data, String title);
}
