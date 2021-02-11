package com.dormbooker;

import android.view.View;

import com.dormbooker.databinding.ActivityHomeBinding;
import com.dormbooker.databinding.CalendarDayBinding;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.ui.ViewContainer;

public class DayViewContainer extends ViewContainer {

    CalendarDayBinding calendarBinding;
    ActivityHomeBinding homeBinding;
    CalendarDay calendarDay;


    public DayViewContainer(View view) {
        super(view);
        calendarBinding = CalendarDayBinding.bind(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
