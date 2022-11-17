package com.example.reactiongame;

import java.util.LinkedHashMap;
import java.util.Map;

public class Options {

    public final static Map<String, Integer> gridSizes = new LinkedHashMap<String, Integer>() {{
        put("3x3", 3);
        put("4x4", 4);
        put("5x5", 5);
    }};

    public final static Map<String, Integer> timers = new LinkedHashMap<String, Integer>() {{
        put("15 sec", 15);
        put("30 sec", 30);
        put("45 sec", 45);
    }};

    public final static Map<String, FrameRateRange> frameRates = new LinkedHashMap<String, FrameRateRange>() {{
        put("1-1.5 sec", new FrameRateRange(1, 1.5));
        put("0.7-1 sec", new FrameRateRange(0.7, 1));
        put("0.5-0.7 sec", new FrameRateRange(0.5, 0.7));
    }};

    public static class FrameRateRange {

        public FrameRateRange(double range_begin, double range_end)
        {
            this.range_begin = range_begin;
            this.range_end = range_end;
        }
        public double range_begin;
        public double range_end;
    }
}
