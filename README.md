# Read before you go

1. Gradle speed-up
    - https://guides.gradle.org/performance/
    - https://medium.com/hackernoon/speed-up-gradle-build-in-android-studio-80a5f74ac9ed
1. Android configuration
    - https://www.bignerdranch.com/blog/android-studio-live-templates/
    - https://medium.com/google-developer-experts/configuring-android-studio-4aa4f54f1153
1. Build and Debugging speed-up
    - https://docs.genymotion.com/desktop/3.1/02_Application/021_Configuring_Genymotion.html#set-up-a-proxy
1. Plugins
    - https://blog.jetbrains.com/idea/2015/04/a-curated-list-of-ide-plugins-for-android-development/
    - https://www.sitepoint.com/top-8-plugins-android-studio/
    - https://github.com/avast/android-butterknife-zelezny
1. Libraries
    - https://blog.mindorks.com/awesome-android-open-source-libraries-56a008c776c0
1. Google resources
    - https://developer.android.com/guide/topics/ui/look-and-feel
    - https://material.io/resources/icons/?style=round
    - https://github.com/googlesamples
1. Other
    - https://medium.com/@cesarmcferreira/mastering-the-terminal-side-of-android-development-e7520466c521
    - https://www.youtube.com/user/androiddevelopers
    - https://github.com/android/architecture-samples
    - https://blog.mindorks.com/essential-guide-for-designing-your-android-app-architecture-mvp-part-1-74efaf1cda40
    - https://medium.com/android-bits/android-app-from-scratch-part-1-model-view-presenter-b5f629f2d9a1
    - https://medium.com/@MiBLT/refactoring-to-mvp-b504a3774ffd
    - https://github.com/futurice/android-best-practices
    - https://github.com/ribot/android-guidelines/blob/master/project_and_code_guidelines.md
    - https://github.com/greenrobot/EventBus
    - https://github.com/amitshekhariitbhu/Android-Debug-Database
    - https://square.github.io/leakcanary/
    - https://github.com/PhilJay/MPAndroidChart
    - https://greenrobot.org/greendao/
    - https://github.com/google/gson
    - https://github.com/kizitonwose/CalendarView

# Business Requirements
Create mobile application that will allow users - living in a dorm - to book in-dorm facilities like laundry room, gym, fitness room, etc. Users should be able to log into application using their credentials to WIKAMP if possible. 

## Personal Data - Account Management
Users should not be allowed to create accounts by their own. Users are not allowed to change their room number nor e-mail address by themselves. Users can change their GDPR preferences. 

## Bookings
User can create bookings to facilities on every floor, except of landry room - user can book laundry room only on his floor. Number of maximum bookings should be calculated automatically by checking number of default duration slots for each facility between 6:00 and 00:00 through the month. User should not create more bookings for specific facility that is allowed. User can see all of his bookings and modify them before they start. User can prolong booking when it already started but before it ends only if there is no next appointment created. User can transfer booking to another user after agreement of both sides. User can preview schedule of selected facility to find empty slot in the schedule. Booking in facility schedule has room number visible. User can report issues with facility during the booking like: mess, there was a party, device damage. All bookings that are finished should be moved to archive after an hour from their ending.
