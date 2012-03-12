package edu.iu.sci2.visualization.bipartitenet.component;

import java.awt.Graphics2D;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;

import math.geom2d.Point2D;

public class MultiLineLabelPainter implements Paintable {
	private Point2D initialPosition;
	private float wrappingWidth;
	private AttributedCharacterIterator styledText;
	private final float lineSpacing;

	public MultiLineLabelPainter(Point2D initialPosition,
			AttributedCharacterIterator styledText, float wrappingWidth,
			float lineSpacing) {
		this.initialPosition = initialPosition;
		this.styledText = styledText;
		this.wrappingWidth = wrappingWidth;
		this.lineSpacing = lineSpacing;
	}

	@Override
	public void paint(Graphics2D g) {
		java.awt.geom.Point2D.Float pen = initialPosition.getAsFloat();
		LineBreakMeasurer measurer = new LineBreakMeasurer(styledText, g.getFontRenderContext());
		while (true) {
			TextLayout layout = measurer.nextLayout(wrappingWidth);
			if (layout == null)
				break;
			float dx = 0;
			if (! layout.isLeftToRight())
				dx = wrappingWidth - layout.getAdvance();
			layout.draw(g, pen.x + dx, pen.y + layout.getAscent());
			pen.y += (layout.getAscent() + layout.getDescent() + layout
					.getLeading()) * lineSpacing;
		}
	}

}
