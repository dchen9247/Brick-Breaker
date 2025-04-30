package com.example.project5

import java.util.TimerTask

class GameTimerTask: TimerTask{
    private lateinit var activity : MainActivity
    constructor( activity : MainActivity ) {
        this.activity = activity
    }

    override fun run() {
        activity.updateModel( )
        activity.updateView( )
    }
}