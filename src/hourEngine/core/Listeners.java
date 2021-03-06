package hourEngine.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


public class Listeners 
{
	public final class Keys implements KeyListener
	{
		public int count = 0;
		boolean on = false;
		boolean left = false;
		boolean up = false;
		boolean right = false;
		
		public boolean getLeft()
		{
			return left;
		}

		@Override
		public void keyPressed(KeyEvent arg0)
		{
			if(arg0.getKeyCode() == KeyEvent.VK_W)
			{
				up = true;
				on = true;
			}
			if(arg0.getKeyCode() == KeyEvent.VK_D)
			{
				right = true;
				on = true;
			}
			if(arg0.getKeyCode() == KeyEvent.VK_A)
			{
				left = true;
				on = true;
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0)
		{
			if(arg0.getKeyCode() == KeyEvent.VK_W)
			{
				up = false;
				count = 0;
			}
			if(arg0.getKeyCode() == KeyEvent.VK_D)
			{
				right = false;
				count = 1;
			}
			if(arg0.getKeyCode() == KeyEvent.VK_A)
			{
				left = false;
				count = 3;
			}
			on = false;
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		
	}
	
	public class Mouse implements MouseListener, MouseMotionListener
	{
		public int x, y;

		@Override
		public void mouseClicked(MouseEvent e) 
		{
			if(!Game.simulate)
			{
				//Game.simulate = true;
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseDragged(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseMoved(MouseEvent arg0)
		{
			x = arg0.getX();
			y = arg0.getY();
		}
		
	}
	
}
