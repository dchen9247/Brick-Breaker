package com.example.project5

import android.media.SoundPool
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var gameView: GameView
    private lateinit var pool: SoundPool
    private var hitSoundId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val poolBuilder = SoundPool.Builder()
        pool = poolBuilder.build()
        hitSoundId = pool.load(this, R.raw.hit, 1)

        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        gameView = GameView(this, width, height)
        setContentView(gameView)

        gameView.getGame().setBestScore(loadBestScore())
        gameView.getGame().onPaddleHit = {
            playHitSound()
        }
        val timer = Timer()
        val task = GameTimerTask(this)
        timer.schedule(task, 0, 16)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val game = gameView.getGame()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!game.isGameStarted()) {
                    game.setGameStarted(true)
                }
                game.movePaddle(event.x)
            }
            MotionEvent.ACTION_MOVE -> {
                game.movePaddle(event.x)
            }
        }
        return true
    }

    fun updateModel() {
        val game = gameView.getGame()
        game.setDeltaTime(16)
        game.update()
        if (game.isGameOver()) {
            if (game.getScore() > game.getBestScore()) {
                game.setBestScore(game.getScore())
                saveBestScore(game.getScore())
            }
        }
    }

    fun updateView() {
        gameView.invalidate()
    }

    fun playHitSound() {
        pool.play(hitSoundId, 1.0f, 1.0f, 0, 0, 1.0f)
    }

    private fun saveBestScore(score: Int) {
        val prefs = getSharedPreferences("brick_breaker_prefs", MODE_PRIVATE)
        prefs.edit().putInt("best_score", score).apply()
    }

    private fun loadBestScore(): Int {
        val prefs = getSharedPreferences("brick_breaker_prefs", MODE_PRIVATE)
        return prefs.getInt("best_score", 0)
    }
}
