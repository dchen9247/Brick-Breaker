package com.example.project5

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.view.View

class GameView: View {
    private lateinit var paint : Paint
    private lateinit var paddleRect : Rect

    private lateinit var game : BrickBreaker

    constructor( context : Context ) : super( context ) {

    }

    constructor(context : Context, width : Int, height : Int ) : super( context ) {
        paint = Paint()
        paint.strokeWidth = 20f
        paint.isAntiAlias = true

        game = BrickBreaker(width.toFloat(), height.toFloat(), 20f, 200f, 30f)
    }

    fun getGame() : BrickBreaker {
        return game
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(Color.WHITE)

        val rowColors = listOf(Color.RED, Color.GREEN, Color.YELLOW, Color.CYAN)
        for ((index, brick) in game.getBricks().withIndex()) {
            if (!brick.hit) {
                paint.color = brick.color
                canvas.drawRect(brick.rect, paint)
            }
        }

        game.getPaddleRect()?.let {
            paint.color = Color.BLUE
            canvas.drawRect(it, paint)
        }

        game.getBallCenter()?.let {
            paint.color = Color.BLACK
            canvas.drawCircle(it.x.toFloat(), it.y.toFloat(), game.getBallRadius(), paint)
        }

        if (game.isGameOver()) {
            paint.color = Color.BLACK
            paint.textSize = 50f
            canvas.drawText("Game Over", game.getScreenWidth() / 2f - 150, game.getScreenHeight()/ 2f, paint)
            canvas.drawText("Bricks hit: ${game.getScore()}", game.getScreenWidth() / 2f - 180, game.getScreenHeight() / 2f + 60, paint)
            canvas.drawText("Bricks left: ${game.bricksLeft()}", game.getScreenWidth() / 2f - 180, game.getScreenHeight() / 2f + 120, paint)
            canvas.drawText("Best score: ${game.getBestScore()}", game.getScreenWidth() / 2f - 180, game.getScreenHeight() / 2f + 180, paint)
        }
    }
}