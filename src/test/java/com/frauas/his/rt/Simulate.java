package com.frauas.his.rt;

import com.frauas.his.rt.controller.WheelController;
import com.frauas.his.rt.models.Wheel;
import com.frauas.his.rt.utils.Calculation;
import com.frauas.his.rt.utils.Constants;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class Simulate {

    @Test
    public void Test1() {
        double v = Calculation.convertKmphToMps(180);
        double coeff = Constants.STATIC_FRICTION_DRY_ROAD;
        double sd = Calculation.calculateStoppingDistance(v, coeff);
        double d = Calculation.calculateDeceleration(v, sd);
        System.out.println("STOPPING DISTANCE: " + sd);
        System.out.println("Deceleration: " + d);
        System.out.println("Stopping time: " + Calculation.calculateStoppingTime(v, Constants.STATIC_FRICTION_DRY_ROAD));
    }

    @Test
    public void Test2() {
//        int roadConditions[] = new int[]{0, 1, 2};
//        double roadDistances[] = new double[]{50, 60, 200};
        double initialVelocity = Calculation.convertKmphToMps(Double.parseDouble("180"));
        Assert.assertEquals(50.0, initialVelocity, 0.0);

        double initialVelocity1 = Calculation.convertMpsToKmph(Double.parseDouble("50"));
        Assert.assertEquals(180.0, initialVelocity1, 0.0);
    }

    @Test
    public void TimeTest() {
        long time = 6870865464l;
        System.out.println(TimeUnit.NANOSECONDS.toSeconds(time));
        System.out.println(TimeUnit.NANOSECONDS.toMillis(time));
//        System.out.println(TimeUnit.NANOSECONDS.toMinutes(time));
        System.out.println(TimeUnit.SECONDS.convert(time, TimeUnit.NANOSECONDS));
        System.out.println(time / 1000000000.0d);
    }


    @Test
    public void Test3(){
        int[] arr = {5,4,3,2,1,};

        for (int i : arr) {
            System.out.println(i);
        }
    }

}
