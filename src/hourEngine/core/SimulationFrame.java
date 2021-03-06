package hourEngine.core;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.dyn4j.dynamics.World;

import hourEngine.menu.Button;
import hourEngine.menu.Menu;
import hourEngine.menu.Menus;

/**
 * A simple scene of a bowling ball bouncing on the floor.
 * <p>
 * Primarily used to illustrate the computation of the mass and size
 * of the ball.  See the {@link WheelJointTesting#initializeWorld()} method.
 * @author William Bittle
 * @since 3.2.0
 * @version 3.2.0
 */
public abstract class SimulationFrame extends JFrame {
	/** The serial version id */
	private static final long serialVersionUID = 7659608187025022915L;

	/** The conversion factor from nano to base */
	public static final double NANO_TO_BASE = 1.0e9;

	/** The canvas to draw to */
	protected final Canvas canvas;
	
	/** The dynamics engine */
	protected final World world;
	
	/** The pixels per meter scale factor */
	protected final double scale;
	
	/** True if the simulation is exited */
	private boolean stopped;
	
	/** True if the simulation is paused */
	private boolean paused;
	
	/** The time stamp for the last iteration */
	private long last;
	
	public static boolean simulate = false;
	
	public Menus ms;
	
	public void AddMenus()
	{
		Menu m = new Menu()
		{
			Button b;
			@Override
			public void init()
			{
				s = "main";
				Image button = Game.tl.textureFromName("start");
				b = new Button((canvas.getWidth()/2) - ((button.getWidth(null)*3)/2),200,button.getWidth(null)*3,button.getHeight(null)*3, button,Game.tl.textureFromName("startc"),Game.tl.textureFromName("startm"));
				Add(b);
				//Add(c);
			}

			@Override
			public void loop()
			{
				if(b.getClicked())
				{
					//System.out.println("ye");
					simulate = true;
					s = "main";
				}
			}
			String s = "main";

			@Override
			public String getTarget()
			{
				return s;
			}

			@Override
			public void renderBackground(Graphics2D g) {
				// TODO Auto-generated method stub
				
			}			
		};

		Menu m2 = new Menu()
		{
			Button b;
			Button c;
			@Override
			public void init()
			{
				s = "main2";
				b = new Button(canvas.getWidth()/2 - ((Game.tl.textureFromName("start").getWidth(null)*3)/2),300,Game.tl.textureFromName("start").getWidth(null)*3,Game.tl.textureFromName("start").getHeight(null)*3, Game.tl.textureFromName("start"),Game.tl.textureFromName("startc"),Game.tl.textureFromName("startm"));
				c = new Button(150,210,25,25);
				Add(b);
				Add(c);
			}

			@Override
			public void loop()
			{
				//System.out.println("ye2");
				if(b.getClicked())
				{
					s = "main";
					System.out.println("ye2");
				}
				if(c.getClicked())
				{
					simulate = true;
				}
			}
			String s = "main2";
			@Override
			public String getTarget() {
				// TODO Auto-generated method stub
				return s;
			}

			@Override
			public void renderBackground(Graphics2D g) {
				// TODO Auto-generated method stub
				
			}
		};
		ms.addMenu("main", m);
		ms.addMenu("main2", m2);
	}
	
