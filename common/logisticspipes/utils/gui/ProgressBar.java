package logisticspipes.utils.gui;

import com.sun.javaws.exceptions.InvalidArgumentException;
import lombok.SneakyThrows;

import logisticspipes.utils.Color;

public class ProgressBar {
	private float currentProgress = 0;
	private final int xPos;
	private final int yPos;
	private final int width;
	private final int height;

	public ProgressBar(int xPos, int yPos, int width, int height) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
	}

	public void render() {
		SimpleGraphics.drawRectNoBlend(xPos, yPos, xPos + width, yPos + height, Color.DARK_GREY, 0);
		SimpleGraphics.drawRectNoBlend(xPos, yPos, xPos + (int)(width * currentProgress), yPos + height - 1, Color.WHITE, 0);
	}

	@SneakyThrows(InvalidArgumentException.class)
	public void setProgress(float progress) {
		if(progress < 0 || progress > 1) throw new InvalidArgumentException(new String[]{"value blower than 0 or greater than 1"});
		this.currentProgress = progress;
	}
}
