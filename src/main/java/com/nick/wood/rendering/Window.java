package com.nick.wood.rendering;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class Window extends JFrame {

	private final RenderHandler renderHandler;
	private final Canvas canvas = new Canvas();
	private final BiConsumer<Integer, Integer> drawConsumer;
	private final Consumer<UnaryOperator<Double>> changeFpsConsumer;

	public Window(int width,
	              int height,
	              BiConsumer<Integer, Integer> drawConsumer,
	              Consumer<UnaryOperator<Double>> changeFpsConsumer) {

		this.drawConsumer = drawConsumer;
		this.changeFpsConsumer = changeFpsConsumer;

		setupBindings();

		this.renderHandler = new RenderHandler(width, height);

		initialiseWindow(width, height);

	}

	void setupBindings() {

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);

				renderHandler.changeScreenSize(getWidth(), getHeight());
			}
		});

		canvas.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				drawConsumer.accept(MouseInfo.getPointerInfo().getLocation().x-canvas.getLocationOnScreen().x,
						MouseInfo.getPointerInfo().getLocation().y-canvas.getLocationOnScreen().y);
			}
		});

		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				drawConsumer.accept(MouseInfo.getPointerInfo().getLocation().x-canvas.getLocationOnScreen().x,
						MouseInfo.getPointerInfo().getLocation().y-canvas.getLocationOnScreen().y);
			}
		});

		canvas.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == 'q') {
					changeFpsConsumer.accept(fps -> {
						double newFps = fps / 2;
						setTitle(newFps + ": q = slow down, w = speed up");
						return newFps;
					});
				} else if (e.getKeyChar() == 'w') {
					changeFpsConsumer.accept(fps -> {
						double newFps = fps * 2;
						setTitle(newFps + ": q = slow down, w = speed up");
						return newFps;
					});
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {

			}
		});

	}

	public void render(int[][] cellColors) {
		// get buffered strategy
		BufferStrategy bufferStrategy = canvas.getBufferStrategy();

		// get graphics from buffered strategy
		Graphics g = bufferStrategy.getDrawGraphics();

		// calls paint in JFrame we are extending. Is needed, says in javadoc
		// if this is not in other things aren't drawn that don't appear in this function
		// ie background
		super.paint(g);

		renderHandler.clear();

		renderHandler.renderPixelMatrix(cellColors);

		renderHandler.render(g);

		g.dispose();

		// tells that you are done with writing to the buffer and
		// puts it in the queue to be shown
		bufferStrategy.show();
	}

	void initialiseWindow(int width, int height) {
		this.setResizable(false);

		// TO close the frame when window is closed
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// sets starting position and size
		setBounds(0, 0, width, height);

		// puts the frame in the center of the screen
		setLocationRelativeTo(null);

		// adds canvas to jframe
		add(canvas);

		// sets window visible. needs to go above buffered strategy
		// so it has something to attach to
		setVisible(true);

		// creates buffers
		canvas.createBufferStrategy(4);
	}


}