	/**
	 * Constructor.
	 * <p>
	 * By default creates a 800x600 canvas.
	 * @param name the frame name
	 * @param scale the pixels per meter scale factor
	 */
	public SimulationFrame(String name, double scale) {
		super(name);
		ms = new Menus();

		this.scale = scale;
		
		// create the world
		this.world = new World();
		
		// setup the JFrame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// add a window listener
		this.addWindowListener(new WindowAdapter() {
			/* (non-Javadoc)
			 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				// before we stop the JVM stop the simulation
				stop();
				super.windowClosing(e);
			}
		});
		
		// create the size of the window
		Dimension size = new Dimension(800, 600);
		
		// create a canvas to paint to 
		this.canvas = new Canvas();
		this.canvas.setPreferredSize(size);
		this.canvas.setMinimumSize(size);
		this.canvas.setMaximumSize(size);

		// add the canvas to the JFrame
		this.add(this.canvas);
		
		// make the JFrame not resizable
		// (this way I dont have to worry about resize events)
		this.setResizable(false);
		
		// size everything
		this.pack();
		AddMenus();
		ms.init();
		ms.addMouseListeners(this.canvas);
		// setup the world
		this.initializeWorld();
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected abstract void initializeWorld();
	
	/**
	 * Start active rendering the simulation.
	 * <p>
	 * This should be called after the JFrame has been shown.
	 */
	private void start() {
		// initialize the last update time
		this.last = System.nanoTime();
		// don't allow AWT to paint the canvas since we are
		this.canvas.setIgnoreRepaint(true);
		// enable double buffering (the JFrame has to be
		// visible before this can be done)
		this.canvas.createBufferStrategy(2);
		// run a separate thread to do active rendering
		// because we don't want to do it on the EDT
		Thread thread = new Thread() {
			public void run() {
				// perform an infinite loop stopped
				// render as fast as possible
				while (!isStopped()) {
					gameLoop();
					// you could add a Thread.yield(); or
					// Thread.sleep(long) here to give the
					// CPU some breathing room
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {}
				}
			}
		};
		// set the game loop thread to a daemon thread so that
		// it cannot stop the JVM from exiting
		thread.setDaemon(true);
		// start the game loop
		thread.start();
	}
	
	/**
	 * The method calling the necessary methods to update
	 * the game, graphics, and poll for input.
	 */
	private void gameLoop() {
		// get the graphics object to render to
		
		
		// by default, set (0, 0) to be the center of the screen with the positive x axis
		// pointing right and the positive y axis pointing up
		
		
		// reset the view
		
		if(simulate)
		{
			Graphics2D g = (Graphics2D)this.canvas.getBufferStrategy().getDrawGraphics();
			this.clear(g);
			this.transform(g);
			// get the current time
	        long time = System.nanoTime();
	        // get the elapsed time from the last iteration
	        long diff = time - this.last;
	        // set the last time
	        this.last = time;
	    	// convert from nanoseconds to seconds
	    	double elapsedTime = (double)diff / NANO_TO_BASE;
			
			// render anything about the simulation (will render the World objects)
			this.render(g, elapsedTime);
	        
			if (!paused) {
		        // update the World
		        this.update(g, elapsedTime);
			}
			g.dispose();
		}
		else
		{
			Graphics2D g = (Graphics2D)this.canvas.getBufferStrategy().getDrawGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
			ms.loop(g);
			g.dispose();
		}
		
		// dispose of the graphics object
		
		
		// blit/flip the buffer
		BufferStrategy strategy = this.canvas.getBufferStrategy();
		if (!strategy.contentsLost()) {
			strategy.show();
		}
		
		// Sync the display on some systems.
        // (on Linux, this fixes event queue problems)
        Toolkit.getDefaultToolkit().sync();
	}

	/**
	 * Performs any transformations to the graphics.
	 * <p>
	 * By default, this method puts the origin (0,0) in the center of the window
	 * and points the positive y-axis pointing up.
	 * @param g the graphics object to render to
	 */
	protected void transform(Graphics2D g) {
		final int w = this.canvas.getWidth();
		final int h = this.canvas.getHeight();
		
		// before we render everything im going to flip the y axis and move the
		// origin to the center (instead of it being in the top left corner)
		AffineTransform yFlip = AffineTransform.getScaleInstance(1, -1);
		AffineTransform move = AffineTransform.getTranslateInstance(w / 2, -h / 2);
		g.transform(yFlip);
		g.transform(move);
	}
	
	/**
	 * Clears the previous frame.
	 * @param g the graphics object to render to
	 */
	protected void clear(Graphics2D g) {
		final int w = this.canvas.getWidth();
		final int h = this.canvas.getHeight();
		
		// lets draw over everything with a white background
		g.setColor(Color.WHITE);
		g.fillRect(-w / 2, -h / 2, w, h);
	}
	
	/**
	 * Renders the example.
	 * @param g the graphics object to render to
	 * @param elapsedTime the elapsed time from the last update
	 */
	protected void render(Graphics2D g, double elapsedTime) {
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// draw all the objects in the world
		for (int i = 0; i < this.world.getBodyCount(); i++) {
			// get the object
			SimulationBody body = (SimulationBody) this.world.getBody(i);
			this.render(g, elapsedTime, body);
		}
	}
	
	/**
	 * Renders the body.
	 * @param g the graphics object to render to
	 * @param elapsedTime the elapsed time from the last update
	 * @param body the body to render
	 */
	protected void render(Graphics2D g, double elapsedTime, SimulationBody body) {
		// draw the object
		body.render(g, this.scale);
	}
	
	/**
	 * Updates the world.
	 * @param g the graphics object to render to
	 * @param elapsedTime the elapsed time from the last update
	 */
	protected void update(Graphics2D g, double elapsedTime) {
        // update the world with the elapsed time
        this.world.update(elapsedTime);
	}
	
	/**
	 * Stops the simulation.
	 */
	public synchronized void stop() {
		this.stopped = true;
	}
	
	/**
	 * Returns true if the simulation is stopped.
	 * @return boolean true if stopped
	 */
	public boolean isStopped() {
		return this.stopped;
	}
	
	/**
	 * Pauses the simulation.
	 */
	public synchronized void pause() {
		this.paused = true;
	}
	
	/**
	 * Pauses the simulation.
	 */
	public synchronized void resume() {
		this.paused = false;
	}
	
	/**
	 * Returns true if the simulation is paused.
	 * @return boolean true if paused
	 */
	public boolean isPaused() {
		return this.paused;
	}
	
	/**
	 * Starts the simulation.
	 */
	public void run() {
		// set the look and feel to the system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		// show it
		this.setVisible(true);
		
		// start it
		this.start();
	}
}
