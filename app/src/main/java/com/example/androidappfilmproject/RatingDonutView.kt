package com.example.androidappfilmproject

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.graphics.Typeface
import androidx.core.animation.doOnEnd

class RatingDonutView @JvmOverloads constructor(context: Context,
attrs: AttributeSet? = null
) : View(context, attrs) {
    //Овал для рисования сегментов прогресс бара
    // Овал для рисования сегментов прогресс бара
    private val oval = RectF()

    // Размеры и центр
    private var radius: Float = 0f
    private var centerX: Float = 0f
    private var centerY: Float = 0f

    // Толщина линии (в px)
    private var strokePx: Float = 4f

    // Целевой прогресс (0..100)
    private var targetProgress: Int = 50

    // Текущее отображаемое значение (для анимации)
    private var currentProgress: Int = 0

    // Текстовый размер (px)
    private var scaleSize = 60f

    // Краски
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val digitPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Аниматор
    private var animator: ValueAnimator? = null

    // Параметры анимации
    private var animateOnLoad = true
    private var animationDuration = 800

    init {
        // Получаем атрибуты
        attrs?.let {
            val a = context.theme.obtainStyledAttributes(it, R.styleable.RatingDonutView, 0, 0)
            try {
                // stroke читаем как dimension (px)
                strokePx = a.getDimension(R.styleable.RatingDonutView_stroke, strokePx)
                targetProgress = a.getInt(R.styleable.RatingDonutView_progress, targetProgress)
                animateOnLoad = a.getBoolean(R.styleable.RatingDonutView_animateOnLoad, true)
                animationDuration =
                    a.getInt(R.styleable.RatingDonutView_animationDuration, animationDuration)
            } finally {
                a.recycle()
            }
        }

        initPaints()

        // Если требуется — анимируем начальное появление от 0 до targetProgress
        if (animateOnLoad) {
            currentProgress = 0
            setProgressAnimated(targetProgress, animationDuration.toLong())
        } else {
            // без анимации просто установить текущее = target
            currentProgress = targetProgress
            updatePaintColors()
        }
    }

    private fun initPaints() {
        // фон
        circlePaint.apply {
            style = Paint.Style.FILL
            color = Color.DKGRAY
        }

        // strokePaint - стиль и толщина (цвет обновляется динамически)
        strokePaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = strokePx
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
        }

        // digitPaint для числа внутри
        digitPaint.apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 2f
            setShadowLayer(5f, 0f, 0f, Color.DKGRAY)
            textSize = scaleSize
            // Редактируем Typeface:
            typeface = Typeface.SANS_SERIF
            isAntiAlias = true
        }

        updatePaintColors()
    }

    private fun updatePaintColors() {
        val color = getPaintColor(currentProgress)
        strokePaint.color = color
        digitPaint.color = color
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val chosenWidth = chooseDimension(widthMode, widthSize)
        val chosenHeight = chooseDimension(heightMode, heightSize)

        val minSide = chosenWidth.coerceAtMost(chosenHeight)
        centerX = minSide / 2f
        centerY = minSide / 2f
        setMeasuredDimension(minSide, minSide)
    }

    private fun chooseDimension(mode: Int, size: Int) =
        when (mode) {
            MeasureSpec.AT_MOST, MeasureSpec.EXACTLY -> size
            else -> 300
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = (width.coerceAtMost(height) / 2f)
        // масштаб текста в зависимости от размера
        scaleSize = radius * 0.6f
        digitPaint.textSize = scaleSize
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawRating(canvas)
        drawText(canvas)
    }

    private fun drawRating(canvas: Canvas) {
        val scale = radius * 0.8f
        canvas.save()
        canvas.translate(centerX, centerY)
        // учтём толщину линии, чтобы арка не обрезалась
        val halfStroke = strokePx / 2f
        oval.set(-scale + halfStroke, -scale + halfStroke, scale - halfStroke, scale - halfStroke)
        // фон
        canvas.drawCircle(0f, 0f, radius, circlePaint)
        // динамически обновляем цвет перед отрисовкой
        updatePaintColors()
        // рисуем арку по текущему (анимированному) прогрессу
        canvas.drawArc(oval, -90f, convertProgressToDegrees(currentProgress), false, strokePaint)
        canvas.restore()
    }

    private fun drawText(canvas: Canvas) {
        val message = String.format("%.1f", currentProgress / 10f)
        // измеряем ширину текста
        val textWidth = digitPaint.measureText(message)
        val fm = digitPaint.fontMetrics
        val textHeight = (fm.descent - fm.ascent)
        // рисуем в центре
        canvas.drawText(message, centerX - textWidth / 2f, centerY + textHeight / 4f, digitPaint)
    }

    private fun getPaintColor(progress: Int): Int = when (progress) {
        in 0..25 -> Color.parseColor("#e84258")
        in 26..50 -> Color.parseColor("#fd8060")
        in 51..75 -> Color.parseColor("#fee191")
        else -> Color.parseColor("#b0d8a4")
    }

    private fun convertProgressToDegrees(progress: Int): Float = progress * 3.6f

    /** Установить прогресс без анимации (0..100) */
    fun setProgress(pr: Int) {
        animator?.cancel()
        targetProgress = pr.coerceIn(0, 100)
        currentProgress = targetProgress
        updatePaintColors()
        invalidate()
    }

    /** Установить прогресс с анимацией */
    fun setProgressAnimated(pr: Int, durationMs: Long = 600L) {
        animator?.cancel()
        targetProgress = pr.coerceIn(0, 100)
        animator = ValueAnimator.ofInt(0, targetProgress).apply {
            duration = durationMs
            interpolator = DecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                currentProgress = valueAnimator.animatedValue as Int
                // обновляем цвет и перерисовываем
                updatePaintColors()
                invalidate()
            }
            doOnEnd {
                currentProgress = targetProgress
                updatePaintColors()
                invalidate()
            }
            start()
        }
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        super.onDetachedFromWindow()
    }
}