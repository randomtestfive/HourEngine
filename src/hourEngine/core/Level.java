package hourEngine.core;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.dyn4j.dynamics.World;

import hourEngine.prefabs.level.Square;

public class Level
{
	public int xSize;
	public int ySize;
	private Map<Integer, Map<Integer, Integer>> collide = new LinkedHashMap<Integer, Map<Integer, Integer>>();
	private Map<Integer, Map<Integer, Integer>>	rotate = new LinkedHashMap<Integer, Map<Integer, Integer>>();
	private Map<Integer, Map<Integer, Integer>>	tileset = new LinkedHashMap<Integer, Map<Integer, Integer>>();
	public ArrayList<Tileset> tilesets = new ArrayList<Tileset>();
	public Level(int x, int y)
	{
		xSize = x;
		ySize = y;
	}
	
	public World addWorld(World in)
	{
		for(int x = 0; x < xSize; x++)
		{
			for(int y = 0; y < ySize; y++)
			{
				if(getCollide(x, y)!=0)
				{
					if(getCollide(x,y)==1)
					{
						Square tmp = new Square(tilesets.get(getTileset(x,y)).tiles.get(0), x, -y);
						in.addBody(tmp);
					}
				}
			}
		}
		return in;
	}
	
	public void setCollide(int x, int y, int val)
	{
		collide = setProperty(x, y, val, collide);
	}
	
	public void setRotate(int x, int y, int val)
	{
		rotate = setProperty(x, y, val, rotate);
	}
	
	public void setTileset(int x, int y, int val)
	{
		tileset = setProperty(x, y, val, tileset);
	}
	
	private Map<Integer, Map<Integer, Integer>> setProperty(int x, int y, int val, Map<Integer, Map<Integer, Integer>> map)
	{
		if(x >= xSize || y >= ySize || x < 0 || y < 0)
		{
			return map;
		}
		if(map.get(x)!=null)
		{
			map.get(x).put(y, val);
		}
		else
		{
			map.put(x, new LinkedHashMap<Integer, Integer>());
			map.get(x).put(y, val);
		}
		return map;
	}
	
	private int getProperty(int x, int y, Map<Integer, Map<Integer, Integer>> map)
	{
		if(x > xSize || y > ySize)
		{
			return 0;
		}
		
		if(map.get(x)!=null && map.get(x).get(y)!=null)
		{
			return map.get(x).get(y);
		}
		else if(map.get(x)==null)
		{
			map.put(x, new LinkedHashMap<Integer, Integer>());
			map.get(x).put(y, 0);
			return map.get(x).get(y);
		}
		else
		{
			map.get(x).put(y, 0);
			return map.get(x).get(y);
		}
	}
	
	public void setSize(int x, int y)
	{
		xSize = x;
		ySize = y;
		for(Map.Entry<Integer, Map<Integer, Integer>> m : collide.entrySet())
		{
			if(m.getKey() >= xSize)
			{
				collide.put(m.getKey(), new LinkedHashMap<Integer, Integer>());
			}
			for(Map.Entry<Integer, Integer> m2 : m.getValue().entrySet())
			{
				if(m2.getKey() >= ySize)
				{
					collide.get(m.getKey()).put(m2.getKey(), 0);
				}
			}
		}
	}
	
	public int getCollide(int x, int y)
	{
		return getProperty(x, y, collide);
	}
	
	public int getRotate(int x, int y)
	{
		return getProperty(x, y, rotate);
	}
	
	public int getTileset(int x, int y)
	{
		return getProperty(x, y, tileset);
	}
	
	public static Level readFromFile(File file)
	{
		Level l = null;
		try (BufferedReader br = new BufferedReader(new FileReader(file)))
		{
			int tilesets = 0;
		    String line;
		    if((line = br.readLine()) != null)
		    {
		    	String size = line.split(":")[0];
		    	String ts = line.split(":")[1];
		    	l = new Level(Integer.parseInt(size.split("x")[0]), Integer.parseInt(size.split("x")[1]));
		    	tilesets = Integer.parseInt(ts);
		    }
		    ArrayList<Tileset> tiletmp = new ArrayList<Tileset>();
		    String[] names = new String[tilesets];
		    for(int i = 0; i < tilesets; i++)
		    {
		    	if((line = br.readLine()) != null)
		    	{
		    		File tmpfile = new File(file.getParent() + "/" + line + ".png");
		    		BufferedImage in = ImageIO.read(tmpfile);
					tiletmp.add(new Tileset(in, line));
					names[i] = line;
					
		    	}
		    }
		    l.tilesets = tiletmp;
		    int y = 0;
		    while ((line = br.readLine()) != null)
		    {
		    	int x = 0;
		    	for (String in : line.split(","))
		    	{
		    		int tmp1 = Integer.parseInt(in.split(":")[0]);
		    		int tmp2 = Integer.parseInt(in.split(":")[1]);
		    		int tmp3 = Integer.parseInt(in.split(":")[2]);
		    		l.setCollide(x, y, tmp1);
		    		l.setRotate(x, y, tmp2);
		    		l.setTileset(x, y, tmp3);
		    		x++;
		    	}
		    	y++;
		    }
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return l;
	}
}
