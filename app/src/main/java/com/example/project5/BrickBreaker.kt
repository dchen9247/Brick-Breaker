package com.example.project5

import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import kotlin.math.*

class BrickBreaker(
    private var screenWidth: Float,
    private var screenHeight: Float,
    private var ballRadius: Float,
    private var paddleWidth: Float,
    private var paddleHeight: Float,
    private var brickRows: Int = 4,
    private var brickCols: Int = 6
) {
    private var deltaTime: Int = 0 // in milliseconds
    private var ballCenter: Point? = null
    private var ballVelocity: Point? = null
    private var paddleRect: Rect? = null
    private var bricks: MutableList<Brick> = mutableListOf()
    private var score = 0
    private var bestScore = 0
    private var gameOver = false
    private var gameStarted = false
    private var ballSpeed: Float = 5f

    init {
        resetGame()
    }

    data class Brick(
        val rect: Rect,
        val color: Int,
        var hit: Boolean = false
    ) {
        fun contains(x: Int, y: Int): Boolean {
            return rect.contains(x, y)
        }
    }

    var onPaddleHit: (() -> Unit)? = null

    fun getScreenWidth() : Float {
        return screenWidth
    }

    fun getScreenHeight() : Float {
        return screenHeight
    }

    fun getBallRadius() : Float {
        return ballRadius
    }

    fun setDeltaTime(ms: Int) {
        if (ms > 0) deltaTime = ms
    }

    fun getDeltaTime(): Int {
        return deltaTime
    }

    fun getBallCenter(): Point? {
        return ballCenter
    }

    fun setBallCenter(point: Point) {
        ballCenter = point
    }

    fun getBallVelocity(): Point? {
        return ballVelocity
    }

    fun setBallVelocity(point: Point) {
        ballVelocity = point
    }

    fun getPaddleRect(): Rect? {
        return paddleRect
    }

    fun setPaddleRect(rect: Rect) {
        paddleRect = rect
    }

    fun getBricks(): List<Brick> {
        return bricks
    }

    fun getScore(): Int {
        return score
    }

    fun getBestScore(): Int {
        return bestScore
    }

    fun setBestScore(score: Int) {
        bestScore = score
    }

    fun isGameOver(): Boolean {
        return gameOver
    }

    fun setGameOver(over: Boolean) {
        gameOver = over
    }

    fun isGameStarted(): Boolean {
        return gameStarted
    }

    fun setGameStarted(started: Boolean) {
        gameStarted = started
    }

    fun resetGame() {
        val startX = (screenWidth / 2).toInt()
        val startY = (screenHeight / 2).toInt()
        ballCenter = Point(startX, startY)

        val angle = Math.PI / 4 * (if (kotlin.random.Random.nextBoolean()) 1 else -1)
        val vx = (cos(angle) * ballSpeed).toInt()
        val vy = (-sin(angle) * ballSpeed).toInt()
        ballVelocity = Point(vx, vy)

        val paddleLeft = (screenWidth / 2 - paddleWidth / 2).toInt()
        val paddleTop = (screenHeight - 100f).toInt()
        paddleRect = Rect(
            paddleLeft,
            paddleTop,
            paddleLeft + paddleWidth.toInt(),
            paddleTop + paddleHeight.toInt()
        )

        bricks.clear()
        val brickWidth = (screenWidth / brickCols).toInt()
        val brickHeight = (screenHeight / 12).toInt()

        val colorRows = listOf(
            listOf(Color.RED, Color.GREEN, Color.RED, Color.GREEN, Color.RED, Color.GREEN),
            listOf(Color.YELLOW, Color.LTGRAY, Color.YELLOW, Color.LTGRAY, Color.YELLOW, Color.LTGRAY),
            listOf(Color.BLUE, Color.MAGENTA, Color.BLUE, Color.MAGENTA, Color.BLUE, Color.MAGENTA),
            listOf(Color.GRAY, Color.CYAN, Color.GRAY, Color.CYAN, Color.GRAY, Color.CYAN,)
        )

        for (row in 0 until brickRows) {
            for (col in 0 until brickCols) {
                val left = col * brickWidth
                val top = row * brickHeight + 50
                val right = left + brickWidth
                val bottom = top + brickHeight
                val color = colorRows[row][col % brickCols]
                bricks.add(Brick(Rect(left, top, right, bottom), color))
            }
        }

        score = 0
        gameOver = false
        gameStarted = false
    }

    fun movePaddle(x: Float) {
        val halfWidth = (paddleRect?.width() ?: 0) / 2
        val newLeft = (x - halfWidth).toInt().coerceIn(0, (screenWidth - paddleWidth).toInt())
        val newTop = paddleRect?.top ?: 0
        paddleRect?.offsetTo(newLeft, newTop)
    }

    fun update() {
        if (!gameStarted || gameOver) return

        ballCenter?.offset(
            (ballVelocity!!.x * deltaTime / 16f).toInt(),
            (ballVelocity!!.y * deltaTime / 16f).toInt()
        )

        checkWallCollision()
        checkPaddleCollision()
        checkBrickCollision()

        if ((ballCenter!!.y - ballRadius) > screenHeight) {
            gameOver = true
        }
    }

    private fun checkWallCollision() {
        ballCenter?.let {
            if (it.x - ballRadius < 0 || it.x + ballRadius > screenWidth) {
                ballVelocity?.x = -(ballVelocity?.x ?: 0)
            }
            if (it.y - ballRadius < 0) {
                ballVelocity?.y = -(ballVelocity?.y ?: 0)
            }
        }
    }

    fun checkPaddleCollision() {
        val ball = ballCenter ?: return
        val paddle = paddleRect ?: return

        val ballRect = Rect(
            (ball.x - ballRadius).toInt(),
            (ball.y - ballRadius).toInt(),
            (ball.x + ballRadius).toInt(),
            (ball.y + ballRadius).toInt()
        )

        if (Rect.intersects(ballRect, paddle)) {
            ballVelocity?.y = -(ballVelocity?.y ?: 0)
            onPaddleHit?.invoke()
        }
    }

    private fun checkBrickCollision() {
        val ball = ballCenter ?: return

        val ballRect = Rect(
            (ball.x - ballRadius).toInt(),
            (ball.y - ballRadius).toInt(),
            (ball.x + ballRadius).toInt(),
            (ball.y + ballRadius).toInt()
        )

        for (brick in bricks) {
            if (!brick.hit && Rect.intersects(brick.rect, ballRect)) {
                brick.hit = true
                ballVelocity?.y = -(ballVelocity?.y ?: 0)
                score++
                break
            }
        }
    }

    fun bricksLeft(): Int {
        return bricks.count { !it.hit }
    }
}
