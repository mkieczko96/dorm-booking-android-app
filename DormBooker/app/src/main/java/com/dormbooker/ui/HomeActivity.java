package com.dormbooker.ui;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.dormbooker.R;
import com.dormbooker.databinding.ActivityHomeBinding;
import com.dormbooker.databinding.CalendarDayBinding;
import com.kizitonwose.calendarview.CalendarView;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.kizitonwose.calendarview.ui.ViewContainer;
import com.kizitonwose.calendarview.utils.Size;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private LocalDate selectedDate;
    private DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd");
    private DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE");
    private DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = getWindowManager();
        wm.getDefaultDisplay().getMetrics(dm);

        final CalendarView calendar = binding.exSevenCalendar;

        int width = (dm.widthPixels - (int)dm.density *16) / 5;
        Size size = new Size(width, (int)(width*1.25));
        calendar.setDaySize(size);

        selectedDate = LocalDate.now();

        class DayViewContainer extends ViewContainer {

            final CalendarDayBinding calendarBinding;
            CalendarDay calendarDay;

            public DayViewContainer(View view) {
                super(view);
                calendarBinding = CalendarDayBinding.bind(view);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        calendar.smoothScrollToDate(calendarDay.getDate().minusDays(2));

                        if(selectedDate != calendarDay.getDate()) {
                            LocalDate oldDate = selectedDate;
                            selectedDate = calendarDay.getDate();
                            calendar.notifyDateChanged(calendarDay.getDate());
                            calendar.notifyDateChanged(oldDate);
                        }
                    }
                });
            }

            public void bind(CalendarDay day) {
                this.calendarDay = day;
                calendarBinding.exSevenDateText.setText(dateFormatter.format(day.getDate()));
                calendarBinding.exSevenDayText.setText(dayFormatter.format(day.getDate()));
                calendarBinding.exSevenMonthText.setText(monthFormatter.format(day.getDate()));

                calendarBinding.exSevenDateText.setTextColor(getView().getContext().getColor(formatter.format(day.getDate()).equals(formatter.format(selectedDate))? R.color.colorYellow : R.color.colorWhite));
                calendarBinding.exSevenSelectedView.setVisibility(formatter.format( day.getDate()).equals(formatter.format(selectedDate)) ? View.VISIBLE : View.GONE);
            }
        }

        calendar.setDayBinder(new DayBinder<DayViewContainer>() {
            @Override
            public DayViewContainer create(View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(DayViewContainer viewContainer, CalendarDay calendarDay) {
                viewContainer.bind(calendarDay);
            }
        });

        YearMonth currentMonth = YearMonth.now();
        calendar.setup(currentMonth, currentMonth.plusMonths(12), DayOfWeek.MONDAY);
        calendar.smoothScrollToDate(LocalDate.now().minusDays(2));

    }
}