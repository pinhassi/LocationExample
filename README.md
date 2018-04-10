# LocationExample
This small app demonstrates Location services usage.

App.java - extends Android Application and used to get ApplicationContext and Handler from anywhere in the app
AppActivity.java - is a parrent Activity to all other activities in the app and used to request location services permissions

MainActivity.java -
 - Extends AppActivity and implements MyLocation.Listener
 - Starts the MyLocation with continues updates at onCreate method (usually should be done once in the app)
 - Adds itself as listener at onResume
 - removes itself as listener at onPause
 - Stops MyLocation at onDestroy, to prevent receiving updates when the last activity is closed (usually should be done once in the app)

