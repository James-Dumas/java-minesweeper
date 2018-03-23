import java.util.ArrayList;

public class Board {
	private int size;
	private Tile[][] board;
	
	public Board(int size, double mineProb)
	{
		board = new Tile[size][size];
		this.size = size;
		
		// determine number of mines to place
		int mineNum = (int) (mineProb * size * size);
		mineNum += Math.random() * 8 - 4;
		
		// create a list and populate it with random (x, y) coordinate pairs
		ArrayList<int[]> minePos = new ArrayList<int[]>();
		boolean retry = false;
		while(minePos.size() < mineNum)
		{
			// get random coordinates
			int[] position = {
					(int)(Math.random() * size), 
					(int)(Math.random() * size)
					};
			
			// make sure those coordinates aren't already in the list
			for(int[] point: minePos)
			{
				if(point[0] == position[0] && point[1] == position[1])
				{
					retry = true;
					break;
				}
			}
			
			if(retry)
			{
				retry = false;
			}
			else
			{
				minePos.add(position);
			}
		}
		
		// fill board with tiles, placing mines at the previously determined points
		for(int y = 0; y < size; y++)
		{
			for(int x = 0; x < size; x++)
			{
				Tile newTile;
				boolean isMineHere = false;
				for(int[] point: minePos)
				{
					if(point[0] == x && point[1] == y)
					{
						isMineHere = true;
						break;
					}
				}
				
				if(isMineHere)
				{
					newTile = new Mine();
				}
				else
				{
					newTile = new Tile();
				}
				
				board[y][x] = newTile;
			}
		}
		
		// calculate the value of each tile (number of mines around)
		for(int y = 0; y < size; y++)
		{
			for(int x = 0; x < size; x++)
			{
				int sum = 0;
				
				for(int k = y - 1; k < y + 2; k++)
				{
					for(int h = x - 1; h < x + 2; h++)
					{
						if(k >= 0 && k < size && h >= 0 && h < size && board[k][h].get_value() == -1)
						{
							sum++;
						}
					}
				}
				
				board[y][x].set_value(sum);
			}
		}
	}
	
	public void uncover_tile(int x, int y)
	{
		// if the tile is a zero, recursively uncover all neighboring tiles
		// otherwise, just uncover the tile
		
		Tile tileHere = board[y][x];
		tileHere.uncover();
		if(tileHere.get_value() == 0)
		{
			for(int k = y - 1; k < y + 2; k++)
			{
				for(int h = x - 1; h < x + 2; h++)
				{
					if(k >= 0 && k < size && h >= 0 && h < size && !board[k][h].is_uncovered())
					{
						this.uncover_tile(h, k);
					}
				}
			}
		}
	}
	
	public boolean is_complete()
	{
		// check if the board is completed (all the mines are flagged)

		boolean complete = true;
		for(int y = 0; y < size; y++)
		{
			for(int x = 0; x < size; x++)
			{
				Tile tileHere = board[y][x];
				if((tileHere.get_value() == -1) != tileHere.is_flagged())
				{
					complete = false;
					break;
				}
			}
		}
		
		return complete;
	}
	
	public Tile get_tile(int x, int y)
	{
		return board[y][x];
	}
	
	public int get_size()
	{
		return size;
	}
	
	public boolean is_flagged(int x, int y)
	{
		return board[y][x].is_flagged();
	}
	
	public void set_flag(int x, int y, boolean value)
	{
		board[y][x].set_flag(value);
	}
}
