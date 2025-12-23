// @ If you want your Chronometer to be persistent (i.e. to keep counting after app restart), do these steps:
// https://tinyurl.com/PersistentChronometer

// @ Add to the Activity, in which you want to listen to Chronometer ticking:

import android.widget.Chronometer

// @ Make that Activity implementing the Chronometer.OnChronometerTickListener interface, for example:

class MainActivity : AppCompatActivity(), Chronometer.OnChronometerTickListener {

// @ Add to the Activity an implementation of the onChronometerTick() function of that interface:

Code: Select all
    override fun onChronometerTick(chronometer: Chronometer?) {
        // Do whatever when the chronometer achieves some time:
        when (chronometer.text) {
            "30:00" -> {...} // "30:00:0" if the Chronometer displays tenths of seconds
            "45:00" -> {...}
            "01:00:00" -> {...}
        }
    }

// @ Add to onCreate() of the Activity:

myChronometer.onChronometerTickListener = this

// IF YOU HAVE MORE THAN ONE CHRONOMETER:

// @ In onCreate() of the Activity, register them all:

firstChronometer.onChronometerTickListener = this
secondChronometer.onChronometerTickListener = this

// @ Create function xxxxxxxChronometerTicked() for each Chronometer, for example:

    private fun firstChronometerTicked() {
        // Do whatever when the chronometer achieves some time:
        when (firstChronometer.text) {
            "30:00" -> {...}
            "45:00" -> {...}
            "01:00:00" -> {...}
        }
    }

// @ Call all these functions from onChronometerTick():

    override fun onChronometerTick(chronometer: Chronometer?) {
        when (chronometer!!.id) {
            firstChronometer.id -> firstChronometerTicked()
            secondChronometer.id -> secondChronometerTicked()
        }
    }