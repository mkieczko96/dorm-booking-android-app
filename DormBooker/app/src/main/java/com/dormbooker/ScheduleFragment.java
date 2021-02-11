package com.dormbooker;

import androidx.fragment.app.Fragment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends Fragment {

    private LocalDate selectedDate;
    private DateTimeFormatter monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM");
    
}