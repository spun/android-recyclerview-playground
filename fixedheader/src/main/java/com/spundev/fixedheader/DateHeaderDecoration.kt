package com.spundev.fixedheader

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.StaticLayout
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.graphics.withTranslation
import androidx.core.text.inSpans
import androidx.core.view.ViewCompat
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.RecyclerView
import com.spundev.fixedheader.model.FixHeaderItemModel
import java.util.*

class DateHeaderDecoration(
    context: Context,
    sessions: List<FixHeaderItemModel>
) : RecyclerView.ItemDecoration() {

    private val paint: TextPaint
    private val width: Int
    private val padding: Int
    private val monthTextSize: Int
    private val dayTextSize: Int

    private val monthTextSizeSpan: AbsoluteSizeSpan
    private val dayTextSizeSpan: AbsoluteSizeSpan
    private val boldSpan = StyleSpan(Typeface.BOLD)

    init {
        val attrs = context.obtainStyledAttributes(
            R.style.fix_DateHeaders,
            R.styleable.fix_DateHeader
        )
        paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = attrs.getColorOrThrow(R.styleable.fix_DateHeader_android_textColor)
            try {
                typeface = ResourcesCompat.getFont(
                    context,
                    attrs.getResourceIdOrThrow(R.styleable.fix_DateHeader_android_fontFamily)
                )
            } catch (_: Exception) {
                // ignore
            }
        }
        width = attrs.getDimensionPixelSizeOrThrow(R.styleable.fix_DateHeader_android_width)
        padding = attrs.getDimensionPixelSize(R.styleable.fix_DateHeader_android_padding, 0)
        monthTextSize =
            attrs.getDimensionPixelSizeOrThrow(R.styleable.fix_DateHeader_fix_monthTextSize)
        dayTextSize = attrs.getDimensionPixelSizeOrThrow(R.styleable.fix_DateHeader_fix_dayTextSize)
        attrs.recycle()

        monthTextSizeSpan = AbsoluteSizeSpan(monthTextSize)
        dayTextSizeSpan = AbsoluteSizeSpan(dayTextSize)
    }

    // Get the sessions date time and create header layouts for each
    private val timeSlots: Map<Int, StaticLayout> =
        indexSessionHeaders(sessions).map {
            it.first to createHeader(it.second)
        }.toMap()


    private fun indexSessionHeaders(sessions: List<FixHeaderItemModel>): List<Pair<Int, Calendar>> {
        return sessions
            .mapIndexed { index, session ->
                index to session.calendar
            }
            .distinctBy {
                // Only valid if all dates are within the same year
                it.second.get(Calendar.DAY_OF_YEAR)
            }
    }

    private fun isRtl(view: View) =
        ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (timeSlots.isEmpty() || parent.isEmpty()) return

        val isRtl = isRtl(parent)
        if (isRtl) {
            c.save()
            c.translate((parent.width - width).toFloat(), 0f)
        }

        val parentPadding = parent.paddingTop

        var earliestPosition = Int.MAX_VALUE
        var previousHeaderPosition = -1
        var previousHasHeader = false
        var earliestChild: View? = null
        for (i in parent.childCount - 1 downTo 0) {
            val child = parent.getChildAt(i)
            if (child == null) {
                // This should not be null, but observed null at times.
                // Guard against it to avoid crash and log the state.
                Log.w(
                    "DateHeaderDecoration",
                    """View is null. Index: $i, childCount: ${parent.childCount},
                        |RecyclerView.State: $state""".trimMargin()
                )
                continue
            }

            if (child.y > parent.height || (child.y + child.height) < 0) {
                // Can't see this child
                continue
            }

            val position = parent.getChildAdapterPosition(child)
            if (position < 0) {
                continue
            }
            if (position < earliestPosition) {
                earliestPosition = position
                earliestChild = child
            }

            val header = timeSlots[position]
            if (header != null) {
                drawHeader(c, child, parentPadding, header, child.alpha, previousHasHeader)
                previousHeaderPosition = position
                previousHasHeader = true
            } else {
                previousHasHeader = false
            }
        }

        if (earliestChild != null && earliestPosition != previousHeaderPosition) {
            // This child needs a sticky header
            findHeaderBeforePosition(earliestPosition)?.let { stickyHeader ->
                previousHasHeader = previousHeaderPosition - earliestPosition == 1
                drawHeader(c, earliestChild, parentPadding, stickyHeader, 1f, previousHasHeader)
            }
        }

        if (isRtl) {
            c.restore()
        }
    }

    private fun findHeaderBeforePosition(position: Int): StaticLayout? {
        for (headerPos in timeSlots.keys.reversed()) {
            if (headerPos < position) {
                return timeSlots[headerPos]
            }
        }
        return null
    }


    private fun drawHeader(
        canvas: Canvas,
        child: View,
        parentPadding: Int,
        header: StaticLayout,
        headerAlpha: Float,
        previousHasHeader: Boolean
    ) {
        val childTop = child.y.toInt()
        val childBottom = childTop + child.height
        var top = (childTop + padding).coerceAtLeast(parentPadding)
        if (previousHasHeader) {
            top = top.coerceAtMost(childBottom - header.height - padding)
        }
        paint.alpha = (headerAlpha * 255).toInt()
        canvas.withTranslation(y = top.toFloat()) {
            header.draw(canvas)
        }
    }

    /**
     * Create a header layout for the given [date].
     */
    private fun createHeader(date: Calendar): StaticLayout {
        val text = SpannableStringBuilder().apply {
            inSpans(monthTextSizeSpan) {
                append(date.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US))
            }
            append("\n")
            inSpans(dayTextSizeSpan, boldSpan) {
                append(date.get(Calendar.DAY_OF_MONTH).toString())
            }
        }
        return newStaticLayout(text, paint, width)
    }

    private fun newStaticLayout(
        source: CharSequence,
        paint: TextPaint,
        width: Int,
        alignment: Layout.Alignment = Layout.Alignment.ALIGN_CENTER,
        spacingMultiplier: Float = 1f,
        spacingAdd: Float = 0f,
        includePad: Boolean = false
    ): StaticLayout {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(source, 0, source.length, paint, width).apply {
                setAlignment(alignment)
                setLineSpacing(spacingAdd, spacingMultiplier)
                setIncludePad(includePad)
            }.build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(
                source,
                paint,
                width,
                alignment,
                spacingMultiplier,
                spacingAdd,
                includePad
            )
        }
    }
}